package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class AddInterest implements CommandPattern {
    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        User user = bank.getUsers().get(command.getEmail());
        if(user==null){
            return;
        }
        Account account = bank.findAccount(user, command.getAccount());
        double interest = 0.0;
        if(account.getAccountType().equals("savings")){
            interest = account.getBalance() * account.getInterestRate();
        }
        account.setBalance(account.getBalance() + interest);
        Transaction.addInterest(command,user,account,interest);
    }
}
