package org.poo.bank;

import org.poo.fileio.CommandInput;
import java.util.HashMap;
import java.util.Map;

/**
 *  Represents a payment transaction within the system.
 *  This class encapsulates details of a payment
 */
public class Payment {

    private static final double STUDENT_TO_GOLD = 350;
    private static final double STUDENT_TO_SILVER = 100;
    private static final double SILVER_TO_GOLD = 250;
    private static final double DISCOUNT_TEN = 10;
    private static final double DISCOUNT_FIVE = 5;
    private static final double DISCOUNT_TWO = 2;
    private static final double LIMIT_ONE_HUNDRED = 100;
    private static final double LIMIT_THREE_HUNDRED = 300;
    private static final double LIMIT_FIVE_HUNDRED = 500;
    private static final double CASHBACK_STUDENT_ONE = 0.001;
    private static final double CASHBACK_STUDENT_TWO = 0.002;
    private static final double CASHBACK_STUDENT_THREE = 0.0025;
    private static final double CASHBACK_SILVER_ONE = 0.003;
    private static final double CASHBACK_SILVER_TWO = 0.004;
    private static final double CASHBACK_SILVER_THREE = 0.005;
    private static final double CASHBACK_GOLD_TWO = 0.0055;
    private static final double CASHBACK_GOLD_THREE = 0.007;

    /**
     * Calculate the commission for a payment
     * @param planType
     * @param amount
     * @return
     */
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
                if (amount < LIMIT_FIVE_HUNDRED) {
                    return 0.0;
                } else {
                    return amount * CASHBACK_STUDENT_ONE;
                }
            }
            case "standard" -> {
                return amount * CASHBACK_STUDENT_TWO;
            }
            default ->
                System.out.println("error");
        }
        return 0.0;
    }

    /**
     * Calculate the threshold for one account
     */
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
                if (sum >= LIMIT_ONE_HUNDRED && sum < LIMIT_THREE_HUNDRED) {
                    ret = CASHBACK_STUDENT_ONE;
                } else if (sum >= LIMIT_THREE_HUNDRED && sum < LIMIT_FIVE_HUNDRED) {
                    ret = CASHBACK_STUDENT_TWO;
                } else if (sum >= LIMIT_FIVE_HUNDRED) {
                    ret = CASHBACK_STUDENT_THREE;
                }
                break;

            case "silver":
                if (sum >= LIMIT_ONE_HUNDRED && sum < LIMIT_THREE_HUNDRED) {
                    ret = CASHBACK_SILVER_ONE;
                } else if (sum >= LIMIT_THREE_HUNDRED && sum < LIMIT_FIVE_HUNDRED) {
                    ret = CASHBACK_SILVER_TWO;
                } else if (sum >= LIMIT_FIVE_HUNDRED) {
                    ret = CASHBACK_SILVER_THREE;
                }
                break;

            case "gold":
                if (sum >= LIMIT_ONE_HUNDRED && sum < LIMIT_THREE_HUNDRED) {
                    ret = CASHBACK_SILVER_THREE;
                } else if (sum >= LIMIT_THREE_HUNDRED && sum < LIMIT_FIVE_HUNDRED) {
                    ret = CASHBACK_GOLD_TWO;
                } else if (sum >= LIMIT_FIVE_HUNDRED) {
                    ret = CASHBACK_GOLD_THREE;
                }
                break;
            default:
                ret = 0;
                break;
        }
        return ret;
    }

    /**
     * Calculate how many times an account has made a transaction to a specific
     * commerciant
     * @param account
     * @param commerciant
     */
    public  void calculateNumberOfTransactions(final Account account,
                                               final Commerciant commerciant) {
        if (account.getNumberOfTransactions() != null
                && account.getNumberOfTransactions().containsKey(commerciant)) {
            int numOfTr = account.getNumberOfTransactions().get(commerciant);
            if (numOfTr == 2) {
                account.getIsDiscountUsed().put(DISCOUNT_TWO, true);
                return;
            }
            if (numOfTr == (2 + 2 + 1)) {
                account.getIsDiscountUsed().put(DISCOUNT_FIVE, true);
                return;
            }
            if (numOfTr == (2 + 2 + 2 + 2 + 2)) {
                account.getIsDiscountUsed().put(DISCOUNT_TEN, true);
            }
        }
    }

    /**
     * calculates the cashback based on the commerciant type, it verifies
     * if it's nrOfTransaction or Threshold
     */
    public static double cashback(final CommandInput command, final Commerciant commerciant,
                                  final Bank bank, final Account account) {
        Exchange exchange = new Exchange(bank);
        User user = bank.getUsers().get(bank.getEmailForAccountIBAN(account.getAccount()));
        Commerciant c = Commerciant.findCommerciant(command, bank, account);
        double rate = 0.0;
        double amount = 0.0;
        if (c != null) {
            if (c.getCashbackStrategy().equals("spendingThreshold")) {
                rate = exchange.findExchangeRate(command.getCurrency(), account.getCurrency());
                amount = threshold(account.getThresholdAmount(), user, account)
                        * command.getAmount();
                return amount * rate;
            }
        }
        return 0.0;
    }

    /**
     * The user should pay a fee for upgrading the plan
     * @param currentPlan
     * @param newPlan
     * @return
     */
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
