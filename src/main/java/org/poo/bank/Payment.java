package org.poo.bank;

import org.poo.fileio.CommandInput;

import java.util.HashMap;
import java.util.Map;

public class Payment {

    public static double commission(String planType, double amount) {
        if (planType == null) {
            return 0.0;
        }

        planType = planType.toLowerCase();

        switch (planType) {
            case "student", "gold" -> {
                return 0.0;
            }
            case "silver" -> {
                if (amount < 500.0) {
                    return 0.0;
                } else {
                    return amount * 0.001;
                }
            }
            case "standard" -> {
                return amount * 0.002;
            }
        }

        return 0.0;
    }

    public static double threshold(double sum, User user, Account account) {
        double ret = 0;
        String plan = account.getPlanType();

        // Dacă tipul contului este business, folosim planul acestuia
        if (account.getAccountType().equals("business")) {
            plan = account.getPlanType();
        }

        // Folosim un switch pentru a determina comisionul în funcție de tipul planului
        if(plan==null){
            return 0.0;
        }
        switch (plan.toLowerCase()) {
            case "standard":
            case "student":
                if (sum >= 100 && sum < 300) {
                    ret = 0.001; // 0.1%
                } else if (sum >= 300 && sum < 500) {
                    ret = 0.002; // 0.2%
                } else if (sum >= 500) {
                    ret = 0.0025; // 0.25%
                }
                break;

            case "silver":
                if (sum >= 100 && sum < 300) {
                    ret = 0.003; // 0.3%
                } else if (sum >= 300 && sum < 500) {
                    ret = 0.004; // 0.4%
                } else if (sum >= 500) {
                    ret = 0.005; // 0.5%
                }
                break;

            case "gold":
                if (sum >= 100 && sum < 300) {
                    ret = 0.005; // 0.5%
                } else if (sum >= 300 && sum < 500) {
                    ret = 0.0055; // 0.55%
                } else if (sum >= 500) {
                    ret = 0.007; // 0.7%
                }
                break;


            default:
                ret = 0; // În caz de plan necunoscut, nu se aplică comision
                break;
        }

        return ret;
    }


    public static void calculateNumberOfTransactions(Account account, Commerciant commerciant){
        if(account.getNumberOfTransactions()!=null) {
            int numOfTr = account.getNumberOfTransactions().get(commerciant);
            if (numOfTr == 2) {
                account.getIsDiscountUsed().put(2.0, true);
                return;
            }
            if (numOfTr == 5) {
                account.getIsDiscountUsed().put(5.0, true);
                return;
            }
            if (numOfTr == 10) {
                account.getIsDiscountUsed().put(10.0, true);
            }
        }
    }

    public static double getDiscount(Account account, Commerciant commerciant){
        calculateNumberOfTransactions(account, commerciant);
        String type = commerciant.getType();
        for (Map.Entry<Double, Boolean> entry : account.getIsDiscountUsed().entrySet()) {
            // Verificăm dacă valoarea este true
            if (entry.getValue()) {
                if((entry.getKey() == 2.0 && type.equals("Food"))
                || (entry.getKey() == 5.0 && type.equals("Clothes"))
                || (entry.getKey() == 10.0 && type.equals("Tech"))) {
                    account.getIsDiscountUsed().put(entry.getKey(), false);
                    return entry.getKey();
                }
            }
        }
        return 0;
    }

    public static double cashback(CommandInput command, Commerciant commerciant, Bank bank, Account account) {
        Exchange exchange = new Exchange(bank);
        User user = bank.getUsers().get(bank.findUserEmailByIBAN(account.getAccount()));
        Commerciant c = Commerciant.findCommerciant(command, bank, account);
        double rate = 0.0;
        double amount = 0.0;
        if(c!=null){
            if(c.getCashbackStrategy().equals("spendingThreshold")){
                rate = exchange. findExchangeRate(command.getCurrency(), account.getCurrency());
                amount = threshold(account.getThresholdAmount(), user, account ) * command.getAmount();
                return amount * rate;
            }
        }
        return 0.0;
    }

    public static  double getUpgradeFee(String currentPlan, String newPlan) {
        // Mapele pentru upgrade-uri
        Map<String, Double> upgradeFees = new HashMap<>();

        // Adăugăm taxele pentru upgrade
        upgradeFees.put("standard_to_silver", 100.0);
        upgradeFees.put("student_to_silver", 100.0);
        upgradeFees.put("silver_to_gold", 250.0);
        upgradeFees.put("standard_to_gold", 350.0);
        upgradeFees.put("student_to_gold", 350.0);

        // Formăm cheia pentru upgrade
        String key = currentPlan.toLowerCase() + "_to_" + newPlan.toLowerCase();

        // Returnăm taxa de upgrade dacă există, altfel 0
        return upgradeFees.getOrDefault(key, 0.0);
    }

}
