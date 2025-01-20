package org.poo.bank.commands.businessReport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.Account;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.fileio.CommandInput;

public class TransactionReportStrategy implements CommandPattern {



    public TransactionReportStrategy() {
    }

    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {

        ReportStrategy strategy = null;

        if(command.getType().equals("transaction")){
            strategy = new BusinessRepTransaction(bank);
        }
        else{
            strategy = new BusinessRepSpendings(bank);
        }
        strategy.generateReport(command, output, obj);
    }
}
