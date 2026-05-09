import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * ATMController.java
 * Core controller that drives the ATM session.
 * Handles the menu loop and delegates each operation to its own method.
 *
 * Operations supported:
 *   1. Transaction History
 *   2. Withdraw
 *   3. Deposit
 *   4. Transfer
 *   5. Quit / Log Out
 *
 * @author  Your Name
 * @version 1.0
 */
public class ATMController {

    // ── Constants ─────────────────────────────────────────────────────
    private static final double MIN_BALANCE    = 500.00;   // minimum balance rule
    private static final double MAX_WITHDRAWAL = 50_000.00;
    private static final double MAX_DEPOSIT    = 2_00_000.00;
    private static final double MAX_TRANSFER   = 1_00_000.00;
    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy  HH:mm:ss");

    // ── State ─────────────────────────────────────────────────────────
    private final Map<String, User> userDatabase;
    private final Scanner           sc;
    private User                    currentUser;

    // ── Constructor ───────────────────────────────────────────────────
    public ATMController(Map<String, User> userDatabase) {
        this.userDatabase = userDatabase;
        this.sc           = new Scanner(System.in);
    }

    // ── Public entry point ────────────────────────────────────────────
    public void start() {
        printBootScreen();

        boolean keepRunning = true;
        while (keepRunning) {
            // Authentication
            AuthService auth = new AuthService(userDatabase, sc);
            currentUser = auth.authenticate();

            if (currentUser == null) {
                System.out.println("\n  Thank you for using JavaBank ATM. Goodbye!\n");
                break;
            }

            // Session menu loop
            keepRunning = sessionLoop();
        }

        sc.close();
    }

    // ── Session loop ──────────────────────────────────────────────────
    private boolean sessionLoop() {
        while (true) {
            UIHelper.printMainMenu(currentUser.getFullName(), currentUser.getBalance());
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> showHistory();
                case "2" -> withdraw();
                case "3" -> deposit();
                case "4" -> transfer();
                case "5" -> {
                    logout();
                    return askAnotherSession();
                }
                default  -> UIHelper.printError("Invalid option. Please choose 1 – 5.");
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // OPERATION 1 — Transaction History
    // ═════════════════════════════════════════════════════════════════
    private void showHistory() {
        UIHelper.printSubHeader("Transaction History  —  " + currentUser.getFullName());

        List<String> history = currentUser.getTransactionHistory();

        if (history.isEmpty()) {
            UIHelper.printInfo("No transactions recorded yet.");
        } else {
            System.out.println();
            int idx = 1;
            for (String record : history) {
                System.out.printf("  %2d.  %s%n", idx++, record);
            }
        }

        System.out.printf("%n  Current Balance : ₹%,.2f%n", currentUser.getBalance());
        UIHelper.printReceiptLine();
        pressEnterToContinue();
    }

    // ═════════════════════════════════════════════════════════════════
    // OPERATION 2 — Withdraw
    // ═════════════════════════════════════════════════════════════════
    private void withdraw() {
        UIHelper.printSubHeader("Cash Withdrawal");
        System.out.printf("  Available Balance : ₹%,.2f%n", currentUser.getBalance());
        System.out.printf("  Max per txn       : ₹%,.2f%n", MAX_WITHDRAWAL);
        System.out.printf("  Min Balance Rule  : ₹%,.2f must remain%n%n", MIN_BALANCE);

        double amount = readPositiveAmount("  Enter amount to withdraw : ₹");
        if (amount == -1) return;

        // Validations
        if (amount > MAX_WITHDRAWAL) {
            UIHelper.printError(String.format(
                    "Amount exceeds single-transaction limit of ₹%,.2f.", MAX_WITHDRAWAL));
            pressEnterToContinue();
            return;
        }
        if ((currentUser.getBalance() - amount) < MIN_BALANCE) {
            UIHelper.printError(String.format(
                    "Insufficient funds. A minimum balance of ₹%,.2f must be maintained.",
                    MIN_BALANCE));
            pressEnterToContinue();
            return;
        }

        UIHelper.printInfo("Processing withdrawal… ");
        UIHelper.pause(1200);

        currentUser.withdraw(amount);

        // Record
        Transaction txn = new Transaction(
                Transaction.Type.WITHDRAWAL, amount, currentUser.getBalance(), "");
        currentUser.addTransaction(txn.toReceiptLine());

        // Receipt
        printReceipt("WITHDRAWAL", amount, currentUser.getBalance());
        pressEnterToContinue();
    }

    // ═════════════════════════════════════════════════════════════════
    // OPERATION 3 — Deposit
    // ═════════════════════════════════════════════════════════════════
    private void deposit() {
        UIHelper.printSubHeader("Cash / Cheque Deposit");
        System.out.printf("  Current Balance : ₹%,.2f%n", currentUser.getBalance());
        System.out.printf("  Max per txn     : ₹%,.2f%n%n", MAX_DEPOSIT);

        double amount = readPositiveAmount("  Enter amount to deposit : ₹");
        if (amount == -1) return;

        if (amount > MAX_DEPOSIT) {
            UIHelper.printError(String.format(
                    "Amount exceeds single-transaction limit of ₹%,.2f.", MAX_DEPOSIT));
            pressEnterToContinue();
            return;
        }

        UIHelper.printInfo("Processing deposit… ");
        UIHelper.pause(1200);

        currentUser.deposit(amount);

        Transaction txn = new Transaction(
                Transaction.Type.DEPOSIT, amount, currentUser.getBalance(), "");
        currentUser.addTransaction(txn.toReceiptLine());

        printReceipt("DEPOSIT", amount, currentUser.getBalance());
        pressEnterToContinue();
    }

    // ═════════════════════════════════════════════════════════════════
    // OPERATION 4 — Transfer
    // ═════════════════════════════════════════════════════════════════
    private void transfer() {
        UIHelper.printSubHeader("Fund Transfer");
        System.out.printf("  Your Balance  : ₹%,.2f%n", currentUser.getBalance());
        System.out.printf("  Max per txn   : ₹%,.2f%n%n", MAX_TRANSFER);

        // Beneficiary
        System.out.print("  Enter beneficiary User ID : ");
        String targetId = sc.nextLine().trim();

        if (targetId.equalsIgnoreCase(currentUser.getUserId())) {
            UIHelper.printError("You cannot transfer funds to your own account.");
            pressEnterToContinue();
            return;
        }

        if (!userDatabase.containsKey(targetId)) {
            UIHelper.printError("Beneficiary account not found.");
            pressEnterToContinue();
            return;
        }

        User target = userDatabase.get(targetId);
        System.out.printf("  Beneficiary   : %s%n%n", target.getFullName());

        // Amount
        double amount = readPositiveAmount("  Enter amount to transfer : ₹");
        if (amount == -1) return;

        if (amount > MAX_TRANSFER) {
            UIHelper.printError(String.format(
                    "Amount exceeds single-transaction limit of ₹%,.2f.", MAX_TRANSFER));
            pressEnterToContinue();
            return;
        }
        if ((currentUser.getBalance() - amount) < MIN_BALANCE) {
            UIHelper.printError(String.format(
                    "Insufficient funds. A minimum balance of ₹%,.2f must be maintained.",
                    MIN_BALANCE));
            pressEnterToContinue();
            return;
        }

        // Confirm
        System.out.printf("%n  ┌─ Confirm Transfer ───────────────────────────────┐%n");
        System.out.printf("  │  To      : %-38s│%n", target.getFullName());
        System.out.printf("  │  Amount  : ₹%-37,.2f│%n", amount);
        System.out.printf("  └──────────────────────────────────────────────────┘%n");
        System.out.print("  Confirm? (Y/N) : ");
        String confirm = sc.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            UIHelper.printInfo("Transfer cancelled.");
            pressEnterToContinue();
            return;
        }

        UIHelper.printInfo("Processing transfer… ");
        UIHelper.pause(1500);

        currentUser.withdraw(amount);
        target.deposit(amount);

        // Record on both sides
        String ts = LocalDateTime.now().format(TS_FMT);

        Transaction outTxn = new Transaction(
                Transaction.Type.TRANSFER_OUT, amount, currentUser.getBalance(),
                "To: " + target.getFullName());
        currentUser.addTransaction(outTxn.toReceiptLine());

        Transaction inTxn = new Transaction(
                Transaction.Type.TRANSFER_IN, amount, target.getBalance(),
                "From: " + currentUser.getFullName());
        target.addTransaction(inTxn.toReceiptLine());

        // Receipt
        System.out.println();
        UIHelper.printReceiptLine();
        System.out.println("  *** TRANSFER RECEIPT ***");
        UIHelper.printReceiptLine();
        System.out.printf("  Date/Time     : %s%n", ts);
        System.out.printf("  To Account    : %s  (%s)%n",
                target.getFullName(), target.getUserId());
        System.out.printf("  Amount Sent   : ₹%,.2f%n", amount);
        System.out.printf("  Your Balance  : ₹%,.2f%n", currentUser.getBalance());
        UIHelper.printReceiptLine();
        UIHelper.printSuccess("Transfer Successful!");
        pressEnterToContinue();
    }

    // ═════════════════════════════════════════════════════════════════
    // OPERATION 5 — Quit / Log Out
    // ═════════════════════════════════════════════════════════════════
    private void logout() {
        UIHelper.printSubHeader("Session Ended");
        System.out.printf("  Account Holder  : %s%n",   currentUser.getFullName());
        System.out.printf("  Closing Balance : ₹%,.2f%n", currentUser.getBalance());
        System.out.println("  Thank you for banking with JavaBank!");
        UIHelper.printReceiptLine();
        UIHelper.pause(1000);
        currentUser = null;
    }

    private boolean askAnotherSession() {
        System.out.print("\n  Insert card for a new session? (Y/N) : ");
        String ans = sc.nextLine().trim();
        return ans.equalsIgnoreCase("Y");
    }

    // ── Helpers ───────────────────────────────────────────────────────

    /**
     * Reads a positive double from the user.
     * Returns -1 if input is invalid (caller decides what to do).
     */
    private double readPositiveAmount(String prompt) {
        System.out.print(prompt);
        String input = sc.nextLine().trim().replaceAll(",", "");
        try {
            double val = Double.parseDouble(input);
            if (val <= 0) throw new NumberFormatException();
            return val;
        } catch (NumberFormatException e) {
            UIHelper.printError("Invalid amount. Please enter a positive number.");
            pressEnterToContinue();
            return -1;
        }
    }

    private void printReceipt(String operation, double amount, double balanceAfter) {
        String ts = LocalDateTime.now().format(TS_FMT);
        System.out.println();
        UIHelper.printReceiptLine();
        System.out.printf("  *** %s RECEIPT ***%n", operation);
        UIHelper.printReceiptLine();
        System.out.printf("  Date/Time       : %s%n", ts);
        System.out.printf("  Amount          : ₹%,.2f%n", amount);
        System.out.printf("  Closing Balance : ₹%,.2f%n", balanceAfter);
        UIHelper.printReceiptLine();
        UIHelper.printSuccess(operation + " Successful!");
    }

    private void pressEnterToContinue() {
        System.out.print("\n  Press ENTER to return to menu…");
        sc.nextLine();
    }

    private void printBootScreen() {
        System.out.println(UIHelper.LINE);
        System.out.println("         ██╗ █████╗ ██╗   ██╗ █████╗      █████╗ ████████╗███╗   ███╗");
        System.out.println("         ██║██╔══██╗██║   ██║██╔══██╗    ██╔══██╗╚══██╔══╝████╗ ████║");
        System.out.println("         ██║███████║██║   ██║███████║    ███████║   ██║   ██╔████╔██║");
        System.out.println("    ██   ██║██╔══██║╚██╗ ██╔╝██╔══██║    ██╔══██║   ██║   ██║╚██╔╝██║");
        System.out.println("    ╚█████╔╝██║  ██║ ╚████╔╝ ██║  ██║    ██║  ██║   ██║   ██║ ╚═╝ ██║");
        System.out.println("     ╚════╝ ╚═╝  ╚═╝  ╚═══╝  ╚═╝  ╚═╝    ╚═╝  ╚═╝  ╚═╝   ╚═╝     ╚═╝");
        System.out.println(UIHelper.LINE);
        System.out.println("                     JavaBank ATM Interface v1.0");
        System.out.println("              Secure • Reliable • Always Available");
        System.out.println(UIHelper.LINE);
        UIHelper.pause(1500);
    }
}