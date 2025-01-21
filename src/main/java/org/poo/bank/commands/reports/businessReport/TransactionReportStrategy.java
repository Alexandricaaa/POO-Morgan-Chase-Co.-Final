package org.poo.bank.commands.reports.businessReport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.fileio.CommandInput;

/**
 * Strategy Pattern
 */
public class TransactionReportStrategy implements CommandPattern {

    public TransactionReportStrategy() { }

    /**
     * the entry point
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {

        ReportStrategy strategy = null;

        if (command.getType().equals("transaction")) {
            strategy = new BusinessRepTransaction(bank);
        } else {
            strategy = new BusinessRepSpendings(bank);
        }
        strategy.generateReport(command, output, obj);
    }
}
