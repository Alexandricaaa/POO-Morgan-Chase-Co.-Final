package org.poo.bank.commands.businessReport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.*;
import java.util.stream.Collectors;

public class BusinessRepSpendings implements ReportStrategy{

    Bank bank;
    public BusinessRepSpendings(Bank bank){
        this.bank = bank;
    }

    @Override
    public void generateReport(CommandInput command, ArrayNode output, ObjectMapper objectMapper) {
        Account account = bank.findAccountByIBAN(command.getAccount());
        if (account == null) {
            return;
        }

        // Creăm nodul de bază pentru raport
        ObjectNode node = Node.createBusinessReportNode(command, account, objectMapper, "commerciant");

        // Preluăm lista comercianților pentru contul dat
        List<Commerciant> commPerAcc = bank.getCommerciantsPerAcc().get(command.getAccount());
        if (commPerAcc != null) {
            commPerAcc = commPerAcc.stream()
                    .sorted(Comparator.comparing(Commerciant::getName))
                    .toList();
        }

        // Preluăm toate tranzacțiile unice pentru cont
        Set<Transaction> uniqueTransactions = new HashSet<>();
        for (User user : bank.getUsers().values()) {
            for (Account a : user.getAccounts()) {
                if (a.getAccount().equals(command.getAccount())) {
                    List<Transaction> transactions = user.getTrPerAcc().getOrDefault(account.getAccount(), new ArrayList<>());
                    uniqueTransactions.addAll(transactions);
                }
            }
        }

        // Filtrăm tranzacțiile pe baza intervalului de timp
        Set<Transaction> filtered = uniqueTransactions.stream()
                .filter(t -> t.getTimestamp() >= command.getStartTimestamp() && t.getTimestamp() <= command.getEndTimestamp())
                .collect(Collectors.toSet());

        // Creăm lista de comercianți care au tranzacții asociate
        ArrayNode commArray = objectMapper.createArrayNode();
        if (commPerAcc != null) {
            for (Commerciant c : commPerAcc) {
                // Calculăm suma cheltuită pentru fiecare comerciant
                double sumPerComm = filtered.stream()
                        .filter(t -> t.getCommerciant() != null && c.getName().equals(t.getCommerciant()))
                        .mapToDouble(Transaction::getAmount)
                        .sum();

                // Adăugăm comerciantul doar dacă a avut tranzacții
                if (sumPerComm > 0) {
                    ObjectNode commNode = objectMapper.createObjectNode();
                    commNode.put("commerciant", c.getName());
                    commNode.put("spent", sumPerComm);
                    commArray.add(commNode);
                }
            }
        }

        // Adăugăm lista de comercianți (goală sau cu valori) în raport
        ObjectNode outputNode = (ObjectNode) node.get("output");
        outputNode.set("commerciants", commArray);
        output.add(node);
    }


}
