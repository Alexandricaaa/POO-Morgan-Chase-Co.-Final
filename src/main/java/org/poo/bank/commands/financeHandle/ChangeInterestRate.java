package org.poo.bank.commands.financeHandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class ChangeInterestRate implements CommandPattern {

    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        Account account = null;
        String email = bank.getEmailForAccountIBAN(command.getAccount());
        User user = bank.getUsers().get(email);
        for (User u : bank.getUsers().values()) {
            for (Account acc : u.getAccounts()) {
                if (acc.getAccount().equals(command.getAccount())) {
                    account = acc;
                }
            }
        }
        if (account != null) {
            account.setInterestRate(command.getInterestRate());
            double amount =  command.getInterestRate();
            BuildOneTransaction.interestChange(command, user,
                    "Interest rate of the account changed to " + amount);
        }
    }
}
