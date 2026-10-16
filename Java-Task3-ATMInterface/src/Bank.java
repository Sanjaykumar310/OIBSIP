
import java.util.ArrayList;
import java.util.List;

public class Bank {
    private final List<Account> accounts = new ArrayList<>();

    public Bank() {
        accounts.add(new Account("user1", "1234", "ACC1001", 10000.00));
        accounts.add(new Account("user2", "5678", "ACC1002", 5000.00));
    }

    public Account findByUserId(String userId) {
        for (Account account : accounts) {
            if (account.getUserId().equals(userId)) {
                return account;
            }
        }
        return null;
    }

    public Account findByAccountId(String accountId) {
        for (Account account : accounts) {
            if (account.getAccountId().equalsIgnoreCase(accountId)) {
                return account;
            }
        }
        return null;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
