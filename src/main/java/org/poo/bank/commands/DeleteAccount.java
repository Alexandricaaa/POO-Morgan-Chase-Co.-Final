package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;
import java.util.Iterator;

public class DeleteAccount implements CommandPattern {
    /**
     * deletes an account if the balance is 0
     */
    @Override
    public void execute(final CommandInput command,
                        final ObjectMapper obj,
                        final ArrayNode output,
                        final Bank bank) {
        User user = bank.getUsers().get(command.getEmail());

        ObjectNode newObject = output.addObject();
        newObject.put("command", "deleteAccount");
        newObject.put("timestamp", command.getTimestamp());

        if (user == null) {
            ObjectNode outObj = obj.createObjectNode();
            outObj.put("error", "Account not found");
            outObj.put("timestamp", command.getTimestamp());
            newObject.set("output", outObj);
            output.add(newObject);
            return;
        }

        boolean accountDeleted = false;

        Iterator<Account> iterator = user.getAccounts().iterator();
        String plan = null;
        while (iterator.hasNext()) {
            Account account = iterator.next();
            if (account.getAccount().equals(command.getAccount())) {
                if (account.getBalance() == 0) {
                    plan = account.getPlanType();
                    account.getCards().clear();
                    iterator.remove();
                    accountDeleted = true;
                    break;
                }
            }
        }
        if (user.getAccounts().isEmpty()) {
            if (plan != null) {
                user.setPlanType(plan);
            }
        }

        if (accountDeleted) {
            ObjectNode outputNode = newObject.putObject("output");
            outputNode.put("success", "Account deleted");
            outputNode.put("timestamp", command.getTimestamp());
        } else {
            BuildOneTransaction.error(command, user,
                    "Account couldn't be deleted - there are funds remaining");

            ObjectNode outputNode = newObject.putObject("output");
            outputNode.put("error", "Account couldn't be deleted"
                            + " - see org.poo.transactions for details");
            outputNode.put("timestamp", command.getTimestamp());
        }
    }
}
