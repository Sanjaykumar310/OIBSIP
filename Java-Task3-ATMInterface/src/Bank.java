import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Bank {
    private final List<Account> accounts = new ArrayList<>();
    private static final String DATA_FILE = "atm-data.ser";

    public Bank() {
        if (!loadData()) {
            accounts.add(new Account("user1", "1234", "ACC1001", 10000));
            accounts.add(new Account("user2", "5678", "ACC1002", 5000));
            saveData();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean loadData() {
        try (ObjectInputStream in =
             new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            accounts.addAll((List<Account>) in.readObject());
            System.out.println("Saved account data loaded.");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    public void saveData() {
        try (ObjectOutputStream out =
             new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(accounts);
        } catch (IOException e) {
            System.out.println("Data saving error: " + e.getMessage());
        }
    }

    public Account findByUserId(String userId) {
        for (Account a : accounts)
            if (a.getUserId().equals(userId)) return a;
        return null;
    }

    public Account findByAccountId(String id) {
        for (Account a : accounts)
            if (a.getAccountId().equalsIgnoreCase(id)) return a;
        return null;
    }
}
