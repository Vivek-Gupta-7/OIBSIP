/**
 * UIHelper.java
 * Static utility class that provides reusable console-formatting helpers.
 * Keeps the controller and service classes clean and readable.
 *
 * @author  Your Name
 * @version 1.0
 */
public class UIHelper {

    private UIHelper() {}   // utility class – no instantiation

    public static final String LINE =
            "═══════════════════════════════════════════════════════════════";
    public static final String THIN =
            "───────────────────────────────────────────────────────────────";

    // ── Banners ───────────────────────────────────────────────────────
    public static void printHeader(String title) {
        System.out.println("\n" + LINE);
        System.out.printf( "  %-61s%n", title);
        System.out.println(LINE);
    }

    public static void printSubHeader(String title) {
        System.out.println("\n" + THIN);
        System.out.printf( "  %s%n", title);
        System.out.println(THIN);
    }

    public static void printSuccess(String msg) {
        System.out.println("\n  ✔ " + msg);
    }

    public static void printError(String msg) {
        System.out.println("\n  ✘ " + msg);
    }

    public static void printInfo(String msg) {
        System.out.println("  ℹ  " + msg);
    }

    // ── Menu ──────────────────────────────────────────────────────────
    public static void printMainMenu(String name, double balance) {
        printHeader("JavaBank ATM — Main Menu");
        System.out.printf("  Account Holder : %s%n", name);
        System.out.printf("  Available Bal  : ₹%,.2f%n", balance);
        System.out.println(THIN);
        System.out.println("   [1]  Transaction History");
        System.out.println("   [2]  Withdraw");
        System.out.println("   [3]  Deposit");
        System.out.println("   [4]  Transfer");
        System.out.println("   [5]  Quit / Log Out");
        System.out.println(THIN);
        System.out.print("   Select option : ");
    }

    // ── Pause helper (simulates ATM processing delay) ─────────────────
    public static void pause(long millis) {
        try { Thread.sleep(millis); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // ── Receipt divider ───────────────────────────────────────────────
    public static void printReceiptLine() {
        System.out.println("  " + THIN);
    }
}