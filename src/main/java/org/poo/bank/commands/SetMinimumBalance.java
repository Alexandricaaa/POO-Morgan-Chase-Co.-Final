package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;

public class SetMinimumBalance implements CommandPattern {
    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
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
        ObjectNode resultNode = obj.createObjectNode();
        resultNode.put("command", "setMinimumBalance");
        resultNode.put("timestamp", command.getTimestamp());

        ObjectNode outputNode = obj.createObjectNode();
        outputNode.put("description", "Minimum balance set successfully");
        outputNode.put("account", account.getAccount());
        outputNode.put("minimumBalance", command.getAmount());

        resultNode.set("output", outputNode);
        output.add(resultNode);
    }
}
