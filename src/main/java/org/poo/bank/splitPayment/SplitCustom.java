package org.poo.bank.splitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class SplitCustom {

    Bank bank;
    public SplitCustom(Bank bank) {
        this.bank = bank;
    }
    public void splitPayment(ObjectMapper obj, CommandInput command) {
        List<String> accountsSplit = command.getAccounts();
        List<Double> amounts = command.getAmountForUsers();
        List<Account> accounts = new ArrayList<>();

        if (accountsSplit == null || amounts.isEmpty()) {
            ObjectNode resultNode = Node.createNode("splitPayment", obj, command.getTimestamp());
            Node.addErrorToNode(obj, resultNode, "error", command.getTimestamp());
            return;
        }

        accountsSplit.stream()
                .map(bank::findAccountByIBAN)
                .filter(Objects::nonNull)
                .forEach(accounts::add);

        if(accounts == null){
            return;
        }

        AtomicInteger contor = new AtomicInteger(0);

        accounts.forEach(acc -> processSplitPayment(acc, command, amounts, contor, bank, accountsSplit));

    }

    private void processSplitPayment(Account acc, CommandInput cmd, List<Double> originalTotalAmount,
                                     AtomicInteger contor, Bank bank, List<String> accountsList) {
        Exchange exchange = new Exchange(bank);
        double amount = originalTotalAmount.get(contor.getAndIncrement());
        double rate = exchange.findExchangeRate(cmd.getCurrency(), acc.getCurrency());
        amount *= rate;

        acc.setSplit(amount);

       String email = bank.findUserEmailByIBAN(acc.getAccount());
       User user = bank.getUsers().get(email);
        if (user == null) {
            throw new IllegalArgumentException("nu exista");
        }

        String formattedAmount = String.format("%.2f", cmd.getAmount());
        String description = "Split payment of " + formattedAmount + " " + cmd.getCurrency();

        Transaction.splitCustom( cmd,user, description,  accountsList,acc,  originalTotalAmount, amount);

    }
}
