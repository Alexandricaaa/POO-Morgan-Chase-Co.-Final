package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import java.util.Map;
/**
 * Class that provides a node with the specified details to the provided output array.
 */

public class Node {
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static ObjectNode createCardNode(final Card card, final ObjectMapper objectMapper) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("cardNumber", card.getCardNumber());
        cardNode.put("status", card.getStatus());
        return cardNode;
    }
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static ObjectNode createUserNode(final User user, final ObjectMapper objectMapper) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("firstName", user.getFirstName());
        userNode.put("lastName", user.getLastName());
        userNode.put("email", user.getEmail());

        ArrayNode accountsArray = objectMapper.createArrayNode();
        for (Account acc : user.getAccounts()) {
            if ("business".equals(acc.getAccountType())
                    && "owner".equals(user.getEmployeeRole().get(acc.getAccount()))) {
                accountsArray.add(createAccountNode(acc, objectMapper));
            }
            if (!"business".equals(acc.getAccountType())) {
                accountsArray.add(createAccountNode(acc, objectMapper));
            }
        }
        userNode.set("accounts", accountsArray);
        return userNode;
    }
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static ObjectNode createAccountNode(final Account account,
                                               final ObjectMapper objectMapper) {
        ObjectNode accountNode = createAccountInfoNode(objectMapper, account);
        accountNode.put("type", account.getAccountType());

        ArrayNode cardsArray = objectMapper.createArrayNode();

        for (Card card : account.getCards()) {
            if (!card.getStatus().equals("destroyed")) {
                cardsArray.add(createCardNode(card, objectMapper));
            }
        }
        accountNode.set("cards", cardsArray);
        return accountNode;
    }
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static ObjectNode createNode(final String command,
                                        final ObjectMapper objectMapper, final int timestamp) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", command);
        node.put("timestamp", timestamp);
        return node;
    }
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static void addErrorWithDescrip(final ObjectMapper objectMapper, final ObjectNode node,
                                           final String description, final int timestamp) {
        ObjectNode outObj = objectMapper.createObjectNode();
        outObj.put("description", description);
        outObj.put("timestamp", timestamp);

        node.set("output", outObj);
        node.put("timestamp", timestamp);
    }
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static void addErrorToNode(final ObjectMapper objectMapper, final ObjectNode node,
                                      final String description, final int timestamp) {
        ObjectNode outObj = objectMapper.createObjectNode();
        outObj.put("error", description);
        node.set("output", outObj);
    }
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static ObjectNode createAccountInfoNode(final ObjectMapper objectMapper,
                                                   final Account acc) {
        ObjectNode outObj = objectMapper.createObjectNode();
        outObj.put("IBAN", acc.getAccount());
        outObj.put("balance", acc.getBalance());
        outObj.put("currency", acc.getCurrency());
        return outObj;
    }
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static void addTransaction(final ArrayNode transactionArray, final Transaction t,
                                      final ObjectMapper objectMapper) {
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("timestamp", t.getTimestamp());
        transactionNode.put("description", t.getDescription());
        transactionNode.put("amount", t.getAmount());
        transactionNode.put("commerciant", t.getCommerciant());
        transactionArray.add(transactionNode);
    }
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static ArrayNode createCommerciantsArray(final Map<String, Double> commerciantTotals,
                                                    final ObjectMapper objectMapper) {
        ArrayNode commerciantsArray = objectMapper.createArrayNode();
        commerciantTotals.forEach((commerciant, total) -> {
            ObjectNode commerciantNode = objectMapper.createObjectNode();
            commerciantNode.put("commerciant", commerciant);
            commerciantNode.put("total", total);
            commerciantsArray.add(commerciantNode);
        });
        return commerciantsArray;
    }
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static ObjectNode createBusinessReportNode(final CommandInput cmd,
                                                      final Account account,
                                                      final ObjectMapper objectMapper,
                                                      final String type) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", "businessReport");
        node.put("timestamp", cmd.getTimestamp());

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("IBAN", account.getAccount());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        outputNode.put("spending limit", account.getSpendingLimit());
        outputNode.put("deposit limit", account.getDepositLimit());
        outputNode.put("statistics type", type);

        node.set("output", outputNode);
        return node;
    }
    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static void createRejectSplitPaymentNode(final CommandInput command,
                                                    final ArrayNode output,
                                                    final ObjectMapper objectMapper,
                                                    final String description) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", "rejectSplitPayment");
        node.put("timestamp", command.getTimestamp());

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("description", description);
        outputNode.put("timestamp", command.getTimestamp());
        node.set("output", outputNode);
        output.add(node);
    }

    /**
     * Adds a new command node with the specified details to the provided output array.
     */

    public static void addCommandNode(final ArrayNode output,
                                      final ObjectMapper objectMapper,
                                      final String commandType,
                                      final String description,
                                      final int timestamp) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", commandType);

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("description", description);
        outputNode.put("timestamp", timestamp);
        node.set("output", outputNode);
        node.put("timestamp", timestamp);
        output.add(node);
    }
}
