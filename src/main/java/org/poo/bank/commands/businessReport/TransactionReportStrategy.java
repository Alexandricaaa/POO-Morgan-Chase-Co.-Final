package org.poo.bank.commands.businessReport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.fileio.CommandInput;

public class TransactionReportStrategy implements ReportStrategy {

    @Override
    public void generateReport(CommandInput cmd, ArrayNode output, ObjectMapper objectMapper, Account account) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", "businessReport");
        node.put("timestamp", cmd.getTimestamp());

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("IBAN", account.getAccount());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        outputNode.put("spending limit", account.getSpendingLimit());
        outputNode.put("deposit limit", account.getDepositLimit());
        outputNode.put("statistics type", "transaction");


    }
}
