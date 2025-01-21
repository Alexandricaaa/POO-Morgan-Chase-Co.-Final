package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;

public class AddAccount implements CommandPattern {

    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {

        User user = bank.getUsers().get(command.getEmail());

        if (user == null) {
            return;
        }

        Account newAccount = new Account(command);


        Account.PlanType(newAccount, user);
        Account.configureAccountByType(bank, newAccount, user, command);
        user.getAccounts().add(newAccount);
        Transaction.messageValidAcc(command,user,"New account created", newAccount.getAccount());
        if(user.getPlanType()!=null){
            newAccount.setPlanType(user.getPlanType());
        }
    }
}
