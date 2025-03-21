package org.poo.bank.commands.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.User;
import org.poo.bank.Account;
import org.poo.bank.Transaction;
import org.poo.fileio.CommandInput;
import java.util.List;

/**
 *
 */
public class Report implements CommandPattern {
    /**
     * execute the command
     * @param command
     * @param obj
     * @param output
     * @param bank
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {

        String email = bank.getEmailForAccountIBAN(command.getAccount());
        User user = bank.getUsers().get(email);
        Account account = null;
        for (Account a : user.getAccounts()) {
            if (a.getAccount().equals(command.getAccount())) {
                account = a;
                break;
            }
        }

        if (account == null) {
            return;
        }

        ObjectNode node = obj.createObjectNode();
        node.put("command", "report");
        node.put("timestamp", command.getTimestamp());
        ObjectNode outObj = obj.createObjectNode();
        outObj.put("IBAN", account.getAccount());
        outObj.put("balance", account.getBalance());
        outObj.put("currency", account.getCurrency());

        List<Transaction> listTr;
        listTr = user.getTrPerAcc().get(account.getAccount());

        if (listTr != null) {
            ArrayNode transactionsArray = obj.createArrayNode();
            listTr.stream()
                    .filter(t -> t.getTimestamp() >= command.getStartTimestamp()
                            && t.getTimestamp() <= command.getEndTimestamp()
                            && !t.isIgnore())
                    .map(t -> Transaction.createTransactionOutputNode(obj, t))
                    .forEach(transactionsArray::add);
            outObj.set("transactions", transactionsArray);
        }
        node.set("output", outObj);
        output.add(node);
    }
}
