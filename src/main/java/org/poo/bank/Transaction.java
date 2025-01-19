package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.poo.fileio.CommandInput;

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
    private double amountToSplit;
    //doar pentru splitPayment
    private String error;
    private boolean allAccepted = false;
    private String findSplitAcc;




    public boolean areAllAccountsAccepted() {
        // Verifică dacă toate conturile au acceptat
        for (Account account : involvedAccounts) {
            if (!account.isAccepted()) {
                return false;
            }
        }
        return true;
    }

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


        public TransactionBuilder findSplitAcc(String findSplitAcc) {
            this.findSplitAcc = findSplitAcc;
            return this;
        }

        public TransactionBuilder error(String error){
            this.error = error;
            return this;
        }
        public TransactionBuilder amountToSplit(double amount){
            this.amountToSplit = amount;
            return this;
        }

        public TransactionBuilder accountSplit(List<String> l){
            this.accountSplit = l;
            return this;
        }

        public TransactionBuilder splitType(String pay){
            this.splitType = pay;
            return this;
        }

        public TransactionBuilder amounts(List<Double> amounts) {
            this.amounts = amounts;
            return this;
        }

        public TransactionBuilder findTransaction(String findTransaction) {
            this.findTransaction = findTransaction;
            return this;
        }

        public TransactionBuilder accountIBAN(String accountIBAN) {
            this.accountIBAN = accountIBAN;
            return this;
        }

        public TransactionBuilder commerciant(String commerciant) {
            this.commerciant = commerciant;
            return this;
        }

        public TransactionBuilder amountWithCurrency(String amountWithCurrency) {
            this.amountWithCurrency = amountWithCurrency;
            return this;
        }

        public TransactionBuilder transferType(String transferType) {
            this.transferType = transferType;
            return this;
        }
        public TransactionBuilder sender(String sender) {
            this.sender = sender;
            return this;
        }
        public TransactionBuilder receiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public TransactionBuilder newPlanType(String newPlanType) {
            this.newPlanType = newPlanType;
            return this;
        }

        public TransactionBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public TransactionBuilder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public TransactionBuilder cardOwner(String cardOwner) {
            this.cardOwner = cardOwner;
            return this;
        }

        public TransactionBuilder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder savingsAccount(String savingsAccount) {
            this.savingsAccount = savingsAccount;
            return this;
        }

        public TransactionBuilder classicAccount(String classicAccount) {
            this.classicAccount = classicAccount;
            return this;
        }

        public TransactionBuilder account(String account){
            this.account = account;
            return this;
        }
        public TransactionBuilder description(String description){
            this.description = description;
            return this;
        }
        public TransactionBuilder email(String email){
             this.email = email;
             return this;
        }
        public TransactionBuilder command(String command){
            this.command = command;
            return this;
        }
        public TransactionBuilder timestamp(int timestamp){
            this.timestamp = timestamp;
            return this;
        }
        public Transaction build() {
            return new Transaction(this);
        }
    }

    public static  ObjectNode createTransactionOutputNode(ObjectMapper objectMapper, Transaction transaction) {
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        // Adăugăm câmpuri opționale dacă sunt disponibile
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
        if(transaction.getCommerciant()!=null){
            transactionNode.put("commerciant", transaction.getCommerciant());
        }
        if(transaction.getAmountWithCurrency()!=null){
            transactionNode.put("amount", transaction.getAmountWithCurrency());
        }
        if(transaction.getAccountIBAN()!=null){
            transactionNode.put("accountIBAN", transaction.getAccountIBAN());
        }

        if(transaction.getNewPlanType()!=null){
            transactionNode.put("newPlanType", transaction.getNewPlanType());
        }
        if(transaction.getCurrency()!=null){
            transactionNode.put("currency", transaction.getCurrency());
        }

        if(transaction.getSplitType()!=null){
            transactionNode.put("splitPaymentType", transaction.getSplitType());
        }
        if(transaction.getError()!=null){
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

        return transactionNode;
    }


    public static void error(CommandInput command, User user, String description){
        Transaction transaction = new Transaction.TransactionBuilder()
                .timestamp(command.getTimestamp())
                .description(description)
                .build();

        user.getTransactions().add(transaction);
    }
    public static void invalidAccType(CommandInput command, User user,  String error){
        Transaction t = new Transaction.TransactionBuilder()
                .description(error)
                .timestamp(command.getTimestamp())
                .account(command.getAccount())
                .build();
        user.getTransactions().add(t);
    }


    public static void addTransactionForWithdrawal(CommandInput command, User user, String classicAcc){
        Transaction tr = new Transaction.TransactionBuilder()
                .timestamp(command.getTimestamp())
                .description("Savings withdrawal")
                .savingsAccount(command.getAccount())
                .savingsAccount(classicAcc)
                .amount(command.getAmount())
                .build();
        user.getTransactions().add(tr);

        Transaction copy = new Transaction.TransactionBuilder()
                .timestamp(command.getTimestamp())
                .description("Savings withdrawal")
                .savingsAccount(command.getAccount())
                .savingsAccount(classicAcc)
                .amount(command.getAmount())
                .build();
        user.getTransactions().add(copy);
    }

    public static void deposit(CommandInput command, User user){
        Transaction t = new Transaction.TransactionBuilder()
                .email(user.getEmail())
                .account(command.getAccount())
                .timestamp(command.getTimestamp())
                .build();
        user.getTransactions().add(t);
    }

    public static void card(CommandInput command, User user, Card card, String description, String iban){
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(command.getTimestamp())
                .description(description)
                .cardNumber(card.getCardNumber())
                .cardOwner(command.getEmail())
                .account(iban)
                .build();
        user.getTransactions().add(t);
    }

    public static void addInterest(CommandInput c, User user, Account a, double amount){
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .description("Interest rate income")
                .currency(a.getCurrency())
                .amount(amount)
                .build();
        user.getTransactions().add(t);
    }

    public static void amountInDescription(CommandInput c, User user, double amount, String description){
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .description(description + amount)
                .amount(amount)
                .build();

        user.getTransactions().add(t);
    }

    public static void cardPayment(CommandInput c, User user, double amount, String commerciant, String iban){
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .description("Card payment")
                .amount(amount)
                .findTransaction(iban)
                .commerciant(commerciant)
                .build();

        user.getTransactions().add(t);
    }

    public static void upgradePlan(CommandInput c, User user, String newPlanType){
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .description("Upgrade plan")
                .newPlanType(newPlanType)
                .accountIBAN(c.getAccount())
                .build();
        user.getTransactions().add(t);
    }

    public static void receivedMoney(CommandInput c, User user, double amount,Account receiver, Account sender){

        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .amountWithCurrency(amount + " " +  receiver.getCurrency())
                .description(c.getDescription())
                .sender(sender.getAccount())
                .receiver(receiver.getAccount())
                .transferType("received")
                .build();
        user.getTransactions().add(t);

    }

    public static void sentMoney(CommandInput c, User user, double amount,Account receiver, Account sender){

        if(sender == null){
            return;
        }
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .amountWithCurrency(amount + " " +  sender.getCurrency())
                .description(c.getDescription())
                .sender(sender.getAccount())
                .receiver(receiver.getAccount())
                .transferType("sent")
                .build();

        user.getTransactions().add(t);

    }
    public static void splitCustom(CommandInput c, User user, String description, List<String> acc, Account a, List<Double> listAmount, double amount){

        Transaction t = new Transaction.TransactionBuilder()
                .findSplitAcc(a.getAccount())
                .currency(c.getCurrency())
                .amounts(listAmount)
                .timestamp(c.getTimestamp())
                .splitType("custom")
                .description(description)
                .accountSplit(acc)
                .amountToSplit(amount)
                .build();
        user.getTransactions().add(t);

    }


}
