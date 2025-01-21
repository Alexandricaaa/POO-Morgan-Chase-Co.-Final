package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import org.poo.fileio.*;
import org.poo.utils.Utils;

import java.util.*;
import java.util.stream.IntStream;

@Data
public class Bank {

    private ArrayList<Commerciant> commerciants = new ArrayList<>();
    private ArrayList<Exchange> exchanges = new ArrayList<>();
    private Map<String, User> users = new LinkedHashMap<>();
    private Map<String, ArrayList<Commerciant>> commerciantsPerAcc = new HashMap<>();
    private Map<String, String> accountAlias = new HashMap<>();
    private Map<String, List<User>> businessUsersPerAcc = new LinkedHashMap<>();
    private Map<List<Transaction>, Boolean> transactionsList =  new HashMap<>();

    public Bank(final ObjectInput input) {
        Utils.resetRandom();

        for (CommerciantInput commerciant : input.getCommerciants()) {
            this.commerciants.add(new Commerciant(commerciant));
        }

        for (UserInput user : input.getUsers()) {
            User myUser = new User(user);
            users.put(myUser.getEmail(), myUser);
        }

        for (ExchangeInput exchange : input.getExchangeRates()) {
            Exchange myExchange = new Exchange(exchange);
            exchanges.add(myExchange);
        }
    }

    public void processCommand(final CommandInput cmd,
                               final ArrayNode output,
                               final ObjectMapper obj) {
        CommandPattern command = Factory.createCommand(cmd);
        if (command != null) {
            command.execute(cmd, obj, output, this);
        }
    }

    public Account findAccount(final User user, final String iban) {
        for (Account account : user.getAccounts()) {
            if (account.getAccount().equals(iban)) {
                return account;
            }
        }
        return null;
    }

    public Account findAccountByIBAN(final String iban) {
        for (User u : this.getUsers().values()) {
            for (Account acc : u.getAccounts()) {
                if (acc.getAccount().equals(iban)) {
                    return acc;
                }
            }
        }
        return null;
    }

    public String getEmailForAccountIBAN(final String iban) {
        for (User u : users.values()) {
            for (Account acc : u.getAccounts()) {
                if (acc.getAccount().equals(iban)) {
                    return u.getEmail();
                }
            }
        }
        return null;
    }

    public Account getAccountUsingCardNumber(final String cardNumber) {
        for (User u : users.values()) {
            for (Account acc : u.getAccounts()) {
                for (Card c : acc.getCards()) {
                    if (c.getCardNumber().equals(cardNumber)) {
                        return acc;
                    }
                }
            }
        }
        return null;
    }

    public boolean isMyAccount(final User user, final String iban) {
        for (Account acc : user.getAccounts()) {
            if (acc.getAccount().equals(iban)) {
                return true;
            }
        }
        return false;
    }

    public static Card findCardInAccount(final Account account, final CommandInput command) {
        return account.getCards().stream()
                .filter(card -> card.getCardNumber().equals(command.getCardNumber()))
                .findFirst()
                .orElse(null);
    }

    public static boolean isUpgrade(final String currentPlan, final String newPlan) {
        List<String> plans = Arrays.asList("standard", "student", "silver", "gold");

        int currentIndex = IntStream.range(0, plans.size())
                .filter(i -> plans.get(i).equalsIgnoreCase(currentPlan))
                .findFirst()
                .orElse(-1);

        int newIndex = IntStream.range(0, plans.size())
                .filter(i -> plans.get(i).equalsIgnoreCase(newPlan))
                .findFirst()
                .orElse(-1);

        return newIndex > currentIndex;
    }

    public void updateAccountPlan(final User user, final String newPlan) {
        user.getAccounts().forEach(account -> account.setPlanType(newPlan));
    }

    public static List<Transaction> findTransactionList(final Map<List<Transaction>,
            Boolean> transactionsList, final Transaction t) {

        for (Map.Entry<List<Transaction>, Boolean> entry : transactionsList.entrySet()) {
            List<Transaction> transactions = entry.getKey();

            if (transactions.contains(t)) {
                return transactions;
            }
        }
        return null;
    }
}
