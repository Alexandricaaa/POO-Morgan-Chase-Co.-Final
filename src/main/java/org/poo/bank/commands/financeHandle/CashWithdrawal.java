package org.poo.bank.commands.financeHandle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

public class CashWithdrawal implements CommandPattern {
    /**
     *
     * @param command
     * @param obj
     * @param output
     * @param bank
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {
        Exchange exchange = new Exchange(bank);
        User user = bank.getUsers().get(command.getEmail());
        Account account = bank.getAccountUsingCardNumber(command.getCardNumber());
        boolean found = false;

        if (account == null) {
            Node.addCommandNode(output, obj, "cashWithdrawal",
                    "Card not found", command.getTimestamp());
            return;
        }
        if (user == null) {
            Node.addCommandNode(output, obj, "cashWithdrawal",
                    "User not found", command.getTimestamp());
            return;
        }

        Card card = Bank.findCardInAccount(account, command);
        for (Account acc : user.getAccounts()) {
            if (account.getAccount().equals(acc.getAccount())) {
                found = true;
                break;
            }
        }

        if (!found || (card != null && card.getStatus().equals("destroyed"))) {
            Node.addCommandNode(output, obj, "cashWithdrawal",
                    "Card not found", command.getTimestamp());
            return;
        }

        double amountRON = command.getAmount();
        double commission = Payment.commission(account.getPlanType(), amountRON);
        double totalRON = amountRON + commission;
        double amountNeeded = totalRON * exchange.findExchangeRate("RON", account.getCurrency());
        double total =  amountNeeded;

        if (account.getBalance() < total) {
            BuildOneTransaction.error(command, user, "Insufficient funds");
            return;
        }

        account.setBalance(account.getBalance() - total);
        BuildOneTransaction.amountInDescription(command, user,
                "Cash withdrawal of " + command.getAmount());
    }
}
