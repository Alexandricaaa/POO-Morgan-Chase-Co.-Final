package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.fileio.CommandInput;
import org.poo.bank.User;
import org.poo.bank.Transaction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PrintTransactions implements CommandPattern {
    /**
     * print all the transactions made by an user
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        User user = bank.getUsers().get(command.getEmail());

        if (user == null) {
            ObjectNode errorNode = obj.createObjectNode();
            errorNode.put("command", "printTransactions");
            errorNode.put("error", "User not found");
            errorNode.put("timestamp", command.getTimestamp());
            output.add(errorNode);
            return;
        }

        List<Transaction> transactions = new ArrayList<>(user.getTransactions());
        transactions.sort(Comparator.comparingInt(Transaction::getTimestamp));

        ObjectNode outputNode = obj.createObjectNode();
        outputNode.put("command", "printTransactions");
        outputNode.put("timestamp", command.getTimestamp());

        ArrayNode transactionsArray = obj.createArrayNode();
        for (Transaction transaction : transactions) {
            if (!transaction.isAllAccepted() && transaction.getSplitType() != null) {
                continue;
            }
            if (transaction.isIgnore()) {
                continue;
            }

            ObjectNode transactionNode = Transaction.createTransactionOutputNode(obj, transaction);
            transactionsArray.add(transactionNode);
        }
        outputNode.set("output", transactionsArray);
        output.add(outputNode);
    }
}
