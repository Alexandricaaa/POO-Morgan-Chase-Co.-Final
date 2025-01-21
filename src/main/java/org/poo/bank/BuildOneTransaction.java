package org.poo.bank;
import org.poo.fileio.CommandInput;
import java.util.ArrayList;
import java.util.List;
/**
 * A utility class that builds various types of transactions for
 * a user based on command inputs using builder.
 */
public class BuildOneTransaction {
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void error(final CommandInput command,
                             final User user,
                             final String description) {
        Transaction transaction = new Transaction.TransactionBuilder()
                .timestamp(command.getTimestamp())
                .description(description)
                .build();

        user.getTransactions().add(transaction);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void messageValidAcc(final CommandInput command,
                                       final User user,
                                       final String description,
                                       final String iban) {
        Transaction transaction = new Transaction.TransactionBuilder()
                .timestamp(command.getTimestamp())
                .description(description)
                .build();

        user.getTransactions().add(transaction);
        user.getTrPerAcc().computeIfAbsent(iban, k -> new ArrayList<>()).add(transaction);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void invalidAccType(final CommandInput command,
                                      final User user,
                                      final String error) {
        Transaction t = new Transaction.TransactionBuilder()
                .description(error)
                .timestamp(command.getTimestamp())
                .account(command.getAccount())
                .build();
        user.getTransactions().add(t);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void addTransactionForWithdrawal(final CommandInput command,
                                                   final User user, final  String savings,
                                                   final String classicAcc) {
        Transaction tr = new Transaction.TransactionBuilder()
                .timestamp(command.getTimestamp())
                .description("Savings withdrawal")
                .savingsAccount(savings)
                .classicAccount(classicAcc)
                .amount(command.getAmount())
                .build();
        user.getTransactions().add(tr);
        user.getTrPerAcc().computeIfAbsent(savings, k -> new ArrayList<>()).add(tr);

        Transaction copy = new Transaction.TransactionBuilder()
                .timestamp(command.getTimestamp())
                .description("Savings withdrawal")
                .savingsAccount(savings)
                .classicAccount(classicAcc)
                .amount(command.getAmount())
                .build();
        user.getTransactions().add(copy);
        user.getTrPerAcc().computeIfAbsent(classicAcc, k -> new ArrayList<>()).add(tr);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void deposit(final CommandInput command, final User user) {
        Transaction t = new Transaction.TransactionBuilder()
                .email(user.getEmail())
                .account(command.getAccount())
                .timestamp(command.getTimestamp())
                .deposited(command.getAmount())
                .ignore(true)
                .build();
        user.getTransactions().add(t);
        String accountIBAN = command.getAccount();

        user.getTrPerAcc().computeIfAbsent(accountIBAN, k -> new ArrayList<>()).add(t);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void card(final CommandInput command, final User user,
                            final Card card, final String description,
                            final String iban) {
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(command.getTimestamp())
                .description(description)
                .cardNumber(card.getCardNumber())
                .cardOwner(command.getEmail())
                .account(iban)
                .build();
        user.getTransactions().add(t);
        user.getTrPerAcc().computeIfAbsent(iban, k -> new ArrayList<>()).add(t);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void addInterest(final CommandInput c, final User user,
                                   final Account a, final double amount) {
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .description("Interest rate income")
                .currency(a.getCurrency())
                .amount(amount)
                .build();
        user.getTransactions().add(t);
        user.getTrPerAcc().computeIfAbsent(c.getAccount(), k -> new ArrayList<>()).add(t);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void amountInDescription(final CommandInput c, final User user,
                                           final String description) {
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .description(description)
                .amount(c.getAmount())
                .build();

        user.getTransactions().add(t);
        user.getTrPerAcc().computeIfAbsent(c.getAccount(), k -> new ArrayList<>()).add(t);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void interestChange(final CommandInput c, final User user,
                                      final String description) {
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .description(description)
                .build();

        user.getTransactions().add(t);
        user.getTrPerAcc().computeIfAbsent(c.getAccount(), k -> new ArrayList<>()).add(t);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void cardPayment(final CommandInput c, final User user,
                                   final double amount, final String commerciant,
                                   final String iban) {
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .description("Card payment")
                .amount(amount)
                .findTransaction(iban)
                .commerciant(commerciant)
                .spent(amount)
                .build();

        user.getTransactions().add(t);
        user.getTrPerAcc().computeIfAbsent(iban, k -> new ArrayList<>()).add(t);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void upgradePlan(final CommandInput c,
                                   final User user, final String newPlanType,
                                   final String iban) {
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .description("Upgrade plan")
                .newPlanType(newPlanType)
                .accountIBAN(iban)
                .build();
        user.getTransactions().add(t);
        user.getTrPerAcc().computeIfAbsent(c.getAccount(), k -> new ArrayList<>()).add(t);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void receivedMoney(final CommandInput c,
                                     final User user,
                                     final double amount,
                                     final Account receiver,
                                     final Account sender) {
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .amountWithCurrency(amount + " " +  receiver.getCurrency())
                .description(c.getDescription())
                .sender(sender.getAccount())
                .receiver(receiver.getAccount())
                .transferType("received")
                .build();
        user.getTransactions().add(t);
        user.getTrPerAcc().computeIfAbsent(receiver.getAccount(), k -> new ArrayList<>()).add(t);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void sentMoney(final CommandInput c,
                                 final User user,
                                 final double amount,
                                 final String receiver,
                                 final Account sender) {
        if (sender == null) {
            return;
        }
        Transaction t = new Transaction.TransactionBuilder()
                .timestamp(c.getTimestamp())
                .amountWithCurrency(amount + " " +  sender.getCurrency())
                .description(c.getDescription())
                .sender(sender.getAccount())
                .receiver(receiver)
                .transferType("sent")
                .spent(amount)
                .build();

        user.getTransactions().add(t);
        String accountIBAN = c.getAccount();
        user.getTrPerAcc().computeIfAbsent(accountIBAN, k -> new ArrayList<>()).add(t);
    }
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void splitCustom(final CommandInput c,
                                   final User user,
                                   final String description,
                                   final List<String> acc,
                                   final Account a,
                                   final List<Double> listAmount,
                                   final double amount) {

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
    /**
     * Creates the transaction with the specified fields being initialized
     */
    public static void splitEqual(final CommandInput c,
                                  final User user,
                                  final String description,
                                  final List<String> acc,
                                  final Account a,
                                  final double amount,
                                  final String err) {

        Transaction t = new Transaction.TransactionBuilder()
                .findSplitAcc(a.getAccount())
                .currency(c.getCurrency())
                .timestamp(c.getTimestamp())
                .splitType("equal")
                .description(description)
                .accountSplit(acc)
                .amountEqual(amount)
                .amount(c.getAmount() / acc.size())
                .error(err)
                .build();

        user.getTransactions().add(t);
    }
}
