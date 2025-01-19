package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

import static org.poo.bank.Exchange.findExchangeRate;

public class WithdrawSavings implements CommandPattern {


    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        User user = bank.getUsers().get(command.getEmail());
        Account account = new Account();
        Account classic = new Account();

        double withdrawAmount = 0.0;

        if (user == null) {
            String email = bank.findUserEmailByIBAN(command.getAccount());
            user = bank.getUsers().get(email);
            if(user == null) {
                return;
            }
        }
        for(Account a : user.getAccounts()) {
            if(a.getAccount().equals(command.getAccount())){
                account = a;
                break;
            }
        }
        if(account==null){
            Transaction.error(command, user,"Account not found" );
            return;
        }

        if(!account.getAccountType().equals("savings")){
            Transaction.invalidAccType(command, user, "Account is not of type savings.");
            return;
        }

        if(User.userAge(user.getBirthDate()) < 21){
            Transaction.error(command, user, "You don't have the minimum age required.");
            return;
        }

        int ok = 0;
        for(Account a : user.getAccounts()) {
            if (a.getAccountType().equals("classic")) {
                classic = a;
                ok = 1;
                double rate = findExchangeRate(command.getCurrency(), account.getCurrency());
                double amount = rate * command.getAmount();

                if (amount > account.getBalance()) {
                    Transaction.invalidAccType(command, user, "Insufficient funds");
                    return;
                } else {
                    a.setBalance(a.getBalance() + amount);
                    account.setBalance(account.getBalance() - amount);

                }
            }
        }

        if(ok==0){
            Transaction.error(command, user, "You do not have a classic account.");
            return;
        }
        Transaction.addTransactionForWithdrawal(command, user, classic.getAccount());
    }
}
