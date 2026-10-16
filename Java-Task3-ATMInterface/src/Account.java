
public class Account {
    private final String userId;
    private final String pin;
    private final String accountId;
    private double balance;

    public Account(String userId, String pin, String accountId,
                   double initialBalance) {
        this.userId = userId;
        this.pin = pin;
        this.accountId = accountId;
        this.balance = initialBalance;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public double getBalance() {
        return balance;
    }

    public boolean authenticate(String enteredUserId, String enteredPin) {
        return userId.equals(enteredUserId)
                && pin.equals(enteredPin);
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        return true;
    }

    public void creditTransfer(double amount) {
        balance += amount;
    }
}
