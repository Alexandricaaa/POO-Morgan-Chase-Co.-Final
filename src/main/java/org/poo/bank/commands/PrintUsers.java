package org.poo.bank.commands;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.Node;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;

public class PrintUsers implements CommandPattern {
    /**
     * Shows all the bank users
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        ObjectNode node = obj.createObjectNode();
        node.put("command", "printUsers");
        node.put("timestamp", command.getTimestamp());
        ArrayNode usersArray = obj.createArrayNode();

        for (User u : bank.getUsers().values()) {
            usersArray.add(Node.createUserNode(u, obj));
        }

        node.set("output", usersArray);
        output.add(node);
    }
}
