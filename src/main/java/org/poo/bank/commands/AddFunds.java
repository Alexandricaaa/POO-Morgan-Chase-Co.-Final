package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class AddFunds implements CommandPattern {

    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        User user = bank.getUsers().get(command.getEmail());
        Account account = new Account();
        for(Account a : user.getAccounts()){
            if(a.getAccount().equals(command.getAccount())){
                account = a;
                break;
            }
        }
        if(account.getAccountType()!=null) {
            if (account.getAccountType().equals("business")) {
                String role = user.getEmployeeRole().get(account.getAccount());
                if (role == null) {
                    return;
                }
                if (role.equals("employee") && command.getAmount() > account.getDepositLimit()) {
                    return;
                }
                Transaction.deposit(command, user);


            }
        }
        account.setBalance(command.getAmount() + account.getBalance());
    }
}
