package org.poo.bank;

import org.poo.fileio.CommandInput;

import java.util.HashMap;
import java.util.Map;

public class Payment {

    private static final double STUDENT_TO_GOLD = 350;
    private static final double STUDENT_TO_SILVER = 100;
    private static final double SILVER_TO_GOLD = 250;

    public static double commission(String planType, final double amount) {
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

    public static double threshold(final double sum, final User user, final Account account) {
        double ret = 0;
        String plan = account.getPlanType();

        if (account.getAccountType().equals("business")) {
            plan = account.getPlanType();
        }

        if (plan == null) {
            return 0.0;
        }
        switch (plan.toLowerCase()) {
            case "standard":
            case "student":
                if (sum >= 100 && sum < 300) {
                    ret = 0.001;
                } else if (sum >= 300 && sum < 500) {
                    ret = 0.002;
                } else if (sum >= 500) {
                    ret = 0.0025;
                }
                break;

            case "silver":
                if (sum >= 100 && sum < 300) {
                    ret = 0.003;
                } else if (sum >= 300 && sum < 500) {
                    ret = 0.004;
                } else if (sum >= 500) {
                    ret = 0.005;
                }
                break;

            case "gold":
                if (sum >= 100 && sum < 300) {
                    ret = 0.005;
                } else if (sum >= 300 && sum < 500) {
                    ret = 0.0055;
                } else if (sum >= 500) {
                    ret = 0.007;
                }
                break;
            default:
                ret = 0;
                break;
        }
        return ret;
    }

    public  void calculateNumberOfTransactions(final Account account, final Commerciant commerciant) {
        if (account.getNumberOfTransactions() != null && account.getNumberOfTransactions().containsKey(commerciant)) {
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

    public static double cashback(final CommandInput command, final Commerciant commerciant,
                                  final Bank bank, final Account account) {
        Exchange exchange = new Exchange(bank);
        User user = bank.getUsers().get(bank.getEmailForAccountIBAN(account.getAccount()));
        Commerciant c = Commerciant.findCommerciant(command, bank, account);
        double rate = 0.0;
        double amount = 0.0;
        if (c != null) {
            if(c.getCashbackStrategy().equals("spendingThreshold")){
                rate = exchange. findExchangeRate(command.getCurrency(), account.getCurrency());
                amount = threshold(account.getThresholdAmount(), user, account ) * command.getAmount();
                return amount * rate;
            }
        }
        return 0.0;
    }

    public static  double getUpgradeFee(final String currentPlan, final String newPlan) {
        Map<String, Double> upgradeFees = new HashMap<>();
        upgradeFees.put("standard_to_silver", STUDENT_TO_SILVER);
        upgradeFees.put("student_to_silver", STUDENT_TO_SILVER);
        upgradeFees.put("silver_to_gold", SILVER_TO_GOLD);
        upgradeFees.put("standard_to_gold", STUDENT_TO_GOLD);
        upgradeFees.put("student_to_gold", STUDENT_TO_GOLD);

        String key = currentPlan.toLowerCase() + "_to_" + newPlan.toLowerCase();
        return upgradeFees.getOrDefault(key, 0.0);
    }
}
