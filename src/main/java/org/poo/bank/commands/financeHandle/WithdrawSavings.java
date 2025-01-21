package org.poo.bank.commands.financeHandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;


public class WithdrawSavings implements CommandPattern {

    private static final int LEGAL_AGE = 21;

    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        Exchange exchange = new Exchange(bank);
        User user = bank.getUsers().get(command.getEmail());
        Account savings = bank.findAccountByIBAN(command.getAccount());
        Account classic = new Account();

        if (user == null) {
            String email = bank.getEmailForAccountIBAN(command.getAccount());
            user = bank.getUsers().get(email);
            if (user == null) {
                return;
            }
        }
        for (Account a : user.getAccounts()) {
            if (a.getAccount().equals(command.getAccount())) {
                savings = a;
                break;
            }
        }
        if (savings == null) {
            BuildOneTransaction.error(command, user, "Account not found");
            return;
        }

        if (!savings.getAccountType().equals("savings")) {
            BuildOneTransaction.invalidAccType(command, user,
                    "Account is not of type savings.");
            return;
        }

        if (User.userAge(user.getBirthDate()) < LEGAL_AGE) {
            BuildOneTransaction.messageValidAcc(command, user,
                    "You don't have the minimum age required.", savings.getAccount());
            return;
        }
        int ok = 0;
        for (Account a : user.getAccounts()) {
            if (a.getAccountType().equals("classic")) {
                classic = a;
                ok = 1;
                double rate = exchange.findExchangeRate(command.getCurrency(),
                        savings.getCurrency());
                double amount = rate * command.getAmount();

                if (amount > savings.getBalance()) {
                    BuildOneTransaction.messageValidAcc(command, user,
                            "Insufficient funds", a.getAccount());
                    return;
                } else {
                    a.setBalance(a.getBalance() + amount);
                    savings.setBalance(savings.getBalance() - amount);

                }
            }
        }

        if (ok == 0) {
            BuildOneTransaction.messageValidAcc(command, user,
                    "You do not have a classic account.", savings.getAccount());
            return;
        }
        BuildOneTransaction.addTransactionForWithdrawal(command,
                user, savings.getAccount(),  classic.getAccount());
    }
}
