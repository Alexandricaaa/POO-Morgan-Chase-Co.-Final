package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class CreateCard implements CommandPattern {

    @Override
    public void execute(final CommandInput command,
                        final ObjectMapper obj,
                        final ArrayNode output,
                        final Bank bank) {
        User user = bank.getUsers().get(command.getEmail());
        Account account = new Account();

        for (Account a : user.getAccounts()) {
            if (a.getAccount().equals(command.getAccount())) {
                account = a;
                break;
            }
        }

        Card card = new Card();
        account.getCards().add(card);
        BuildOneTransaction.card(command, user, card,
                "New card created", account.getAccount());
    }
}
