package org.poo.bank.splitPayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;

public class AcceptSplitPayment implements CommandPattern {
    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {

        String email = command.getEmail();
        User user = bank.getUsers().get(email);

        if(user == null ){
            return;
        }

        Transaction target = null;
        for(Transaction t : user.getTransactions()){
            if(t.getSplitType()!=null && t.getSplitType().equals(command.getSplitPaymentType())
                    && !t.isAccept()){
                target = t;
                break;
            }
        }
        if(target!=null) {
            target.setAccept(true);
        }
        List<Transaction> list = Bank.findTransactionList(bank.getTransactionsList(), target);

        //daca vreunu n a acceptat
        if(list !=null){
            for(Transaction t : list){
                if(!t.isAccept()){
                    return;
                }
            }
        }

        String iban = null;
        if(list!=null) {
            //daca au acceptat toti
            for (Transaction t : list) {
                Account a = bank.findAccountByIBAN(t.getFindSplitAcc());
                if(t.getSplitType().equals("custom")) {
                    if (a.getBalance() < t.getAmountToSplit()) {
                        iban = a.getAccount();
                        break;
                    }
                }
                else{
                    if (a.getBalance() < t.getAmountEqual()) {
                        iban = a.getAccount();
                        break;
                    }
                }
            }

            if (iban != null) {
                for (Transaction t : list) {
                    t.setError("Account " + iban + " has insufficient funds for a split payment.");
                }
            } else {
                for (Transaction t : list) {
                    Account a = bank.findAccountByIBAN(t.getFindSplitAcc());
                    if(t.getSplitType().equals("custom")) {
                        a.setBalance(a.getBalance() - t.getAmountToSplit());
                    } else{
                        a.setBalance(a.getBalance() - t.getAmountEqual());
                        }
                }
            }

            for (Transaction t : list) {
                t.setAllAccepted(true);
            }

        }

    }
}
