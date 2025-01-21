package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
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
    private String findTransaction;
    private String savingsAccount;
    private String classicAccount;
    private String sender;
    private String receiver;
    private String cardOwner;
    private String cardNumber;
    private String accountIBAN;

    private List<String> accountSplit;
    private List<Account> involvedAccounts;
    private String splitType;
    private List<Double> amounts;
    private boolean accept = false;
    private boolean reject = false;
    private double amountToSplit;
    private String error;
    private boolean allAccepted = false;
    private String findSplitAcc;
    private Double amountEqual;
    private Boolean alreadyProcessed = false;

    private Double deposited;
    private Double spent;
    private boolean ignore = false;


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


        public final TransactionBuilder ignore(final Boolean ignored) {
            this.ignore = ignored;
            return this;
        }
        /**
         * Sets the amount for report and returns the builder instance.
         */
        public final TransactionBuilder deposited(final  Double depositedSum) {
            this.deposited = depositedSum;
            return this;
        }
        /**
         * Sets the amount for report and returns the builder instance.
         */
        public final TransactionBuilder spent(final  Double spentSum) {
            this.spent = spentSum;
            return this;
        }
        /**
         * Sets the amount for equalSplit and returns the builder instance.
         */
        public final TransactionBuilder amountEqual(final  double amount) {
            this.amountEqual = amount;
            return this;
        }
        /**
         * Sets the found account and returns the builder instance.
         */
        public final TransactionBuilder findSplitAcc(final  String find) {
            this.findSplitAcc = find;
            return this;
        }
        /**
         * Sets an error and returns the builder instance.
         */
        public final TransactionBuilder error(final  String err){
            this.error = err;
            return this;
        }
        /**
         * Sets the amount that every account should pay and returns the builder instance.
         */
        public final TransactionBuilder amountToSplit(final  double amount){
            this.amountToSplit = amount;
            return this;
        }
        /**
         * Sets accounts involved in splitPayment and returns the builder instance.
         */
        public final TransactionBuilder accountSplit(final  List<String> l){
            this.accountSplit = l;
            return this;
        }
        /**
         * Sets split Type and returns the builder instance.
         */
        public final TransactionBuilder splitType(final  String pay){
            this.splitType = pay;
            return this;
        }
        /**
         * Sets the amounts for every account involved in split and returns the builder instance.
         */
        public final TransactionBuilder amounts(final  List<Double> sum) {
            this.amounts = sum;
            return this;
        }
        /**
         * set the found transaction and returns the builder instance.
         */
        public final TransactionBuilder findTransaction(final  String find) {
            this.findTransaction = find;
            return this;
        }
        /**
         * Sets the account and returns the builder instance.
         */
        public final TransactionBuilder accountIBAN(final  String account) {
            this.accountIBAN = account;
            return this;
        }
        /**
         * Sets the commerciant and returns the builder instance.
         */
        public final TransactionBuilder commerciant(final  String comm) {
            this.commerciant = comm;
            return this;
        }

        /**
         * Sets the amount with currency and returns the builder instance.
         */
        public final TransactionBuilder amountWithCurrency(final String amountCurr) {
            this.amountWithCurrency = amountCurr;
            return this;
        }
        /**
         * Sets the transder type and returns the builder instance.
         */
        public final TransactionBuilder transferType(final String transfer) {
            this.transferType = transfer;
            return this;
        }
        /**
         * Sets the sender Account for SplitPayment and returns the builder instance.
         */
        public final TransactionBuilder sender(final String s) {
            this.sender = s;
            return this;
        }
        /**
         * Sets the receiver Account for SplitPayment and returns the builder instance.
         */
        public final TransactionBuilder receiver(final String rec) {
            this.receiver = rec;
            return this;
        }

        /**
         * Sets the plan Type and returns the builder instance.
         */
        public final TransactionBuilder newPlanType(final String newType) {
            this.newPlanType = newType;
            return this;
        }
        /**
         * Sets the currency of an account and returns the builder instance.
         */
        public final TransactionBuilder currency(final String curr) {
            this.currency = curr;
            return this;
        }
        /**
         * Sets the card number and returns the builder instance.
         */
        public final TransactionBuilder cardNumber(final String cardN) {
            this.cardNumber = cardN;
            return this;
        }
        /**
         * Sets the owner of the card and returns the builder instance.
         */
        public final TransactionBuilder cardOwner(final String card) {
            this.cardOwner = card;
            return this;
        }
        /**
         * Sets the amount and returns the builder instance.
         */
        public final TransactionBuilder amount(final Double sum) {
            this.amount = sum;
            return this;
        }
        /**
         * Sets the account of type savings and returns the builder instance.
         */
        public final TransactionBuilder savingsAccount(final String savings) {
            this.savingsAccount = savings;
            return this;
        }
        /**
         * Sets the classic account and returns the builder instance.
         */
        public final TransactionBuilder classicAccount(final String classic) {
            this.classicAccount = classic;
            return this;
        }
        /**
         * Sets the account and returns the builder instance.
         */
        public final TransactionBuilder account(final String acc) {
            this.account = acc;
            return this;
        }
        /**
         * Sets the description and returns the builder instance.
         */
        public final TransactionBuilder description(final String desc) {
            this.description = desc;
            return this;
        }
        /**
         * Sets the email and returns the builder instance.
         */
        public final TransactionBuilder email(final String mail) {
            this.email = mail;
            return this;
        }
        /**
         * Sets the command and returns the builder instance.
         */
        public final TransactionBuilder command(final String c) {
            this.command = c;
            return this;
        }
        /**
         * Sets the timestamp and returns the builder instance.
         */
        public final TransactionBuilder timestamp(final int time) {
            this.timestamp = time;
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
        if (transaction.getCommerciant() != null) {
            transactionNode.put("commerciant", transaction.getCommerciant());
        }
        if (transaction.getAmountWithCurrency() != null) {
            transactionNode.put("amount", transaction.getAmountWithCurrency());
        }
        if (transaction.getAccountIBAN() != null) {
            transactionNode.put("accountIBAN", transaction.getAccountIBAN());
        }

        if (transaction.getNewPlanType() != null) {
            transactionNode.put("newPlanType", transaction.getNewPlanType());
        }
        if (transaction.getCurrency() != null) {
            transactionNode.put("currency", transaction.getCurrency());
        }

        if (transaction.getSplitType() != null) {
            transactionNode.put("splitPaymentType", transaction.getSplitType());
        }
        if (transaction.getError() != null) {
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
        if (transaction.getSavingsAccount() != null) {
            transactionNode.put("savingsAccountIBAN", transaction.getSavingsAccount());
        }
        if (transaction.getClassicAccount() != null) {
            transactionNode.put("classicAccountIBAN", transaction.getClassicAccount());
        }

        return transactionNode;
    }



}