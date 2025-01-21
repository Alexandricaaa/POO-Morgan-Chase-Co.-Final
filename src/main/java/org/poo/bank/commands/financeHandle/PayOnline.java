package org.poo.bank.commands.financeHandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;
import java.util.ArrayList;
import java.util.Map;

/**
 * make a transaction to a commerciant
 */
public class PayOnline implements CommandPattern {
    private static final int GOLD = 300;
    private static final int DIVIDE = 100;

    /**
     *
     * @param command
     * @param obj
     * @param output
     * @param bank
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        Payment payment = new Payment();
        if (command.getAmount() == 0) {
            return;
        }
        Exchange exchange = new Exchange(bank);
        User user = bank.getUsers().get(command.getEmail());
        Account account = bank.getAccountUsingCardNumber(command.getCardNumber());
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
            Node.addCommandNode(output, obj, "payOnline",
                    "Card not found", command.getTimestamp());
            return;
        }
        for (Card c : account.getCards()) {
            if (c.getCardNumber().equals(command.getCardNumber())) {
                copyCard = c;
                if (c.getStatus().equals("destroyed")) {
                    BuildOneTransaction.error(command, user, "Card has already been used");
                    Node.addCommandNode(output, obj, "payOnline",
                            "Card not found", command.getTimestamp());
                    return;
                }
            }
        }
        Commerciant commerciant = Commerciant.findCommerciant(command, bank, account);
        Commerciant.incrementNumOfTr(command, bank, account);
        ArrayList<Commerciant> commerciants = bank.getCommerciantsPerAcc()
                .get(account.getAccount());

        if (commerciants == null) {
            commerciants = new ArrayList<>();
            bank.getCommerciantsPerAcc().put(account.getAccount(), commerciants);
        }
        commerciants.add(commerciant);
        double discount = 0;

        for (Map.Entry<Double, Boolean> entry : account.getIsDiscountUsed().entrySet()) {
            if (entry.getValue()) {
                if (entry.getKey() == 2 && commerciant.getType().equals("Food")
                        || entry.getKey() == (2.0 + 2.0 + 1.0)
                        && commerciant.getType().equals("Clothes")
                || entry.getKey() == (2.0 + 2.0 + 2.0 + 2.0 + 2.0)
                        && commerciant.getType().equals("Tech")) {

                    entry.setValue(false);
                    discount = entry.getKey();
                    break;
                }
            }
        }
        double cashback = Payment.cashback(command, commerciant, bank, account);
        double amountInCurrency = command.getAmount()
                * exchange.findExchangeRate(command.getCurrency(),
                account.getCurrency());
        double commissionInRON = Payment.commission(account.getPlanType(),
                command.getAmount() * exchange.findExchangeRate(command.getCurrency(), "RON"));
        double commissionInCurrency = exchange.
                findExchangeRate("RON", account.getCurrency()) * commissionInRON;
        double amountWithDiscount = amountInCurrency * discount / DIVIDE;
        double total = amountInCurrency - amountWithDiscount - cashback + commissionInCurrency;

        if (account.getAccountType().equals("business") && user.getEmployeeRole()
                != null && account.getAccount() != null) {
            if (user.getEmployeeRole().get(account.getAccount()).equals("employee")) {
                if (total > account.getSpendingLimit()) {
                    if (!commerciant.getCashbackStrategy().equals("spendingThreshold")) {
                        if (account.getNumberOfTransactions().containsKey(commerciant)) {

                            int currentCount = account.getNumberOfTransactions().get(commerciant);

                            if (currentCount > 0) {
                                account.getNumberOfTransactions().
                                        put(commerciant, currentCount - 1);
                            }
                        }
                    } else {
                        double rate = exchange.findExchangeRate(command.getCurrency(), "RON");
                        double sumRON = command.getAmount() * rate;
                        account.setThresholdAmount(account.getThresholdAmount() - sumRON);
                    }
                    return;
                }
            }
        }
        if (account.getBalance() < total) {
            if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
                account.setThresholdAmount(account.getThresholdAmount() - command.getAmount()
                        * exchange.findExchangeRate(command.getCurrency(), "RON"));
            } else {
                if (account.getNumberOfTransactions().containsKey(commerciant)) {
                    int currentCount = account.getNumberOfTransactions().get(commerciant);

                    if (currentCount > 0) {
                        account.getNumberOfTransactions().put(commerciant, currentCount - 1);
                    }
                }
            }
            BuildOneTransaction.error(command, user, "Insufficient funds");
            return;
        }
        payment.calculateNumberOfTransactions(account, commerciant);
        account.setBalance(account.getBalance() - total);

        BuildOneTransaction.cardPayment(command, user, amountInCurrency,
                commerciant.getName(), account.getAccount());
        double gold = command.getAmount() * exchange.findExchangeRate(command.getCurrency(), "RON");
        if (GOLD <= gold) {
            account.setGoldUpdate(account.getGoldUpdate() + 1);
        }
        if (account.getPlanType() != null) {
            if (account.getGoldUpdate() >= (2 + 2 + 1) && account.getPlanType().equals("silver")) {
                account.setPlanType("gold");
                BuildOneTransaction.upgradePlan(command, user, "gold", account.getAccount());
            }
        }
        if (account.getAccountType().equals("business")) {
            user.getSpendings().put(command.getTimestamp(), amountInCurrency);
            if (!bank.getCommerciantsPerAcc().containsKey(account.getAccount())) {
                bank.getCommerciantsPerAcc().put(account.getAccount(), new ArrayList<>());
            }
            if (!user.getEmployeeRole().get(account.getAccount()).equals("owner")) {
                bank.getCommerciantsPerAcc().get(account.getAccount()).add(commerciant);
            }
        }
        if (copyCard != null) {
            if (copyCard.isOneTime()) {
                BuildOneTransaction.card(command, user, copyCard,
                        "The card has been destroyed", account.getAccount());
                copyCard.setStatus("destroyed");
                Card newOneTimeCard = new Card();
                newOneTimeCard.setOneTime(true);
                account.getCards().add(newOneTimeCard);
                BuildOneTransaction.card(command, user, newOneTimeCard,
                        "New card created", account.getAccount());
            }
        }
    }
}
