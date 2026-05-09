import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaction.java
 * Immutable value object that captures a single ATM transaction event.
 * Provides a formatted string suitable for display and logging.
 *
 * @author  Your Name
 * @version 1.0
 */
public class Transaction {

    // ── Supported transaction types ───────────────────────────────────
    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN
    }

    // ── Fields ────────────────────────────────────────────────────────
    private final Type   type;
    private final double amount;
    private final double balanceAfter;
    private final String note;               // e.g. "Transferred to U002"
    private final LocalDateTime timestamp;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy  HH:mm:ss");

    // ── Constructor ───────────────────────────────────────────────────
    public Transaction(Type type, double amount, double balanceAfter, String note) {
        this.type         = type;
        this.amount       = amount;
        this.balanceAfter = balanceAfter;
        this.note         = (note == null ? "" : note);
        this.timestamp    = LocalDateTime.now();
    }

    // ── Formatted display ─────────────────────────────────────────────
    /**
     * Returns a single-line receipt string, e.g.
     *   [28-May-2025  14:32:05]  DEPOSIT         +₹5,000.00   Bal: ₹90,000.00
     */
    public String toReceiptLine() {
        String sign   = (type == Type.DEPOSIT || type == Type.TRANSFER_IN) ? "+" : "-";
        String label  = String.format("%-15s", type.name());
        String amt    = String.format("%s₹%,.2f", sign, amount);
        String bal    = String.format("Bal: ₹%,.2f", balanceAfter);
        String extra  = note.isEmpty() ? "" : "  [" + note + "]";

        return String.format("[%s]  %s  %-18s  %s%s",
                timestamp.format(FMT), label, amt, bal, extra);
    }

    // ── Getters ───────────────────────────────────────────────────────
    public Type          getType()         { return type;         }
    public double        getAmount()       { return amount;       }
    public double        getBalanceAfter() { return balanceAfter; }
    public String        getNote()         { return note;         }
    public LocalDateTime getTimestamp()    { return timestamp;    }

    @Override
    public String toString() { return toReceiptLine(); }
}