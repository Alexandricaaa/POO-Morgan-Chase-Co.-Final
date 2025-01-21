package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Account;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;
import java.util.ArrayList;
import java.util.List;

/**
 * adds an user to the business account with a specified role
 */
public class AddNewBusinessAssociate implements CommandPattern {
    /**
     * execute the command
     */
    @Override
    public void execute(final CommandInput command,
                        final ObjectMapper obj,
                        final ArrayNode output,
                        final Bank bank) {
        User user = bank.getUsers().get(command.getEmail());
        Account account = bank.findAccountByIBAN(command.getAccount());

        if (!user.getEmployeeRole().containsKey(command.getAccount())) {
            user.getEmployeeRole().put(command.getAccount(), command.getRole());
            user.getAccounts().add(account);
        }

        List<User> businessUsers = bank.getBusinessUsersPerAcc()
                .getOrDefault(command.getAccount(), new ArrayList<>());
        businessUsers.add(user);

        bank.getBusinessUsersPerAcc().put(command.getAccount(), businessUsers);
    }
}
