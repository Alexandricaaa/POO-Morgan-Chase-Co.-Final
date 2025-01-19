package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class CashWithdrawal implements CommandPattern {
    @Override
    public void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank) {
        Exchange exchange = new Exchange(bank);
        User user = bank.getUsers().get(command.getEmail());
        Account account = bank.findAccountByCardNumber(command.getCardNumber());
        boolean found = false;


        if(account == null){
            Bank.addCommandNode(output, obj, "cashWithdrawal",
                    "Card not found", command.getTimestamp());
            return;
        }
        if(user == null){
            Bank.addCommandNode(output, obj, "cashWithdrawal",
                    "User not found", command.getTimestamp());
            return;
        }

        Card card = Bank.findCardInAccount(account, command);
        for(Account acc : user.getAccounts()){
            if(account.getAccount().equals(acc.getAccount())){
                found = true;
                break;
            }
        }
        if(!found || (card!=null && card.getStatus().equals("destroyed"))){
            Bank.addCommandNode(output, obj, "cashWithdrawal",
                    "Card not found", command.getTimestamp());
            return;
        }


        double amountRON = command.getAmount();
        double commission = Payment.commission(account.getPlanType(), amountRON);
        commission = exchange.findExchangeRate("RON", account.getCurrency());
        double amountNeeded = amountRON * exchange.findExchangeRate("RON", account.getCurrency());
        double total =  amountNeeded - commission;

        if(account.getBalance() < total){
            Transaction.error(command,user,"Insufficient funds");
            return;
        }
        account.setBalance(account.getBalance() - total);
        Transaction.amountInDescription(command,user,command.getAmount(), "Cash withdrawal of");


    }
}
