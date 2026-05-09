import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User.java
 * Represents a bank account / ATM card holder.
 * Stores credentials, balance, and full transaction history.
 *
 * @author  Your Name
 * @version 1.0
 */
public class User {

    // ── Fields ────────────────────────────────────────────────────────
    private final String userId;
    private final String pin;
    private final String fullName;
    private double balance;
    private final List<String> transactionHistory;

    private static final int MAX_HISTORY = 50; // keep last 50 records

    // ── Constructor ───────────────────────────────────────────────────
    public User(String userId, String pin, String fullName, double initialBalance) {
        if (userId == null || userId.isBlank())
            throw new IllegalArgumentException("User ID cannot be empty.");
        if (pin == null || pin.length() < 4)
            throw new IllegalArgumentException("PIN must be at least 4 digits.");
        if (initialBalance < 0)
            throw new IllegalArgumentException("Initial balance cannot be negative.");

        this.userId             = userId;
        this.pin                = pin;
        this.fullName           = fullName;
        this.balance            = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    // ── Credential validation ─────────────────────────────────────────
    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    // ── Balance operations ────────────────────────────────────────────
    public double getBalance() { return balance; }

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

    // ── Transaction history ───────────────────────────────────────────
    public void addTransaction(String record) {
        if (transactionHistory.size() >= MAX_HISTORY)
            transactionHistory.remove(0);          // evict oldest
        transactionHistory.add(record);
    }

    /** Returns an unmodifiable view so history cannot be altered externally. */
    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    // ── Getters ───────────────────────────────────────────────────────
    public String getUserId()  { return userId;   }
    public String getFullName(){ return fullName; }

    @Override
    public String toString() {
        return String.format("User[id=%s, name=%s, balance=%.2f]",
                userId, fullName, balance);
    }
}