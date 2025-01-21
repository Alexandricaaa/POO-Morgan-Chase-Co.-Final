package org.poo.bank.commands.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpendingsReport implements CommandPattern {

    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        Account account = bank.findAccountByIBAN(command.getAccount());
        String email = bank.getEmailForAccountIBAN(command.getAccount());
        User user = bank.getUsers().get(email);
        if (user == null || account == null) {
            return;
        }
        if ("savings".equals(account.getAccountType())) {
            ObjectNode node = obj.createObjectNode();
            node.put("command", "spendingsReport");
            node.put("timestamp", command.getTimestamp());
            Node.addErrorToNode(obj, node,
                    "This kind of report is not"
                            + " supported for a saving account", command.getTimestamp());
            output.add(node);
        } else {
            ObjectNode node = obj.createObjectNode();
            node.put("command", "spendingsReport");
            node.put("timestamp", command.getTimestamp());

            ObjectNode outObj = Node.createAccountInfoNode(obj, account);

            List<Transaction> transactions = user.getTransactions();
            if (transactions != null) {
                ArrayNode transactionArray = obj.createArrayNode();
                Map<String, Double> commerciantTotals = new HashMap<>();

                transactions.stream()
                        .filter(t -> t.getTimestamp() >= command.getStartTimestamp()
                                && t.getTimestamp() <= command.getEndTimestamp())
                        .filter(t -> t.getFindTransaction()
                                != null && t.getFindTransaction()
                                .equals(command.getAccount()))
                        .forEach(t -> {
                            Node.addTransaction(transactionArray, t, obj);

                            String commerciant = t.getCommerciant();
                            Double amount = t.getAmount();

                            if (commerciant != null && amount != null) {
                                commerciantTotals.merge(commerciant, amount, Double::sum);
                            }
                        });

                outObj.set("transactions", transactionArray);
                outObj.set("commerciants", Node.createCommerciantsArray(commerciantTotals, obj));
            }
            node.set("output", outObj);
            output.add(node);
        }
    }
}
