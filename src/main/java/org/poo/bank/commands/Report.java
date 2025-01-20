package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;

public class Report implements CommandPattern {
    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {

        String email = bank.findUserEmailByIBAN(command.getAccount());
        User user = bank.getUsers().get(email);
        Account account = null;
        for(Account a : user.getAccounts()) {
            if(a.getAccount().equals(command.getAccount())){
                account = a;
                break;
            }
        }
        if(account == null) {
            return;
        }
        ObjectNode node = obj.createObjectNode();
        node.put("command", "report");
        node.put("timestamp", command.getTimestamp());
        ObjectNode outObj = obj.createObjectNode();
        outObj.put("IBAN", account.getAccount());
        outObj.put("balance", account.getBalance());
        outObj.put("currency", account.getCurrency());

        List<Transaction> listTr = new ArrayList<>();
        listTr = user.getTrPerAcc().get(account.getAccount());

        if(listTr != null) {
            ArrayNode transactionsArray = obj.createArrayNode();
            listTr.stream()
                    .filter(t -> t.getTimestamp() >= command.getStartTimestamp() && t.getTimestamp() <= command.getEndTimestamp() && !t.isIgnore())
                    .map(t -> Transaction.createTransactionOutputNode(obj, t)) // Transformăm tranzacțiile în noduri JSON
                    .forEach(transactionsArray::add);
            outObj.set("transactions", transactionsArray);
        }
        node.set("output", outObj);
        output.add(node);
    }
}
