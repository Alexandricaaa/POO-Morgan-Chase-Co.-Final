package org.poo.bank.commands.splitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;
import java.util.ArrayList;
import java.util.List;

public class AcceptSplitPayment implements CommandPattern {

    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {

        String email = command.getEmail();
        User user = bank.getUsers().get(email);

        ObjectNode objectNode = obj.createObjectNode();
        objectNode.put("command", "acceptSplitPayment");

        if (user == null) {
            Node.addErrorWithDescrip(obj, objectNode,
                    "User not found", command.getTimestamp());
            output.add(objectNode);
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
        }
        List<Transaction> list = Bank.findTransactionList(bank.getTransactionsList(), target);
        List<Transaction> targetList = new ArrayList<>();
        Transaction firstTransaction = null;
        if (list != null) {
            for (Transaction t : list) {
                if (t.getAlreadyProcessed() == null) {
                    firstTransaction = t;
                    break;
                }
            }
            if (firstTransaction != null) {
                String description = firstTransaction.getDescription();
                for (Transaction t : list) {
                    if (t.getDescription().equals(description)) {
                        targetList.add(t);
                    }
                }
            }
            for (Transaction t : targetList) {
                if (!t.isAccept()) {
                    return;
                }
            }
        }

        String iban = null;
        for (Transaction t : targetList) {
            Account a = bank.findAccountByIBAN(t.getFindSplitAcc());
            if (t.getSplitType().equals("custom")) {
                if (a.getBalance() < t.getAmountToSplit()) {
                    iban = a.getAccount();
                    break;
                }
            } else {
                if (a.getBalance() < t.getAmountEqual()) {
                    iban = a.getAccount();
                    break;
                }
            }
        }
        if (iban != null) {
            for (Transaction t : targetList) {
                t.setError("Account " + iban
                        + " has insufficient funds for a split payment.");
            }
        } else {
            for (Transaction t : targetList) {
                Account a = bank.findAccountByIBAN(t.getFindSplitAcc());
                if (t.getSplitType().equals("custom")) {
                    a.setBalance(a.getBalance() - t.getAmountToSplit());
                } else {
                    a.setBalance(a.getBalance() - t.getAmountEqual());
                    }
            }
        }
        for (Transaction t : targetList) {
            if (t.isReject()) {
                t.setError("One user rejected the payment.");
            }
        }
        for (Transaction t : targetList) {
            t.setAlreadyProcessed(true);
            t.setAllAccepted(true);
        }
    }
}
