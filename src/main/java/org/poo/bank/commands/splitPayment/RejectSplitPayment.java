package org.poo.bank.commands.splitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.List;

public class RejectSplitPayment implements CommandPattern {

    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        User user = bank.getUsers().get(command.getEmail());
        if (user == null) {
            Node.createRejectSplitPaymentNode(command, output, obj,
                    "User not found");
            return;
        }
        Transaction target = null;
        for (Transaction t : user.getTransactions()) {
            if (t.getSplitType() != null && t.getSplitType()
                    .equals(command.getSplitPaymentType())
                    && (!t.isAccept())) {
                target = t;
                break;
            }
        }
        if (target != null) {
            target.setAccept(true);
            target.setReject(true);
        }
        List<Transaction> list = Bank.findTransactionList(bank
                .getTransactionsList(), target);
        if (list != null) {
            for (Transaction t : list) {
                if (!t.isAccept()) {
                    return;
                }
            }
        }
        if (list != null) {
            for (Transaction t : list) {
                t.setError("One user rejected the payment.");
            }
            for (Transaction t : list) {
                t.setAllAccepted(true);
            }
        }
    }
}
