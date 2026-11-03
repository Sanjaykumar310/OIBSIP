import java.util.Scanner;

public class ATM {
    private final Bank bank;
    private final Scanner scanner;

    public ATM(Bank bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("========== JAVA ATM ==========");

        Account account = login();

        if (account == null) {
            System.out.println("Access denied. Too many failed attempts.");
            return;
        }

        System.out.println("Login successful. Welcome, " + account.getUserId());

        boolean running = true;

        while (running) {
            System.out.println("\n========== ATM MENU ==========");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Check Balance");
            System.out.println("6. Quit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": showHistory(account); break;
                case "2": withdraw(account); break;
                case "3": deposit(account); break;
                case "4": transfer(account); break;
                case "5":
                    System.out.printf("Current balance: Rs. %.2f%n",
                        account.getBalance());
                    break;
                case "6":
                    bank.saveData();
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private Account login() {
        for (int i = 1; i <= 3; i++) {
            System.out.print("Enter User ID: ");
            String user = scanner.nextLine().trim();

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.findByUserId(user);

            if (account != null && account.authenticate(user, pin))
                return account;

            System.out.println("Incorrect User ID or PIN. Attempts remaining: "
                + (3 - i));
        }
        return null;
    }

    private double readAmount(String message) {
        System.out.print(message);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return -1;
        }
    }

    private void deposit(Account account) {
        double amount = readAmount("Enter deposit amount: Rs. ");

        if (account.deposit(amount)) {
            account.addTransaction(new Transaction(
                "DEPOSIT", amount, "Cash deposit successful"));
            bank.saveData();
            System.out.printf("Deposit successful. Balance: Rs. %.2f%n",
                account.getBalance());
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    private void withdraw(Account account) {
        double amount = readAmount("Enter withdrawal amount: Rs. ");

        if (amount <= 0) {
            System.out.println("Invalid amount.");
        } else if (amount > account.getBalance()) {
            System.out.println("Insufficient Funds.");
        } else if (account.withdraw(amount)) {
            account.addTransaction(new Transaction(
                "WITHDRAWAL", amount, "Cash withdrawal successful"));
            bank.saveData();
            System.out.printf("Withdrawal successful. Balance: Rs. %.2f%n",
                account.getBalance());
        }
    }

    private void transfer(Account sender) {
        System.out.print("Enter recipient account ID: ");
        String id = scanner.nextLine().trim();

        Account recipient = bank.findByAccountId(id);

        if (recipient == null || recipient == sender) {
            System.out.println("Invalid recipient account.");
            return;
        }

        double amount = readAmount("Enter transfer amount: Rs. ");

        if (amount <= 0 || amount > sender.getBalance()) {
            System.out.println("Invalid amount or insufficient funds.");
            return;
        }

        sender.withdraw(amount);
        recipient.creditTransfer(amount);

        sender.addTransaction(new Transaction(
            "TRANSFER SENT", amount,
            "To account " + recipient.getAccountId()));

        recipient.addTransaction(new Transaction(
            "TRANSFER RECEIVED", amount,
            "From account " + sender.getAccountId()));

        bank.saveData();

        System.out.println("Transfer successful.");
        System.out.printf("Your new balance: Rs. %.2f%n",
            sender.getBalance());
    }

    private void showHistory(Account account) {
        System.out.println("\n====== TRANSACTION HISTORY ======");

        if (account.getTransactions().isEmpty()) {
            System.out.println("No transactions recorded for this account.");
            return;
        }

        int i = 1;
        for (Transaction t : account.getTransactions())
            System.out.println((i++) + ". " + t);
    }
}
