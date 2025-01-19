package org.poo.bank;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Data
public class Account {

    private String account;
    private double balance;
    private String planType;  // silver gold
    private String accountType;  // daca e standard business etc
    private double interestRate;
    private double minimumBalance;
    private double thresholdSpent;
    private String currency;
    private String alias;
    //private String role;

    private double depositLimit;
    private double spendingLimit;

    private int goldUpdate = 0;
    private double split = 0.0;

    private boolean accepted;

    private ArrayList<Card> cards = new ArrayList<>();


    private Map<Commerciant, Integer> numberOfTransactions = new HashMap<>();

    private ArrayList<Commerciant> commerciants = new ArrayList<>();
    private double thresholdAmount = 0.0;

    //double = discountul, boolean daca a fost folosit sau nu
    private Map<Double, Boolean> isDiscountUsed = new HashMap<>();


    public Account(){
        this.accepted = false;
    }

    //deep copy
    public Account(Account a){
        this.account = a.account;
        this.balance = a.balance;
        this.accountType = a.accountType;
        this.cards = new ArrayList<>();
        for(Card card : a.cards){
            this.cards.add(card);
        }
    }

    public Account(CommandInput input){
        account = Utils.generateIBAN();
        balance = 0.0;
        currency = input.getCurrency();
        accountType = input.getAccountType();
        planType = "standard";
        this.accepted = false;
    }



    public void initializeTransactions(ArrayList<Commerciant> commerciants) {
        for (Commerciant commerciant : commerciants) {
            numberOfTransactions.put(commerciant, 0);
        }
    }


    public static void PlanType(Account account, User user) {
        String plan = null;
        for(Account a : user.getAccounts()){
            if(a.getPlanType() != null){
                plan = a.getPlanType();
                break;
            }
        }
        if (plan != null) {
            account.setPlanType(plan);
        } else {
            if (user.getOccupation().equals("student")) {
                account.setPlanType("student");
            }
        }
    }

    public static void configureAccountByType(Bank bank, Account account, User user, CommandInput cmd) {
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

    public static void configureBusinessAccount(Bank bank, Account account, User user, CommandInput cmd) {
        //user.getRole().put(account.getIBAN(), "owner");

        Exchange exchange = new Exchange(bank);
        account.setPlanType(user.getPlan());
        user.getEmployeeRole().put(account.getAccount(), "owner");
        //account.getAssociates().add(user);


        double depositLimit = exchange.findExchangeRate("RON", cmd.getCurrency()) * 500;
        account.setDepositLimit(depositLimit);
        account.setSpendingLimit(depositLimit); // Același calcul pentru spending limit
    }

    @Override
    public String toString() {
        // Nu pune referințe la utilizatori, doar informațiile relevante ale contului
        return "Account{" +
                "accountNumber='" + account + '\'' +
                '}';
    }
}

