package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;

@Data
public class Transaction {

    private String description;
    private String email;
    private int timestamp;
    private String command;
    private String account;
    private Double amount;
    private String currency;
    private String newPlanType;
    private String amountWithCurrency;
    private String transferType;
    private String commerciant;
    private String findTransaction; // e ibanul pentru spendingsReport


    private String savingsAccount;
    private String classicAccount;
    private String sender;
    private String receiver;

    private String cardOwner;
    private String cardNumber;

    //e pentru upgrade
    private String accountIBAN;

    //pentru split
    private List<String> accountSplit;
    private List<Account> involvedAccounts;
    private String splitType;
    private List<Double> amounts;
    private boolean accept = false;
    private boolean reject = false;
    //de aici scad din balance acc
    private double amountToSplit;
    //doar pentru splitPayment
    private String error;
    private boolean allAccepted = false;
    private String findSplitAcc;
    private Double amountEqual;
    private Boolean alreadyProcessed = false;

    //pentru businessReport
    private Double deposited;
    private Double spent;

    //pentru report
    private boolean ignore = false;


    //builder
    private Transaction(TransactionBuilder builder) {
        this.account = builder.account;
        this.description = builder.description;
        this.email = builder.email;
        this.command = builder.command;
        this.timestamp = builder.timestamp;
        this.savingsAccount = builder.savingsAccount;
        this.classicAccount = builder.classicAccount;
        this.amount = builder.amount;
        this.cardOwner = builder.cardOwner;
        this.cardNumber = builder.cardNumber;
        this.currency = builder.currency;
        this.newPlanType = builder.newPlanType;
        this.amountWithCurrency = builder.amountWithCurrency;
        this.transferType = builder.transferType;
        this.sender = builder.sender;
        this.receiver = builder.receiver;
        this.commerciant = builder.commerciant;
        this.accountIBAN = builder.accountIBAN;
        this.findTransaction = builder.findTransaction;
        this.accountSplit = builder.accountSplit;
        this.splitType = builder.splitType;
        this.amounts = builder.amounts;
        this.amountToSplit = builder.amountToSplit;
        this.error = builder.error;
        this.allAccepted = builder.allAccepted;
        this.findSplitAcc = builder.findSplitAcc;
        this.amountEqual = builder.amountEqual;
        this.deposited = builder.deposited;
        this.spent = builder.spent;
        this.ignore = builder.ignore;
        this.alreadyProcessed = builder.alreadyProcessed;

    }

    public static class TransactionBuilder {
        private String account;
        private String description;
        private String email;
        private String command;
        private int timestamp;
        private String savingsAccount;
        private String classicAccount;
        private Double amount;
        private String cardOwner;
        private String cardNumber;
        private String currency;
        private String newPlanType;
        private String amountWithCurrency;
        private String transferType;
        private String sender;
        private String receiver;
        private String commerciant;
        private String accountIBAN;
        private String findTransaction;
        private List<String> accountSplit = null;
        private String splitType;
        private List<Double> amounts;
        private double amountToSplit;
        private String error;
        private boolean allAccepted;
        private String findSplitAcc;
        private Double amountEqual;
        private Double deposited;
        private Double spent;
        private boolean ignore;
        private Boolean alreadyProcessed;


        public TransactionBuilder ignore(final Boolean ignore) {
            this.ignore = ignore;
            return this;
        }
        public TransactionBuilder deposited(final  Double deposited) {
            this.deposited = deposited;
            return this;
        }

        public TransactionBuilder spent(final  Double spent) {
            this.spent = spent;
            return this;
        }

        public TransactionBuilder amountEqual(final  double amountEqual) {
            this.amountEqual = amountEqual;
            return this;
        }

        public TransactionBuilder findSplitAcc(final  String findSplitAcc) {
            this.findSplitAcc = findSplitAcc;
            return this;
        }

        public TransactionBuilder error(final  String error){
            this.error = error;
            return this;
        }
        public TransactionBuilder amountToSplit(final  double amount){
            this.amountToSplit = amount;
            return this;
        }

        public TransactionBuilder accountSplit(final  List<String> l){
            this.accountSplit = l;
            return this;
        }

        public TransactionBuilder splitType(final  String pay){
            this.splitType = pay;
            return this;
        }

        public TransactionBuilder amounts(final  List<Double> amounts) {
            this.amounts = amounts;
            return this;
        }

        public TransactionBuilder findTransaction(final  String findTransaction) {
            this.findTransaction = findTransaction;
            return this;
        }

        public TransactionBuilder accountIBAN(final  String accountIBAN) {
            this.accountIBAN = accountIBAN;
            return this;
        }

        public TransactionBuilder commerciant(final  String commerciant) {
            this.commerciant = commerciant;
            return this;
        }

        public TransactionBuilder amountWithCurrency(final String amountWithCurrency) {
            this.amountWithCurrency = amountWithCurrency;
            return this;
        }

        public TransactionBuilder transferType(final String transferType) {
            this.transferType = transferType;
            return this;
        }
        public TransactionBuilder sender(final String sender) {
            this.sender = sender;
            return this;
        }
        public TransactionBuilder receiver(final String receiver) {
            this.receiver = receiver;
            return this;
        }

        public TransactionBuilder newPlanType(final String newPlanType) {
            this.newPlanType = newPlanType;
            return this;
        }

        public TransactionBuilder currency(final String currency) {
            this.currency = currency;
            return this;
        }

        public TransactionBuilder cardNumber(final String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public TransactionBuilder cardOwner(final String cardOwner) {
            this.cardOwner = cardOwner;
            return this;
        }

        public TransactionBuilder amount(final Double amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder savingsAccount(final String savingsAccount) {
            this.savingsAccount = savingsAccount;
            return this;
        }

        public TransactionBuilder classicAccount(final String classic) {
            this.classicAccount = classic;
            return this;
        }

        public TransactionBuilder account(final String account) {
            this.account = account;
            return this;
        }
        public TransactionBuilder description(final String description) {
            this.description = description;
            return this;
        }
        public TransactionBuilder email(final String email) {
            this.email = email;
            return this;
        }
        public TransactionBuilder command(final String command) {
            this.command = command;
            return this;
        }
        public TransactionBuilder timestamp(final int timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        public Transaction build() {
            return new Transaction(this);
        }
    }

    public static  ObjectNode createTransactionOutputNode(final ObjectMapper objectMapper, final Transaction transaction) {
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        if (transaction.getSender() != null) {
            transactionNode.put("senderIBAN", transaction.getSender());
        }
        if (transaction.getReceiver() != null) {
            transactionNode.put("receiverIBAN", transaction.getReceiver());
        }
        if (transaction.getAmount() != null) {
            transactionNode.put("amount", transaction.getAmount());
        }
        if (transaction.getTransferType() != null) {
            transactionNode.put("transferType", transaction.getTransferType());
        }
        if (transaction.getCardNumber() != null) {
            transactionNode.put("card", transaction.getCardNumber());
        }
        if (transaction.getCardOwner() != null) {
            transactionNode.put("cardHolder", transaction.getCardOwner());
        }
        if (transaction.getAccount() != null) {
            transactionNode.put("account", transaction.getAccount());
        }
        if(transaction.getCommerciant() != null) {
            transactionNode.put("commerciant", transaction.getCommerciant());
        }
        if(transaction.getAmountWithCurrency() != null) {
            transactionNode.put("amount", transaction.getAmountWithCurrency());
        }
        if(transaction.getAccountIBAN() != null) {
            transactionNode.put("accountIBAN", transaction.getAccountIBAN());
        }

        if(transaction.getNewPlanType() != null) {
            transactionNode.put("newPlanType", transaction.getNewPlanType());
        }
        if(transaction.getCurrency() != null) {
            transactionNode.put("currency", transaction.getCurrency());
        }

        if(transaction.getSplitType() != null) {
            transactionNode.put("splitPaymentType", transaction.getSplitType());
        }
        if(transaction.getError() != null) {
            transactionNode.put("error", transaction.getError());
        }
        if (transaction.getAmounts() != null) {
            ArrayNode amountsArray = objectMapper.createArrayNode();
            transaction.getAmounts().forEach(amountsArray::add);
            transactionNode.set("amountForUsers", amountsArray);
        }
        if (transaction.getAccountSplit() != null) {
            ArrayNode accSplitArray = objectMapper.createArrayNode();
            transaction.getAccountSplit().forEach(accSplitArray::add);
            transactionNode.set("involvedAccounts", accSplitArray);
        }
        if(transaction.getSavingsAccount() != null) {
            transactionNode.put("savingsAccountIBAN", transaction.getSavingsAccount());
        }
        if(transaction.getClassicAccount() != null) {
            transactionNode.put("classicAccountIBAN", transaction.getClassicAccount());
        }

        return transactionNode;
    }



}