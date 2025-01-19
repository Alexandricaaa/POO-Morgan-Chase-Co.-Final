package org.poo.bank.commands.businessReport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Account;
import org.poo.fileio.CommandInput;

public interface ReportStrategy {

    void generateReport(CommandInput cmd, ArrayNode output, ObjectMapper objectMapper, Account account);
}
