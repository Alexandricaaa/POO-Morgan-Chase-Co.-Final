package org.poo.bank.commands.financeHandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.*;
import org.poo.fileio.CommandInput;
import java.util.ArrayList;

public class SendMoney implements CommandPattern {
    @Override
    public void execute(final CommandInput command, final ObjectMapper obj,
                        final ArrayNode output, final Bank bank) {

        Exchange exchange = new Exchange(bank);
        Account sender = bank.findAccountByIBAN(command.getAccount());
        Account receiver = bank.findAccountByIBAN(command.getReceiver());
        User user = bank.getUsers().get(command.getEmail());
        Commerciant sendToComm = null;

        if (sender == null) {
            if (bank.getAccountAlias().get(command.getAccount()) != null) {
                sender = bank.findAccountByIBAN(bank.getAccountAlias().get(command.getAccount()));
            }
        }
        if (receiver == null) {
            receiver = bank.findAccountByIBAN(bank.getAccountAlias().get(command.getReceiver()));
        }
         sendToComm = bank.getCommerciants().stream()
                .filter(commerciant -> commerciant.getAccount().equals(command.getReceiver()))
                .findFirst()
                .orElse(null);

        if ((sender == null || receiver == null) && sendToComm == null) {
            Node.addCommandNode(output, obj, "sendMoney",
                    "User not found", command.getTimestamp());
            return;
        }

        double cashback = 0.0;
        double amountInRON = command.getAmount()
                * exchange.findExchangeRate(sender.getCurrency(), "RON");
        double commissionInRON = Payment.commission(sender.getPlanType(), amountInRON);
        double commissionInCurr = exchange.findExchangeRate("RON", sender.getCurrency())
                * commissionInRON;

        if (sendToComm != null) {
            ArrayList<Commerciant> commerciants = bank.getCommerciantsPerAcc().
                    get(sender.getAccount());

            if (commerciants == null) {
                commerciants = new ArrayList<>();
                bank.getCommerciantsPerAcc().put(sender.getAccount(), commerciants);
            }

            commerciants.add(sendToComm);
            sender.setThresholdAmount(sender.getThresholdAmount() + command.getAmount());
            Account copy = sender;
            Commerciant copyComm = sendToComm;
            cashback = bank.getCommerciants().stream()
                    .filter(comm -> comm.getName().equals(copyComm.getName())
                            && comm.getCashbackStrategy().equals("spendingThreshold"))
                    .findFirst()
                    .map(comm -> {
                        double sum = copy.getThresholdAmount()
                                * exchange.findExchangeRate(copy.getCurrency(), "RON");
                        return Payment.threshold(sum, user, copy) * amountInRON;
                    })
                    .orElse(0.0);
        }
        cashback = exchange.findExchangeRate("RON", sender.getCurrency()) * cashback;
        double total = command.getAmount() + commissionInCurr - cashback;

        if (sender.getBalance() < total) {
            BuildOneTransaction.error(command, user, "Insufficient funds");
            return;
        }
        sender.setBalance(sender.getBalance() - total);

        if (sendToComm == null) {
            double receivedAmount = command.getAmount()
                    * exchange.findExchangeRate(sender.getCurrency(), receiver.getCurrency());
            receiver.setBalance(receiver.getBalance() + receivedAmount);
            String mail2 = bank.getEmailForAccountIBAN(receiver.getAccount());
            User user2 = bank.getUsers().get(mail2);
            BuildOneTransaction.receivedMoney(command, user2, receivedAmount, receiver, sender);
        } else {
            BuildOneTransaction.sentMoney(command, user, command.getAmount(),
                    sendToComm.getAccount(), sender);
        }
        if (receiver != null) {
            BuildOneTransaction.sentMoney(command, user, command.getAmount(),
                    receiver.getAccount(), sender);
        }
    }
}
