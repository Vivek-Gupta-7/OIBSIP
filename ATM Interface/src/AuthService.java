import java.util.Map;
import java.util.Scanner;

/**
 * AuthService.java
 * Handles user authentication with a 3-attempt lockout policy.
 * Separates security logic from the main ATM controller.
 *
 * @author  Your Name
 * @version 1.0
 */
public class AuthService {

    private static final int MAX_ATTEMPTS = 3;

    private final Map<String, User> db;
    private final Scanner           sc;

    public AuthService(Map<String, User> db, Scanner sc) {
        this.db = db;
        this.sc = sc;
    }

    /**
     * Presents the login prompt and returns the authenticated User,
     * or null if the user exhausts all attempts or chooses to quit.
     */
    public User authenticate() {
        UIHelper.printHeader("Welcome to JavaBank ATM");
        System.out.println("  Press ENTER on User ID prompt to exit.\n");

        // ── Step 1 : enter user ID ────────────────────────────────────
        System.out.print("  Enter User ID  : ");
        String userId = sc.nextLine().trim();

        if (userId.isEmpty()) return null;          // graceful exit

        if (!db.containsKey(userId)) {
            System.out.println("\n  ✘ User ID not found. Please contact your bank.");
            UIHelper.pause(1800);
            return null;
        }

        User user = db.get(userId);

        // ── Step 2 : verify PIN (max 3 tries) ─────────────────────────
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.print("  Enter PIN      : ");
            String pin = sc.nextLine().trim();

            if (user.validatePin(pin)) {
                System.out.printf("%n  ✔ Welcome, %s!%n", user.getFullName());
                UIHelper.pause(900);
                return user;
            }

            int remaining = MAX_ATTEMPTS - attempt;
            if (remaining > 0)
                System.out.printf("  ✘ Incorrect PIN. %d attempt(s) remaining.%n", remaining);
            else
                System.out.println("\n  ✘ Card blocked. Too many incorrect attempts.\n"
                        + "  Please contact your bank for assistance.");
        }
        return null;
    }
}