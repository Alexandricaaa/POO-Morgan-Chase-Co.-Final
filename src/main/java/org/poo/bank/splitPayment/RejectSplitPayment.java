package org.poo.bank.splitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;

public class RejectSplitPayment implements CommandPattern {

    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        User user = bank.getUsers().get(command.getEmail());

        if(user == null){
            ObjectNode node = obj.createObjectNode();
            node.put("command", "rejectSplitPayment");
            node.put("timestamp", command.getTimestamp());


            ObjectNode outObj = obj.createObjectNode();
            outObj.put("description", "User not found");
            outObj.put("timestamp", command.getTimestamp());
            node.set("output", outObj);
            output.add(node);
            return;
        }
        user.setRejected(true);
        List<Transaction> transactions = user.getTransactions();
        List<String> ibanInvolved = new ArrayList<>();

        if (transactions != null) {
            for (Transaction t : transactions) {
                if (t.getSplitType() != null) {
                    ibanInvolved = t.getAccountSplit();
                }
            }
        }
        for (String s : ibanInvolved) {
            Account a = bank.findAccountByIBAN(s);
            String email = bank.findUserEmailByIBAN(s);
            List<Transaction> transactionsForOneAcc =user.getTransactions();
            Transaction target = bank.targetTransaction(transactionsForOneAcc,
                    command.getSplitPaymentType(),
                    ibanInvolved);;
            }


    }
}
