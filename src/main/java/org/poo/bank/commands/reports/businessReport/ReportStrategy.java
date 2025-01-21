package org.poo.bank.commands.reports.businessReport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

public interface ReportStrategy {
    /**
     * implements the logic for Strategy design Pattern
     * @param cmd
     * @param output
     * @param objectMapper
     */
    void generateReport(CommandInput cmd, ArrayNode output, ObjectMapper objectMapper);
}
