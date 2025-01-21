package org.poo.bank.commands.financeHandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;

/**
 *  Changes the deposit limit in a business account
 */
public class ChangeDepositLimit implements CommandPattern {

    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        ObjectNode node = obj.createObjectNode();
        node.put("command", "changeDepositLimit");
        node.put("timestamp", command.getTimestamp());

        User user = bank.getUsers().get(command.getEmail());
        Account account = bank.findAccount(user, command.getAccount());

        if (!account.getAccountType().equals("business")) {
            ObjectNode outObj = obj.createObjectNode();
            outObj.put("description", "This is not a business account");
            outObj.put("timestamp", command.getTimestamp());
            node.set("output", outObj);
            output.add(node);
            return;
        }
        String role = user.getEmployeeRole().get(account.getAccount());
        if (!role.equals("owner")) {
            ObjectNode outObj = obj.createObjectNode();
            outObj.put("description",
                    "You must be owner in order to change deposit limit.");
            outObj.put("timestamp", command.getTimestamp());
            node.set("output", outObj);
            output.add(node);

        } else {
            account.setDepositLimit(command.getAmount());
        }
    }
}
