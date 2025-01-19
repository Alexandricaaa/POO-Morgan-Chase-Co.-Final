package org.poo.bank;

import org.poo.bank.commands.*;
import org.poo.fileio.CommandInput;

public class Factory {


    public static CommandPattern createCommand(CommandInput command) {
        return switch (command.getCommand()) {

            case "printUsers" -> new PrintUsers();

            case "addAccount" -> new AddAccount();

            case "withdrawSavings" -> new WithdrawSavings();

            case "addFunds" -> new AddFunds();

            case "createCard" -> new CreateCard();

            case "deleteAccount" -> new DeleteAccount();

            case "createOneTimeCard" -> new CreateOneTimeCard();

            //nu e terminat
            case "upgradePlan" -> new UpgradePlan();

            case "addInterest" -> new AddInterest();

            case "setMinimumBalance" -> new SetMinimumBalance();

            case "addNewBusinessAssociate" -> new AddNewBusinessAssociate();

            case "changeSpendingLimit" -> new ChangeSpendingLimit();

            case "changeDepositLimit" -> new ChangeDepositLimit();

            case "changeInterestRate" -> new ChangeInterestRate();

            case "setAlias" -> new SetAlias();

            case "report" -> new Report();

           // case "businessReport" -> new BusinessReport();

            //case "spendingsReport" -> new SpendingsReport();

           // case "splitPayment" -> new SplitPayment();

           // case "acceptSplitPayment" -> new AcceptSplitPayment();

            //case "rejectSplitPayment" -> new RejectSplitPayment();

            case "cashWithdrawal" -> new CashWithdrawal();

            case "sendMoney" -> new SendMoney();

            case "payOnline" -> new PayOnline();

            case "printTransactions" ->new PrintTransactions();

            default -> null;
        };
    }
}
