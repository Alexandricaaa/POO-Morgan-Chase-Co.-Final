package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.poo.fileio.*;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Bank {

    private ArrayList<Commerciant> commerciants = new ArrayList<>();  // aici se afla toti comm dintr-un test
    public static ArrayList<Exchange> exchanges = new ArrayList<>();
    private Map<String, User> users = new LinkedHashMap<>();
    private Map<String, ArrayList<Commerciant>> commerciantsPerAcc = new HashMap<>();
    private Map<String, String> accountAlias = new HashMap<>();


    public Bank(ObjectInput input) {
        Utils.resetRandom();

        for (CommerciantInput commerciant : input.getCommerciants()) {
            this.commerciants.add(new Commerciant(commerciant));
        }

        for (UserInput user : input.getUsers()) {
            User myUser = new User(user);
            users.put(myUser.getEmail(), myUser);
        }

        for(ExchangeInput exchange : input.getExchangeRates()){
            Exchange myExchange = new Exchange(exchange);
            exchanges.add(myExchange);
        }
    }

    public void processCommand(CommandInput cmd, ArrayNode output, ObjectMapper obj) {
        int timestamp = cmd.getTimestamp();
        System.out.println(timestamp);
        CommandPattern command = Factory.createCommand(cmd);
        if (command != null) {
            command.execute(cmd,obj, output, this);
        } else {

            System.out.println("Unknown command start debug : " + cmd.getCommand());
        }
    }

    public Account findAccount(User user, String iban){
        for(Account account : user.getAccounts()){
            if(account.getAccount().equals(iban)){
                return account;
            }
        }
        return null;
    }
    public Account findAccountByIBAN(String iban) {
        for (User u : this.getUsers().values()) {
            for (Account acc : u.getAccounts()) {
                if (acc.getAccount().equals(iban)) {
                    return acc;
                }
            }
        }
        return null;
    }

    public String findUserEmailByIBAN(String iban) {
        for (User u : users.values()) {
            for (Account acc : u.getAccounts()) {
                if (acc.getAccount().equals(iban)) {
                    return u.getEmail();
                }
            }
        }
        return null;
    }

    public Account findAccountByCardNumber(String cardNumber) {
        for (User u : users.values()) {
            for (Account acc : u.getAccounts()) {
                for (Card c : acc.getCards()) {
                    if (c.getCardNumber().equals(cardNumber)) {
                        return acc;
                    }
                }
            }
        }
        return null;
    }

    public boolean isMyAccount(User user, String iban){
        for(Account acc : user.getAccounts()){
            if(acc.getAccount().equals(iban)){
                return true;
            }
        }
        return false;
    }

    public static void addCommandNode(ArrayNode output, ObjectMapper objectMapper,
                                      String commandType, String description, int timestamp) {
        // Creează nodul principal pentru comanda
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", commandType);

        // Creează nodul interior pentru output
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("description", description);
        outputNode.put("timestamp", timestamp);

        // Adaugă nodul de output la nodul principal
        node.set("output", outputNode);
        node.put("timestamp", timestamp);

        // Adaugă nodul final la array-ul de output
        output.add(node);
    }

    public static Card findCardInAccount(Account account, CommandInput command) {
        return account.getCards().stream()
                .filter(card -> card.getCardNumber().equals(command.getCardNumber()))
                .findFirst()
                .orElse(null); // Returnează null dacă nu găsește cardul
    }


}
