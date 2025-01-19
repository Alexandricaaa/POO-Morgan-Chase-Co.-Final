package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Account;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;

public class AddNewBusinessAssociate implements CommandPattern {
    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        User user = bank.getUsers().get(command.getEmail());
        Account account = bank.findAccountByIBAN(command.getAccount());

        //daca nu exista cheia pt acest user
        if(!user.getEmployeeRole().containsKey(command.getAccount())){
            user.getEmployeeRole().put(command.getAccount(), command.getRole());
            user.getAccounts().add(account);
        }
    }
}
