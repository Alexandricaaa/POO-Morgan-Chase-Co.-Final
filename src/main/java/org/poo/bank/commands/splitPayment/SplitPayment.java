package org.poo.bank.commands.splitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.Transaction;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Command SplitPayment
 */
public class SplitPayment implements CommandPattern {

    /**
     *
     * @param command
     * @param obj
     * @param output
     * @param bank
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {

        if (command.getSplitPaymentType().equals("custom")) {
            SplitCustom split = new SplitCustom(bank);
            split.splitPayment(obj, command);
        } else {
            SplitEqual split = new SplitEqual();
            split.splitPayment(obj, command, bank);
        }

        List<Transaction> trans = new ArrayList<>();

        for (String iban : command.getAccounts()) {
            User user = bank.getUsers().get(bank.getEmailForAccountIBAN(iban));
            for (Transaction t : user.getTransactions()) {
                if (t.getSplitType() != null && t.getSplitType()
                        .equals(command.getSplitPaymentType())) {
                    trans.add(t);
                }
            }
        }
        bank.getTransactionsList().put(trans, false);
    }
}
