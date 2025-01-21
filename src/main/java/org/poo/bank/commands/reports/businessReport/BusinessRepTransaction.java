package org.poo.bank.commands.reports.businessReport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BusinessRepTransaction implements ReportStrategy {

    private final Bank bank;
    public BusinessRepTransaction(final Bank bank) {
        this.bank = bank;
    }

    @Override
    public void generateReport(final CommandInput command,
                               final ArrayNode output, final ObjectMapper objectMapper) {

        Account account = bank.findAccountByIBAN(command.getAccount());
        if (account == null) {
            return;
        }

        ObjectNode node = Node.createBusinessReportNode(command, account,
                objectMapper, "transaction");
        ArrayNode managersArray = objectMapper.createArrayNode();
        ArrayNode employeesArray = objectMapper.createArrayNode();
        double totalSpentByEmployees = 0.0;
        double totalDepositedByEmployees = 0.0;

        List<User> employees = bank.getBusinessUsersPerAcc().get(command.getAccount());
        if (employees != null) {
            Set<User> uniqueEmployees = new LinkedHashSet<>(employees);

            for (User u : uniqueEmployees) {
                String role = u.getEmployeeRole().get(command.getAccount());
                if (role == null || role.equals("owner")) {
                    continue;
                }

                double spent = 0.0;
                double deposited = 0.0;

                ObjectNode userNode = objectMapper.createObjectNode();
                userNode.put("username", u.getLastName() + " " + u.getFirstName());

                List<Transaction> transactions = u.getTrPerAcc()
                        .getOrDefault(command.getAccount(), new ArrayList<>())
                        .stream()
                        .filter(t -> t.getTimestamp() >= command.getStartTimestamp()
                                && t.getTimestamp() <= command.getEndTimestamp())
                        .toList();

                for (Transaction t : transactions) {
                    if (t.getSpent() != null) {
                        if (role.equals("employee") && t.getSpent() <= account.getSpendingLimit()) {
                            spent += t.getSpent();
                        }
                        if (role.equals("manager")) {
                            spent += t.getSpent();
                        }
                    }
                    if (t.getDeposited() != null) {
                        if (role.equals("employee") && t.getDeposited()
                                <= account.getDepositLimit()) {
                            deposited += t.getDeposited();
                        }
                        if (role.equals("manager")) {
                            deposited += t.getDeposited();
                        }
                    }
                }

                userNode.put("spent", spent);
                userNode.put("deposited", deposited);

                if ("manager".equals(role)) {
                    managersArray.add(userNode);
                } else if ("employee".equals(role)) {
                    employeesArray.add(userNode);
                }
                totalSpentByEmployees += spent;
                totalDepositedByEmployees += deposited;
            }
        }

        ObjectNode outputNode = (ObjectNode) node.get("output");
        outputNode.set("managers", managersArray);
        outputNode.set("employees", employeesArray);
        outputNode.put("total spent", totalSpentByEmployees);
        outputNode.put("total deposited", totalDepositedByEmployees);
        output.add(node);
    }
}
