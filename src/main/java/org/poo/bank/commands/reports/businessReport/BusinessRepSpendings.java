package org.poo.bank.commands.reports.businessReport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;
import java.util.*;
import java.util.stream.Collectors;

public class BusinessRepSpendings implements ReportStrategy {

    private final Bank bank;
    public BusinessRepSpendings(final Bank bank) {
        this.bank = bank;
    }

    @Override
    public void generateReport(final CommandInput command, final ArrayNode output,
                               final ObjectMapper objectMapper) {

        Account account = bank.findAccountByIBAN(command.getAccount());
        if (account == null) {
            return;
        }

        ObjectNode node = Node.createBusinessReportNode(command, account, objectMapper, "commerciant");

        List<Commerciant> commPerAcc = bank.getCommerciantsPerAcc().get(command.getAccount());
        if (commPerAcc != null) {
            commPerAcc = commPerAcc.stream()
                    .sorted(Comparator.comparing(Commerciant::getName))
                    .toList();
        }

        List<Transaction> businessTransactions = new ArrayList<>();
        for (User user : bank.getUsers().values()) {
            List<Transaction> userTransactions
                    = user.getTrPerAcc().getOrDefault(command.getAccount(),
                    new ArrayList<>());
            if (user.getEmployeeRole() != null
                    && user.getEmployeeRole().get(command.getAccount()) != null
                    && !user.getEmployeeRole().get(command.getAccount()).equals("owner")) {
                businessTransactions.addAll(userTransactions);
            }
        }

        List<Transaction> filteredTransactions = businessTransactions.stream()
                .filter(t -> t.getTimestamp()
                        >= command.getStartTimestamp()
                        && t.getTimestamp() <= command.getEndTimestamp())
                .toList();

        Map<Commerciant, Boolean> isProcessed = new LinkedHashMap<>();
        for (Commerciant comm : bank.getCommerciants()) {
            isProcessed.put(comm, false);
        }

        ArrayNode commArray = objectMapper.createArrayNode();

        if (commPerAcc != null) {
            for (Commerciant c : commPerAcc) {
                List<Transaction> commTransactions = filteredTransactions.stream()
                        .filter(t -> t.getCommerciant() != null
                                && c.getName().equals(t.getCommerciant()))
                        .collect(Collectors.toList());
                ArrayNode employeesArray = null;
                ArrayNode managersArray = null;
                if (!commTransactions.isEmpty()) {
                    ObjectNode commNode = objectMapper.createObjectNode();
                    double totalReceived = commTransactions.stream()
                            .mapToDouble(Transaction::getAmount)
                            .sum();

                    if (!isProcessed.get(c)) {
                         employeesArray = objectMapper.createArrayNode();
                         managersArray = objectMapper.createArrayNode();
                        isProcessed.put(c, true);

                        commArray.add(commNode);
                        commNode.put("commerciant", c.getName());
                        commNode.put("total received", totalReceived);
                        commNode.set("managers", managersArray);
                        commNode.set("employees", employeesArray);
                    }

                    List<String> employees = new ArrayList<>();
                    List<String> managers = new ArrayList<>();
                    for (User user : bank.getUsers().values()) {
                        List<Transaction> userTransactions = user.getTransactions();
                        for (Transaction t : commTransactions) {
                            if (userTransactions.contains(t)) {
                                if (user.getEmployeeRole().
                                        get(command.getAccount()).equals("employee")) {
                                    employees.add(user.getLastName() + " " + user.getFirstName());
                                }
                                if (user.getEmployeeRole().
                                        get(command.getAccount()).equals("manager")) {
                                    managers.add(user.getLastName() + " " + user.getFirstName());
                                }
                            }
                        }
                    }

                    if (employeesArray != null) {
                        employees.forEach(employeesArray::add);
                    }
                    if (managersArray != null) {
                        managers.forEach(managersArray::add);
                    }

                }
            }
        }
        ObjectNode outputNode = (ObjectNode) node.get("output");
        outputNode.set("commerciants", commArray);
        output.add(node);
    }
}