package org.poo.bank.splitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SplitEqual {


    public void splitPayment(ObjectMapper obj, CommandInput command, Bank bank) {
        Exchange exchange = new Exchange(bank);
        List<String> accountsSplit = command.getAccounts();
        List<Account> accounts = new ArrayList<>();
        double total = command.getAmount();

        if(accountsSplit.isEmpty()) {
        ObjectNode resultNode = Node.createNode("splitPayment", obj, command.getTimestamp());
        Node.addErrorToNode(obj, resultNode, "error", command.getTimestamp());
        return;
        }

        double amountPerAcc = total / command.getAccounts().size();

        List<Account> resolvedAccounts = command.getAccounts().stream()
                .map(bank::findAccountByIBAN)
                .filter(Objects::nonNull)
                .toList();

        if (resolvedAccounts.size() != command.getAccounts().size()) {
            ObjectNode resultNode = Node.createNode("splitPayment", obj, command.getTimestamp());
            Node.addErrorToNode(obj, resultNode, "error", command.getTimestamp());
            return;
        }



        boolean hasCurrencyError = resolvedAccounts.stream()
                .anyMatch(acc -> exchange.findExchangeRate(command.getCurrency(), acc.getCurrency()) == 0);
        if (hasCurrencyError) {
            return;
        }
        resolvedAccounts.forEach(acc -> processSplitTransaction(command, acc, amountPerAcc, bank, accountsSplit));
    }
    private void processSplitTransaction(CommandInput cmd, Account acc, double amountPerAcc, Bank bank, List<String> accountsList) {
        String email = bank.findUserEmailByIBAN(acc.getAccount());
        User user = bank.getUsers().get(email);

        Exchange exchange = new Exchange(bank);
        double rate = exchange.findExchangeRate(cmd.getCurrency(), acc.getCurrency());
        double localAmount = rate * amountPerAcc;
        String formattedAmount = String.format("%.2f", cmd.getAmount());
        String description = "Split payment of " + formattedAmount + " " + cmd.getCurrency();
        String error= null;
        Transaction.splitEqual(cmd, user, description, accountsList, acc,  localAmount, error);
    }
}

