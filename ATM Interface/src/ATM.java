import java.util.HashMap;
import java.util.Map;

/**
 * ATM.java
 * Entry point of the ATM Interface application.
 * Initializes user accounts and launches the ATM session.
 *
 * @author  Your Name
 * @version 1.0
 */
public class ATM {

    public static void main(String[] args) {

        // ── Seed user database ──────────────────────────────────────────
        Map<String, User> userDatabase = new HashMap<>();

        User alice = new User("U001", "1234",
                "Alice Johnson", 85_000.00);
        alice.addTransaction("Initial deposit       +₹85,000.00");

        User bob = new User("U002", "5678",
                "Bob Smith", 42_500.50);
        bob.addTransaction("Initial deposit       +₹42,500.50");

        User charlie = new User("U003", "9999",
                "Charlie Brown", 1_20_000.00);
        charlie.addTransaction("Initial deposit    +₹1,20,000.00");

        userDatabase.put(alice.getUserId(),   alice);
        userDatabase.put(bob.getUserId(),     bob);
        userDatabase.put(charlie.getUserId(), charlie);
        // ────────────────────────────────────────────────────────────────

        // Boot the ATM machine
        ATMController atm = new ATMController(userDatabase);
        atm.start();
    }
}