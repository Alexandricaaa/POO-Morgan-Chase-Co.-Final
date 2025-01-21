package org.poo.bank.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;

/**
 *  Upgrades the current plan if possible
 */
public class UpgradePlan implements CommandPattern {

    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {

        Exchange exchange = new Exchange(bank);
        ObjectNode node = Node.createNode(command.getCommand(), obj, command.getTimestamp());
        Account account = bank.findAccountByIBAN(command.getAccount());
        if (account == null) {
            Node.addErrorWithDescrip(obj, node,
                    "Account not found", command.getTimestamp());
            output.add(node);
            return;
        }
        String userEmail = bank.getEmailForAccountIBAN(command.getAccount());
        if (userEmail == null) {
            Node.addErrorToNode(obj, node,
                    "User not found", command.getTimestamp());
            output.add(node);
            return;
        }

        User user = bank.getUsers().get(userEmail);
        if (user == null) {
            Node.addErrorToNode(obj, node,
                    "User not found", command.getTimestamp());
            output.add(node);
            return;
        }

        if (account.getPlanType() != null) {
            if (account.getPlanType().equals(command.getNewPlanType())) {
                BuildOneTransaction.messageValidAcc(command, user,
                        "The user already has the " + account.getPlanType()
                                + " plan.", account.getAccount());
                return;
            }
        }

        if (Bank.isUpgrade(account.getPlanType(), command.getNewPlanType())) {
            if (account.getPlanType() == null) {
                return;
            }
            double sumInRON = Payment.getUpgradeFee(account.getPlanType(),
                    command.getNewPlanType());
            double rate = exchange.findExchangeRate("RON", account.getCurrency());
            double sumInAccCurrency = rate * sumInRON;

            if (account.getBalance() < sumInAccCurrency) {
                BuildOneTransaction.error(command, user, "Insufficient funds");
            } else {
                double newBalance = account.getBalance() - sumInAccCurrency;
                account.setBalance(newBalance);
                bank.updateAccountPlan(user, command.getNewPlanType());
                BuildOneTransaction.upgradePlan(command, user,
                        command.getNewPlanType(), command.getAccount());
            }
        }
    }
}
