package org.poo.bank.splitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.Transaction;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class SplitPayment implements CommandPattern {
    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {

        if(command.getSplitPaymentType().equals("custom")) {
            SplitCustom split = new SplitCustom(bank);
            split.splitPayment(obj, command);
        }
        else{
            SplitEqual split = new SplitEqual();
            split.splitPayment(obj, command, bank);
        }

        List<Transaction> trans= new ArrayList<>();

        //pun toate tranzactiile de tip split
        for(String iban : command.getAccounts()){
            User user = bank.getUsers().get(bank.findUserEmailByIBAN(iban));
            for(Transaction t : user.getTransactions()){
                if(t.getSplitType()!=null && t.getSplitType().equals(command.getSplitPaymentType())){
                    trans.add(t);
                }
            }
        }
        bank.getTransactionsList().put(trans, false);

    }
}
