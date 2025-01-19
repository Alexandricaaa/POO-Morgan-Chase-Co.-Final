package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.fileio.CommandInput;
import org.poo.bank.User;
import org.poo.bank.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PrintTransactions implements CommandPattern {

    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        User user = bank.getUsers().get(command.getEmail());

        if (user == null) {
            // Dacă utilizatorul nu există, adăugăm un mesaj de eroare
            ObjectNode errorNode = obj.createObjectNode();
            errorNode.put("command", "printTransactions");
            errorNode.put("error", "User not found");
            errorNode.put("timestamp", command.getTimestamp());
            output.add(errorNode);
            return;
        }

        // Sortăm tranzacțiile utilizatorului în funcție de timestamp
        List<Transaction> transactions = new ArrayList<>(user.getTransactions());
        transactions.sort(Comparator.comparingInt(Transaction::getTimestamp));

        // Creăm nodul principal pentru output
        ObjectNode outputNode = obj.createObjectNode();
        outputNode.put("command", "printTransactions");
        outputNode.put("timestamp", command.getTimestamp());

        // Creăm un array pentru tranzacții
        ArrayNode transactionsArray = obj.createArrayNode();

        // Iterăm prin tranzacții și construim nodurile JSON pentru fiecare
        for (Transaction transaction : transactions) {
            ObjectNode transactionNode = Transaction.createTransactionOutputNode(obj, transaction);
            transactionsArray.add(transactionNode);
        }

        // Adăugăm array-ul de tranzacții în nodul principal
        outputNode.set("output", transactionsArray);
        output.add(outputNode);
    }

}
