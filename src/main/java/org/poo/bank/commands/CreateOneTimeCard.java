package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class CreateOneTimeCard implements CommandPattern {
    /**
     * creates an one time card
     * @param command
     * @param obj
     * @param output
     * @param bank
     */
    @Override
    public void execute(final CommandInput command,
                        final ObjectMapper obj,
                        final ArrayNode output,
                        final Bank bank) {
        User user = bank.getUsers().get(command.getEmail());
        Card oneTimeCard = new Card();
        oneTimeCard.setOneTime(true);

        Account account = bank.findAccount(user, command.getAccount());
        if (account == null) {
            return;
        }
        account.getCards().add(oneTimeCard);
        BuildOneTransaction.card(command, user, oneTimeCard,
                "New card created", account.getAccount());
    }
}
