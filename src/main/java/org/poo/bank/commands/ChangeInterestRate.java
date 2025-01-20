package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class ChangeInterestRate implements CommandPattern {
    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        Account account = null;
        //User user = bank.getUsers().get(command.getEmail());
        String email = bank.findUserEmailByIBAN(command.getAccount());
        User user = bank.getUsers().get(email);
        for (User u : bank.getUsers().values()) {
            for (Account acc : u.getAccounts()) {
                if (acc.getAccount().equals(command.getAccount())) {
                    account = acc;
                }
            }
        }
        if(account!=null){
            account.setInterestRate(command.getInterestRate());
            double amount =  command.getInterestRate();
            Transaction.interestChange(command, user,"Interest rate of the account changed to " + amount);
        }
    }
}
