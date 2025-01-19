package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.bank.Node;
import org.poo.bank.User;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;

public class PrintUsers implements CommandPattern {


    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        ObjectNode node = obj.createObjectNode();
        node.put("command", "printUsers");
        node.put("timestamp", command.getTimestamp());

        ArrayNode usersArray = obj.createArrayNode();

        // Procesăm fiecare utilizator
        for (User u : bank.getUsers().values()) {
            usersArray.add(Node.createUserNode(u, obj));
        }

        node.set("output", usersArray);
        output.add(node);
    }
}
