import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Bank {
    private final List<Account> accounts = new ArrayList<>();
    private static final String DATA_FILE = "atm-data.ser";

    public Bank() {
        // Load persisted data. If loading fails because the file is missing we fall back to demo accounts.
        // If the file exists but could not be read (e.g., permission error) we preserve it and start with an empty in‑memory list.
        // Corrupted files are backed up by loadData() and then removed, allowing fresh demo data to be saved.
        boolean loaded = loadData();
        if (!loaded) {
            File f = new File(DATA_FILE);
            if (!f.exists()) {
                // No data file – create demo accounts and persist them.
                accounts.add(new Account("user1", "1234", "ACC1001", 10000));
                accounts.add(new Account("user2", "5678", "ACC1002", 5000));
                saveData();
            } else {
                System.out.println("Existing data file could not be loaded; starting with empty account list.");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean loadData() {
        // Attempt to load persisted account data.
        // If the file is missing, corrupted, or cannot be deserialized we keep the existing
        // in‑memory accounts (which will be populated with demo data by the constructor).
        // Before discarding a corrupted file we make a backup copy.
        File dataFile = new File(DATA_FILE);
        if (!dataFile.exists()) {
            // No persisted data – treat as a fresh start.
            return false;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dataFile))) {
            accounts.addAll((List<Account>) in.readObject());
            System.out.println("Saved account data loaded.");
            return true;
        } catch (InvalidClassException | StreamCorruptedException e) {
            // The file exists but cannot be interpreted as a valid serialized object.
            System.out.println("Warning: ATM data file appears corrupted (" + e.getMessage() + ")");
            backupCorruptedFile(dataFile);
            return false;
        } catch (IOException | ClassNotFoundException e) {
            // General I/O problem – could be permission issue, disk error, etc.
            System.out.println("Error loading ATM data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create a backup of a corrupted data file so the user can inspect it.
     * The backup is named <original>.bak.timestamp.
     */
    private void backupCorruptedFile(File original) {
        try {
            String backupName = original.getName() + ".bak." + System.currentTimeMillis();
            File backup = new File(backupName);
            try (InputStream in = new FileInputStream(original);
                 OutputStream out = new FileOutputStream(backup)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            System.out.println("Corrupted ATM data has been backed up as " + backup.getName());
        } catch (IOException ex) {
            System.out.println("Failed to back up corrupted ATM data: " + ex.getMessage());
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
