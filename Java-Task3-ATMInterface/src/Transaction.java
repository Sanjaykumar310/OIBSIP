
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String type;
    private final double amount;
    private final String details;
    private final LocalDateTime timestamp;

    public Transaction(String type, double amount, String details) {
        this.type = type;
        this.amount = amount;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        return timestamp.format(formatter)
                + " | " + type
                + " | Amount: Rs. " + String.format("%.2f", amount)
                + " | " + details;
    }
}
