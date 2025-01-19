package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.poo.fileio.*;
import org.poo.utils.Utils;

import java.util.*;
import java.util.stream.IntStream;

@Data
public class Bank {

    private ArrayList<Commerciant> commerciants = new ArrayList<>();  // aici se afla toti comm dintr-un test
    public  ArrayList<Exchange> exchanges = new ArrayList<>();
    private Map<String, User> users = new LinkedHashMap<>();
    private Map<String, ArrayList<Commerciant>> commerciantsPerAcc = new HashMap<>();
    private Map<String, String> accountAlias = new HashMap<>();

    //pentru split
    Map<List<Transaction>, Boolean> transactionsList =  new HashMap<>();



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

//    public ArrayList<Exchange> getExchanges() {
//        return exchanges;
//    }

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


    public static boolean isUpgrade(String currentPlan, String newPlan) {
        List<String> plans = Arrays.asList("standard", "student", "silver", "gold");

        // Folosim lambda pentru a obține indexurile
        int currentIndex = IntStream.range(0, plans.size())
                .filter(i -> plans.get(i).equalsIgnoreCase(currentPlan))
                .findFirst()
                .orElse(-1);

        int newIndex = IntStream.range(0, plans.size())
                .filter(i -> plans.get(i).equalsIgnoreCase(newPlan))
                .findFirst()
                .orElse(-1);

        return newIndex > currentIndex;
    }

    public void updateAccountPlan(User user, String newPlan) {
        // Actualizăm planul pentru fiecare cont al utilizatorului folosind lambda
        user.getAccounts().forEach(account -> account.setPlanType(newPlan));

    }


    public Transaction targetTransaction(List<Transaction> transactions,
                                             String splitPaymentType,
                                             List<String> ibanInvolved) {
        if (transactions == null || ibanInvolved == null) {
            return null;
        }

        return transactions.stream()
                .filter(t -> t.getSplitType() != null &&
                        splitPaymentType.equals(t.getSplitType()) &&
                        t.getInvolvedAccounts() != null &&  // Verifică dacă lista nu este null
                        t.getInvolvedAccounts().size() == ibanInvolved.size() &&
                        t.getInvolvedAccounts().containsAll(ibanInvolved))
                .findFirst()
                .orElse(null);

    }

    public static List<Transaction> findTransactionList(Map<List<Transaction>, Boolean> transactionsList, Transaction t) {
        for (Map.Entry<List<Transaction>, Boolean> entry : transactionsList.entrySet()) {
            List<Transaction> transactions = entry.getKey();
            // Căutăm tranzacția t în lista de tranzacții
            if (transactions.contains(t)) {
                return transactions; // Returnăm lista în care se află tranzacția t
            }
        }
        return null; // Dacă tranzacția nu a fost găsită
    }

}
