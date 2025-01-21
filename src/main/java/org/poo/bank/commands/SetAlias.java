package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Account;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;

public class SetAlias implements CommandPattern {

    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        boolean accountFound = false;
        Account a = null;
        User user = bank.getUsers().get(command.getEmail());
        for (Account account : user.getAccounts()) {
            if (account.getAccount().equals(command.getAccount())) {
                a = account;
                accountFound = true;
                break;
            }
        }
        if (accountFound) {
            a.setAlias(command.getAlias());
            bank.getAccountAlias().put(command.getAlias(), command.getAccount());
        }
    }
}
