
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ATM {
    private final Bank bank;
    private final Scanner scanner;
    private final List<Transaction> history = new ArrayList<>();

    public ATM(Bank bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("================================");
        System.out.println("          JAVA ATM");
        System.out.println("================================");

        Account account = login();

        if (account == null) {
            System.out.println("Access denied. Too many failed attempts.");
            return;
        }

        System.out.println("\nLogin successful. Welcome, "
                + account.getUserId() + "!");

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
                case "1":
                    showHistory();
                    break;
                case "2":
                    withdraw(account);
                    break;
                case "3":
                    deposit(account);
                    break;
                case "4":
                    transfer(account);
                    break;
                case "5":
                    System.out.printf("Current balance: Rs. %.2f%n",
                            account.getBalance());
                    break;
                case "6":
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-6.");
            }
        }
    }

    private Account login() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("Enter User ID: ");
            String userId = scanner.nextLine().trim();

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.findByUserId(userId);

            if (account != null && account.authenticate(userId, pin)) {
                return account;
            }

            System.out.println("Incorrect User ID or PIN. Attempts remaining: "
                    + (3 - attempt));
        }

        return null;
    }

    private void withdraw(Account account) {
        double amount = readAmount("Enter withdrawal amount: Rs. ");

        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        if (amount > account.getBalance()) {
            System.out.println("Insufficient Funds.");
            return;
        }

        if (account.withdraw(amount)) {
            history.add(new Transaction("WITHDRAWAL", amount,
                    "Cash withdrawal successful"));
            System.out.printf("Withdrawal successful. Balance: Rs. %.2f%n",
                    account.getBalance());
        }
    }

    private void deposit(Account account) {
        double amount = readAmount("Enter deposit amount: Rs. ");

        if (account.deposit(amount)) {
            history.add(new Transaction("DEPOSIT", amount,
                    "Cash deposit successful"));
            System.out.printf("Deposit successful. Balance: Rs. %.2f%n",
                    account.getBalance());
        } else {
            System.out.println("Amount must be greater than zero.");
        }
    }

    private void transfer(Account sender) {
        System.out.print("Enter recipient account ID: ");
        String recipientId = scanner.nextLine().trim();

        Account recipient = bank.findByAccountId(recipientId);

        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        if (recipient == sender) {
            System.out.println("You cannot transfer money to your own account.");
            return;
        }

        double amount = readAmount("Enter transfer amount: Rs. ");

        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        if (amount > sender.getBalance()) {
            System.out.println("Insufficient Funds.");
            return;
        }

        if (sender.withdraw(amount)) {
            recipient.creditTransfer(amount);

            history.add(new Transaction("TRANSFER SENT", amount,
                    "To account " + recipient.getAccountId()));

            history.add(new Transaction("TRANSFER RECEIVED", amount,
                    "From account " + sender.getAccountId()));

            System.out.println("Transfer successful.");
            System.out.printf("Your new balance: Rs. %.2f%n",
                    sender.getBalance());
        }
    }

    private double readAmount(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a numeric value.");
            return -1;
        }
    }

    private void showHistory() {
        System.out.println("\n====== TRANSACTION HISTORY ======");

        if (history.isEmpty()) {
            System.out.println("No transactions recorded in this session.");
            return;
        }

        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }
}
