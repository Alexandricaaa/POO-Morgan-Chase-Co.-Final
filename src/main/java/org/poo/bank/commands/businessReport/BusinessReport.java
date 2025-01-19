package org.poo.bank.commands.businessReport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Account;
import org.poo.bank.Bank;
import org.poo.bank.CommandPattern;
import org.poo.fileio.CommandInput;

public class BusinessReport implements CommandPattern {

    private final ReportStrategy reportStrategy;

    public BusinessReport( ReportStrategy reportStrategy) {
        this.reportStrategy = reportStrategy;
    }


    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        Account account = bank.findAccountByIBAN(command.getAccount());

        if (account == null) {
            return;
        }

        reportStrategy.generateReport(command, output, obj, account);
        // Folosim strategia aleasă
    }
}
