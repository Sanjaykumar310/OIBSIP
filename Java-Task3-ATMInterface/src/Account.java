import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String userId;
    private final String pin;
    private final String accountId;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String userId, String pin, String accountId, double initialBalance) {
        this.userId = userId;
        this.pin = pin;
        this.accountId = accountId;
        this.balance = initialBalance;
    }

    public String getUserId() { return userId; }
    public String getAccountId() { return accountId; }
    public double getBalance() { return balance; }

    public boolean authenticate(String user, String password) {
        return userId.equals(user) && pin.equals(password);
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        return true;
    }

    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        balance += amount;
        return true;
    }

    public void creditTransfer(double amount) {
        if (amount > 0) balance += amount;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
