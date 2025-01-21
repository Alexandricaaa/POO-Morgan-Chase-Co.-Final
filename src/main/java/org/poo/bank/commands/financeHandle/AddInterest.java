package org.poo.bank.commands.financeHandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class AddInterest implements CommandPattern {
    /**
     *
     * @param command
     * @param obj
     * @param output
     * @param bank
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        User user = bank.getUsers().get(command.getEmail());

        if (user == null) {
            String email = bank.getEmailForAccountIBAN(command.getAccount());
            user = bank.getUsers().get(email);

            if (user == null) {
                return;
            }
        }
        Account account = bank.findAccount(user, command.getAccount());
        double interest = 0.0;

        if (account.getAccountType().equals("savings")) {
            interest = account.getBalance() * account.getInterestRate();
        }
        account.setBalance(account.getBalance() + interest);
        BuildOneTransaction.addInterest(command, user, account,  interest);
    }
}
