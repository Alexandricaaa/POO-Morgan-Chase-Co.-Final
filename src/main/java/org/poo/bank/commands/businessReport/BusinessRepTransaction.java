package org.poo.bank.commands.businessReport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BusinessRepTransaction implements ReportStrategy{

    Bank bank;
    public BusinessRepTransaction(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void generateReport(CommandInput command, ArrayNode output, ObjectMapper objectMapper) {
        Account account = bank.findAccountByIBAN(command.getAccount());
        if (account == null) {
            return; // Contul nu a fost găsit
        }

        // Creează nodul principal pentru raport
        ObjectNode node = Node.createBusinessReportNode(command, account, objectMapper, "transaction");

        // Pregătește structuri pentru manageri și angajați
        ArrayNode managersArray = objectMapper.createArrayNode();
        ArrayNode employeesArray = objectMapper.createArrayNode();
        double totalSpentByEmployees = 0.0;
        double totalDepositedByEmployees = 0.0;

        // Găsește utilizatorii asociați IBAN-ului
        List<User> employees = bank.getBusinessUsersPerAcc().get(command.getAccount());

        if(employees!=null) {
            for (User u : employees) {
                String role = u.getEmployeeRole().get(command.getAccount());
                if (role == null || role.equals("owner")) {
                    continue; // Ignorăm proprietarul sau lipsa rolului
                }

                double spent = 0.0;
                double deposited = 0.0;

                // Creăm nod pentru utilizator
                ObjectNode userNode = objectMapper.createObjectNode();
                userNode.put("username", u.getLastName() + " " + u.getFirstName());

                // Filtrăm tranzacțiile relevante
                List<Transaction> transactions = u.getTrPerAcc()
                        .getOrDefault(command.getAccount(), new ArrayList<>())
                        .stream()
                        .filter(t -> t.getTimestamp() >= command.getStartTimestamp() && t.getTimestamp() <= command.getEndTimestamp())
                        .toList();

                // Calculăm sumele cheltuite și depuse
                for (Transaction t : transactions) {
                    if (t.getSpent() != null) {
                        if(role.equals("employee") && t.getSpent() <= account.getSpendingLimit()) {
                            spent += t.getSpent();
                        }
                        if(role.equals("manager")){
                            spent += t.getSpent();
                        }
                    }
                    if (t.getDeposited() != null) {
                        if(role.equals("employee") && t.getDeposited() <= account.getDepositLimit()) {
                            deposited += t.getDeposited();
                        }
                        if(role.equals("manager")){
                            deposited += t.getDeposited();
                        }
                    }
                }

                userNode.put("spent", spent);
                userNode.put("deposited", deposited);

                // Adăugăm utilizatorul la manageri sau angajați
                if ("manager".equals(role)) {
                    managersArray.add(userNode);
                } else if ("employee".equals(role)) {
                    employeesArray.add(userNode);
                }

                totalSpentByEmployees += spent;
                totalDepositedByEmployees += deposited;
            }
        }

        // Setăm datele finale în nodul de output
        ObjectNode outputNode = (ObjectNode) node.get("output");
        outputNode.set("managers", managersArray);
        outputNode.set("employees", employeesArray);
        outputNode.put("total spent", totalSpentByEmployees);
        outputNode.put("total deposited", totalDepositedByEmployees);

        // Adăugăm nodul în lista de ieșire
        output.add(node);
    }
}
