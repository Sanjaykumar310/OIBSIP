import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public String toString() {
        return timestamp.format(
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        ) + " | " + type +
        " | Amount: Rs. " + String.format("%.2f", amount) +
        " | " + details;
    }
}
