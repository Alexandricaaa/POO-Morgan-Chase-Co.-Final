package org.poo.bank;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class Account {

    private String account;
    private double balance;
    private String planType;
    private String accountType;
    private double interestRate;
    private double minimumBalance;
    private String currency;
    private String alias;
    private double depositLimit;
    private double spendingLimit;
    private int goldUpdate = 0;
    private double split = 0.0;
    private boolean accepted;

    private ArrayList<Card> cards = new ArrayList<>();
    private Map<Commerciant, Integer> numberOfTransactions = new HashMap<>();
    private ArrayList<Commerciant> commerciants = new ArrayList<>();
    private double thresholdAmount = 0.0;

    private Map<Double, Boolean> isDiscountUsed = new HashMap<>();
    private Map<Transaction, Double> businessSpendings = new LinkedHashMap<>();
    private Map<Transaction, Double> businessDeposit = new LinkedHashMap<>();
    private List<User> businessUsers = new ArrayList<>();
    private static final int BUSINESS_LIMIT = 500;

    public Account() {
        this.accepted = false;
    }

    //deep copy
    public Account(final Account a) {
        this.account = a.account;
        this.balance = a.balance;
        this.accountType = a.accountType;
        this.cards = new ArrayList<>();
        this.cards.addAll(a.cards);
    }

    public Account(final CommandInput input) {
        account = Utils.generateIBAN();
        balance = 0.0;
        currency = input.getCurrency();
        accountType = input.getAccountType();
        planType = "standard";
        this.accepted = false;
    }

    /**
     * Sets the plan Type to an account when it is created
     */
    public static void planType(final Account account, final User user) {
        String plan = null;
        for (Account a : user.getAccounts()) {
            if (a.getPlanType() != null) {
                plan = a.getPlanType();
                break;
            }
        }

        if (plan != null) {
            account.setPlanType(plan);
        } else {
            if (user.getOccupation().equals("student")) {
                account.setPlanType("student");
            } else {
                account.setPlanType("standard");
            }
        }
    }

    /**
     * Type of account
     */
    public static void configureAccountByType(final Bank bank,
                                              final Account account,
                                              final User user,
                                              final CommandInput cmd) {
        switch (cmd.getAccountType()) {
            case "savings":
                account.setInterestRate(cmd.getInterestRate());
                break;
            case "business":
                configureBusinessAccount(bank, account, user, cmd);
                break;

            default:
                break;
        }
    }

/**
 * Configures the business account for a user
 **/
    public static void configureBusinessAccount(final Bank bank,
                                                final Account account,
                                                final User user,
                                                final CommandInput cmd) {
        Exchange exchange = new Exchange(bank);
       if (user.getPlan() != null) {
           account.setPlanType(user.getPlan());
       }
        user.getEmployeeRole().put(account.getAccount(), "owner");

        double depositLimit = exchange.findExchangeRate("RON", cmd.getCurrency()) * BUSINESS_LIMIT;
        account.setDepositLimit(depositLimit);
        account.setSpendingLimit(depositLimit);
    }
}

