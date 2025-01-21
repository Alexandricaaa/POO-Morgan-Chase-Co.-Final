package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class AddAccount implements CommandPattern {

    @Override
    public void execute(final CommandInput command,
                        final ObjectMapper obj,
                        final ArrayNode output,
                        final Bank bank) {
        User user = bank.getUsers().get(command.getEmail());

        if (user == null) {
            return;
        }

        Account newAccount = new Account(command);
        Account.planType(newAccount, user);
        Account.configureAccountByType(bank, newAccount, user, command);
        user.getAccounts().add(newAccount);
        BuildOneTransaction.messageValidAcc(command, user,
                "New account created", newAccount.getAccount());
        if (user.getPlanType() != null) {
            newAccount.setPlanType(user.getPlanType());
        }
    }
}
