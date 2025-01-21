package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.UserInput;
import org.poo.fileio.ExchangeInput;
import org.poo.utils.Utils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.stream.IntStream;


/**
 * Handles the entry point logic for my Bank Project
 */
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
/**
 * Processes the given command by creating the corresponding
 * command object using the factory,
 * and then executing the command if it is valid.
 **/
    public void processCommand(final CommandInput cmd,
                               final ArrayNode output,
                               final ObjectMapper obj) {
        CommandPattern command = Factory.createCommand(cmd);
        if (command != null) {
            command.execute(cmd, obj, output, this);
        }
    }

    /**
     *
     * @param user
     * @param iban
     * @return
     */
    public Account findAccount(final User user, final String iban) {
        for (Account account : user.getAccounts()) {
            if (account.getAccount().equals(iban)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Finds the instance of an Account using IBAN
     * @param iban
     * @return
     */
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

    /**
     * finds the email of the user
     */
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

    /**
     * Finds the account based on given card number
     */
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

    /**
     *
     * @param user
     * @param iban
     * @return
     */
    public boolean isMyAccount(final User user, final String iban) {
        for (Account acc : user.getAccounts()) {
            if (acc.getAccount().equals(iban)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a card in the card list associated with the account
     */
    public static Card findCardInAccount(final Account account, final CommandInput command) {
        return account.getCards().stream()
                .filter(card -> card.getCardNumber().equals(command.getCardNumber()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifies if the Upgrade command can be processed, it cannot be done a
     * downgrade to the current plan
     */
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

    /**
     * updates the plan
     * @param user
     * @param newPlan
     */
    public void updateAccountPlan(final User user, final String newPlan) {
        user.getAccounts().forEach(account -> account.setPlanType(newPlan));
    }

    /**
     * Finds a Transaction List in a Map
     */
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
