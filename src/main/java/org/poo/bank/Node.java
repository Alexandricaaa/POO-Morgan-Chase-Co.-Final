package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Node {

    public static ObjectNode createAccountNode(Account account, ObjectMapper objectMapper) {
        ObjectNode accountNode = objectMapper.createObjectNode();
        accountNode.put("IBAN", account.getAccount());
        accountNode.put("balance", account.getBalance());
        accountNode.put("currency", account.getCurrency());
        accountNode.put("type", account.getAccountType());

        ArrayNode cardsArray = objectMapper.createArrayNode();
        // Procesăm fiecare card al contului
        for (Card card : account.getCards()) {
            if (!card.getStatus().equals("destroyed")) {
                cardsArray.add(createCardNode(card, objectMapper));
            }
        }
        accountNode.set("cards", cardsArray);

        return accountNode;
    }

    public static ObjectNode createCardNode(Card card, ObjectMapper objectMapper) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("cardNumber", card.getCardNumber());
        cardNode.put("status", card.getStatus());
        return cardNode;
    }

    public static ObjectNode createUserNode(User user, ObjectMapper objectMapper) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("firstName", user.getFirstName());
        userNode.put("lastName", user.getLastName());
        userNode.put("email", user.getEmail());

        ArrayNode accountsArray = objectMapper.createArrayNode();
        // Procesăm fiecare cont al utilizatorului
        for (Account acc : user.getAccounts()) {
            accountsArray.add(createAccountNode(acc, objectMapper));
        }
        userNode.set("accounts", accountsArray);
        return userNode;
    }

    public static ObjectNode createNode(String command,ObjectMapper objectMapper, int timestamp) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", command);
        node.put("timestamp", timestamp);
        return node;
    }
    public static void addErrorToNode(ObjectMapper objectMapper, ObjectNode node, String description, int timestamp) {
        ObjectNode outObj = objectMapper.createObjectNode();
        outObj.put("description", description);
        outObj.put("timestamp", timestamp);
        node.set("output", outObj);
    }
}
