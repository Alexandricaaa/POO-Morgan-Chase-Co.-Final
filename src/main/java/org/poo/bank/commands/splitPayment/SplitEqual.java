package org.poo.bank.commands.splitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;
import java.util.List;
import java.util.Objects;

public class SplitEqual {

    /**
     * Split the amount for the involved accounts
     * @param obj
     * @param command
     * @param bank
     */
    public void splitPayment(final ObjectMapper obj, final CommandInput command, final Bank bank) {
        Exchange exchange = new Exchange(bank);
        List<String> accountsSplit = command.getAccounts();
        double total = command.getAmount();

        if (accountsSplit.isEmpty()) {
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
                .anyMatch(acc -> exchange.findExchangeRate(command.getCurrency(),
                        acc.getCurrency()) == 0);
        if (hasCurrencyError) {
            return;
        }
        resolvedAccounts.forEach(acc -> processSplitTransaction(command,
                acc, amountPerAcc, bank, accountsSplit));
    }
    private void processSplitTransaction(final CommandInput cmd,
                                         final Account acc, final double amountPerAcc,
                                         final Bank bank, final List<String> accountsList) {
        String email = bank.getEmailForAccountIBAN(acc.getAccount());
        User user = bank.getUsers().get(email);

        Exchange exchange = new Exchange(bank);
        double rate = exchange.findExchangeRate(cmd.getCurrency(), acc.getCurrency());
        double localAmount = rate * amountPerAcc;
        String formattedAmount = String.format("%.2f", cmd.getAmount());
        String description = "Split payment of " + formattedAmount + " " + cmd.getCurrency();
        String error = null;
        BuildOneTransaction.splitEqual(cmd, user, description,
                accountsList, acc,  localAmount, error);
    }
}

