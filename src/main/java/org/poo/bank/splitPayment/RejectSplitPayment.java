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


        if (user == null) {
            Node.createRejectSplitPaymentNode(command, output, obj, "User not found");
            return;
        }

        Transaction target = null;
        for (Transaction t : user.getTransactions()) {
            if (t.getSplitType() != null && t.getSplitType().equals(command.getSplitPaymentType())
                    && (!t.isAccept() || !t.isReject())) {
                target = t;
                break;
            }
        }
        if (target != null) {
            target.setReject(true);
        }

        List<Transaction> list = Bank.findTransactionList(bank.getTransactionsList(), target);
        if (list != null) {
            for (Transaction t : list) {
                if (!t.isAccept()) {
                    if (!t.isReject()) {
                        return;
                    }
                }
            }
        }

        for (Transaction t : list) {
            t.setError("ne user rejected the payment.");
        }

        for (Transaction t : list) {
            t.setAllAccepted(true);
        }

    }
}
