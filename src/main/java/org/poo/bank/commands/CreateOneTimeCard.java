package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class CreateOneTimeCard implements CommandPattern {
    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        User user = bank.getUsers().get(command.getEmail());

        Card oneTimeCard = new Card();
        oneTimeCard.setOneTime(true);

        Account account = bank.findAccount(user, command.getAccount());
        if (account == null) {
            return;
        }
        account.getCards().add(oneTimeCard);
        Transaction.card(command, user, oneTimeCard, "New card created", account.getAccount());
    }
}
