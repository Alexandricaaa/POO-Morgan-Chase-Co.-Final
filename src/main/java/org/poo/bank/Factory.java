package org.poo.bank;

import org.poo.bank.commands.*;
import org.poo.bank.commands.reports.businessReport.TransactionReportStrategy;
import org.poo.bank.commands.financeHandle.*;
import org.poo.bank.commands.reports.Report;
import org.poo.bank.commands.reports.SpendingsReport;
import org.poo.bank.commands.splitPayment.AcceptSplitPayment;
import org.poo.bank.commands.splitPayment.RejectSplitPayment;
import org.poo.bank.commands.splitPayment.SplitPayment;
import org.poo.fileio.CommandInput;
/**
 * A factory class responsible for creating instances of various command patterns
 * based on the provided command input.
 */
public class Factory {
    /**
     * Creates and returns a specific command object based on the given command input.
     *
     * @param command The input command that specifies which command object to create.
     * @return The corresponding CommandPattern object, or null if no matching command is found.
     */
    public static CommandPattern createCommand(final CommandInput command) {

        return switch (command.getCommand()) {

            case "printUsers" -> new PrintUsers();

            case "addAccount" -> new AddAccount();

            case "withdrawSavings" -> new WithdrawSavings();

            case "addFunds" -> new AddFunds();

            case "createCard" -> new CreateCard();

            case "deleteAccount" -> new DeleteAccount();

            case "createOneTimeCard" -> new CreateOneTimeCard();

            case "upgradePlan" -> new UpgradePlan();

            case "addInterest" -> new AddInterest();

            case "setMinimumBalance" -> new SetMinimumBalance();

            case "addNewBusinessAssociate" -> new AddNewBusinessAssociate();

            case "changeSpendingLimit" -> new ChangeSpendingLimit();

            case "changeDepositLimit" -> new ChangeDepositLimit();

            case "changeInterestRate" -> new ChangeInterestRate();

            case "setAlias" -> new SetAlias();

            case "report" -> new Report();

            case "businessReport" -> new TransactionReportStrategy();

            case "spendingsReport" -> new SpendingsReport();


            case "splitPayment" -> new SplitPayment();


            case "acceptSplitPayment" -> new AcceptSplitPayment();


            case "rejectSplitPayment" -> new RejectSplitPayment();

            case "cashWithdrawal" -> new CashWithdrawal();

            case "sendMoney" -> new SendMoney();

            case "payOnline" -> new PayOnline();

            case "printTransactions" -> new PrintTransactions();

            default -> null;
        };
    }
}
