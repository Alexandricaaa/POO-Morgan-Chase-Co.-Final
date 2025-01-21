package org.poo.bank;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;

@Data
public class Commerciant {

    private String name;
    private int id;
    private String account;
    private String type;
    private String cashbackStrategy;

    public Commerciant(final CommerciantInput commerciant) {
        this.account = commerciant.getAccount();
        this.id = commerciant.getId();
        this.type = commerciant.getType();
        this.cashbackStrategy = commerciant.getCashbackStrategy();
        this.name = commerciant.getCommerciant();
    }

    public static Commerciant findCommerciant(final CommandInput command,
                                              final Bank bank, final Account a) {
        boolean found = false;
        for (Commerciant commerciant : bank.getCommerciants()) {
            if (commerciant.getName().equals(command.getCommerciant())) {
                for (Commerciant c : a.getCommerciants()) {
                    if (c.getName().equals(commerciant.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    a.getCommerciants().add(commerciant);
                }
                return commerciant;
            }
        }
        return null;
    }

    public static void incrementNumOfTr(final CommandInput command, final Bank bank,
                                        final Account account) {

        Exchange exchange = new Exchange(bank);
        double amount = command.getAmount();
        double ron = amount * exchange.findExchangeRate(command.getCurrency(), "RON");

        Commerciant commerciant = findCommerciant(command, bank, account);
        if (commerciant != null) {
            if (!commerciant.getCashbackStrategy().equals("nrOfTransactions")) {
                account.setThresholdAmount(account.getThresholdAmount() + ron);
                return;
            }
            for (Commerciant c : bank.getCommerciants()) {
                if (c.getName().equals(command.getCommerciant())) {
                    if (!account.getNumberOfTransactions().containsKey(c)) {
                        account.getNumberOfTransactions().put(c, 1);
                        break;
                    } else {
                        account.getNumberOfTransactions()
                                .put(c, account.getNumberOfTransactions()
                                        .getOrDefault(c, 0) + 1);

                    }
                }
            }
        }
    }
}
