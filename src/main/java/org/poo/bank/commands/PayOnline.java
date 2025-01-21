package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.Map;

public class PayOnline implements CommandPattern {
    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        Payment payment = new Payment();
        if(command.getAmount() == 0){
            return;
        }
        Exchange exchange = new Exchange(bank);
        User user = bank.getUsers().get(command.getEmail());
        Account account = bank.findAccountByCardNumber(command.getCardNumber());
        boolean card = false;
        Card copyCard = null;
        boolean isMyAccount = false;

        if (account != null) {
            for (Account a : user.getAccounts()) {
                if (account.getAccount().equals(a.getAccount())) {
                    card = true;
                    isMyAccount = bank.isMyAccount(user, account.getAccount());
                    break;
                }
            }
        }

        if (account == null || !card || (!isMyAccount)) {
            Bank.addCommandNode(output, obj, "payOnline",
                    "Card not found", command.getTimestamp());
            return;
        }

        for (Card c : account.getCards()) {
            if (c.getCardNumber().equals(command.getCardNumber())) {
                copyCard = c;
                if (c.getStatus().equals("destroyed")) {
                    Transaction.error(command, user, "Card has already been used");
                    Bank.addCommandNode(output, obj, "payOnline",
                            "Card not found", command.getTimestamp());
                    return;
                }
            }
        }

        Commerciant commerciant = Commerciant.findCommerciant(command, bank, account);
        // Verificăm dacă comerciantul nu există deja în mapa numberOfTransactions
        //account.getNumberOfTransactions().putIfAbsent(commerciant, 0);

        Commerciant.incrementNumOfTr(command, bank, account);

        // Obține lista de comercianți pentru contul specific
        ArrayList<Commerciant> commerciants = bank.getCommerciantsPerAcc().get(account.getAccount());

// Verifică dacă lista există deja
        if (commerciants == null) {
            // Dacă nu există, creează o nouă listă și adaug-o în mapă
            commerciants = new ArrayList<>();
            bank.getCommerciantsPerAcc().put(account.getAccount(), commerciants);
        }

// Adaugă comerciantul în listă
        commerciants.add(commerciant);



        double discount = 0;
       // if(commerciant.getCashbackStrategy().equals("nrOfTransactions")) {
             //discount = payment.getDiscount(account, commerciant);

       // }
        for (Map.Entry<Double, Boolean> entry : account.getIsDiscountUsed().entrySet()) {
            if (entry.getValue()) {
                if(entry.getKey() == 2 && commerciant.getType().equals("Food")
                        || entry.getKey() == 5.0 && commerciant.getType().equals("Clothes")
                || entry.getKey() == 10.0 && commerciant.getType().equals("Tech")) {

                    entry.setValue(false);
                    discount = entry.getKey();
                    break;
                }

                 // Ieșim din buclă după ce găsim prima valoare true
            }
        }


        double cashback = Payment.cashback(command, commerciant, bank, account);

        double amountInCurrency = command.getAmount() * exchange.findExchangeRate(command.getCurrency(), account.getCurrency());
        double commissionInRON = Payment.commission(account.getPlanType(),
                command.getAmount() * exchange.findExchangeRate(command.getCurrency(), "RON"));
        //double rest = amountInCurrency - commissionInCurrency;
        double commissionInCurrency = exchange.findExchangeRate("RON", account.getCurrency()) * commissionInRON;
        double amountWithDiscount = amountInCurrency * discount /100;
        double total = amountInCurrency - amountWithDiscount - cashback + commissionInCurrency;

        System.out.println("cashback " + cashback);
        System.out.println("commission " + commissionInCurrency);
        System.out.println("total threshold " + account.getThresholdAmount());
        System.out.println("discount " + discount);
        System.out.println("total " + total);
        System.out.println("amount with discount " + amountWithDiscount);

        if (account.getAccountType().equals("business") && user.getEmployeeRole() != null && account.getAccount()!=null) {
            if (user.getEmployeeRole().get(account.getAccount()).equals("employee")) {
                if (total > account.getSpendingLimit()) {
                    if (!commerciant.getCashbackStrategy().equals("spendingThreshold")) {
                        if (account.getNumberOfTransactions().containsKey(commerciant)) {
                            // Obține valoarea curentă
                            int currentCount = account.getNumberOfTransactions().get(commerciant);
                            // Scade cu 1 dacă valoarea este mai mare decât 0
                            if (currentCount > 0) {
                                account.getNumberOfTransactions().put(commerciant, currentCount - 1);
                            }
                        }
                    }else{
                        double rate = exchange.findExchangeRate(command.getCurrency(), "RON");
                        double sumRON = command.getAmount() * rate;
                        account.setThresholdAmount(account.getThresholdAmount() - sumRON);
                    }
                    return;
                }
            }
        }

//        System.out.println("balance " + account.getBalance());
//        System.out.println("total " + total );
//        System.out.println("dif " + (account.getBalance() - total));

        if (account.getBalance() < total) {
         //   System.out.println("daca intra aici inseamna ca balance < total ");
            if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
                account.setThresholdAmount(account.getThresholdAmount() - command.getAmount() * exchange.findExchangeRate(command.getCurrency(), "RON"));
            } else {
                if (account.getNumberOfTransactions().containsKey(commerciant)) {
                    // Obține valoarea curentă
                    int currentCount = account.getNumberOfTransactions().get(commerciant);
                    // Scade cu 1 dacă valoarea este mai mare decât 0
                    if (currentCount > 0) {
                        account.getNumberOfTransactions().put(commerciant, currentCount - 1);
                    }
                }
            }
            Transaction.error(command, user, "Insufficient funds");
            return;
        }

       // System.out.println("new balance  " + (account.getBalance() - total ));


       payment.calculateNumberOfTransactions(account, commerciant);

        System.out.println("new baalnce " + (account.getBalance() - total));
        account.setBalance(account.getBalance() - total);

        Transaction.cardPayment(command, user,amountInCurrency, commerciant.getName() ,account.getAccount());

        double gold = command.getAmount() * exchange.findExchangeRate(command.getCurrency(), "RON");
        if (300 <= gold) {
            account.setGoldUpdate(account.getGoldUpdate() + 1);
        }
        if(account.getPlanType()!=null) {
            if (account.getGoldUpdate() >= 5 && account.getPlanType().equals("silver")) {
                account.setPlanType("gold");
                Transaction.upgradePlan(command, user, "gold", account.getAccount());
            }
        }

        if (account.getAccountType().equals("business")) {
            user.getSpendings().put(command.getTimestamp(), amountInCurrency);
            if (!bank.getCommerciantsPerAcc().containsKey(account.getAccount())) {
                // Dacă nu există, inițializează o nouă listă
                bank.getCommerciantsPerAcc().put(account.getAccount(), new ArrayList<>());
            }

            if (!user.getEmployeeRole().get(account.getAccount()).equals("owner")) {
                // Adaugă comerciantul în lista asociată contului
                bank.getCommerciantsPerAcc().get(account.getAccount()).add(commerciant);
            }
        }
        if(copyCard!=null){
            if(copyCard.isOneTime()){
                Transaction.card(command, user, copyCard, "The card has been destroyed", account.getAccount());
                copyCard.setStatus("destroyed");
                Card newOneTimeCard = new Card();
                newOneTimeCard.setOneTime(true);
                account.getCards().add(newOneTimeCard);
                Transaction.card(command,user, newOneTimeCard, "New card created", account.getAccount());
            }
        }
    }
}
