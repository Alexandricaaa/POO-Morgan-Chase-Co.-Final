package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Account;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;

public class SetMinimumBalance implements CommandPattern {

    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        Account account = null;
        for (User u : bank.getUsers().values()) {
            for (Account acc : u.getAccounts()) {
                if (acc.getAccount().equals(command.getAccount())) {
                    account = acc;
                }
            }
        }
        if (account != null) {
            account.setMinimumBalance(command.getAmount());
        }
    }
}
