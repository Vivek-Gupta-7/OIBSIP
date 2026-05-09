import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ATM_GUI.java
 * Java Swing GUI frontend for the JavaBank ATM.
 * Directly uses the existing User, Transaction, AuthService logic.
 * Run this file instead of ATM.java to get the visual interface.
 *
 * @author  Your Name
 * @version 2.0
 */
public class ATM_GUI extends JFrame {

    // ── Colors ────────────────────────────────────────────────────
    static final Color BG        = new Color(10, 15, 10);
    static final Color CARD      = new Color(17, 24, 17);
    static final Color CARD2     = new Color(22, 32, 22);
    static final Color GREEN     = new Color(0, 230, 118);
    static final Color GREEN_D   = new Color(0, 180, 90);
    static final Color MUTED     = new Color(100, 150, 100);
    static final Color BORDER    = new Color(30, 50, 30);
    static final Color RED       = new Color(239, 83, 80);
    static final Color AMBER     = new Color(255, 193, 7);
    static final Color TEXT      = new Color(220, 240, 220);

    // ── Fonts ─────────────────────────────────────────────────────
    static final Font  FONT_TITLE  = new Font("Segoe UI", Font.BOLD,   18);
    static final Font  FONT_SUB    = new Font("Segoe UI", Font.PLAIN,  12);
    static final Font  FONT_LABEL  = new Font("Segoe UI", Font.PLAIN,  11);
    static final Font  FONT_BTN    = new Font("Segoe UI", Font.BOLD,   13);
    static final Font  FONT_AMOUNT = new Font("Segoe UI", Font.BOLD,   26);
    static final Font  FONT_MONO   = new Font("Consolas", Font.PLAIN,  12);

    // ── State ─────────────────────────────────────────────────────
    private final Map<String, User> db;
    private User   currentUser;
    private int    loginAttempts = 0;

    // ── Main panel (CardLayout) ───────────────────────────────────
    private final CardLayout    cards = new CardLayout();
    private final JPanel        mainPanel = new JPanel(cards);

    // ── Shared references ──────────────────────────────────────────
    private JLabel  lblBalance, lblName, lblFooterUser, lblClock;
    private JLabel  lblLoginErr, lblWdErr, lblDepErr, lblTrErr;
    private JTextField  fldUserId, fldTrBenef;
    private JPasswordField fldPin, fldWdAmt, fldDepAmt, fldTrAmt;
    private JPanel  txnListPanel;
    private JPanel  receiptPanel;
    private JLabel  receiptMsg;

    // transfer confirm
    private JLabel confName, confUid, confAmt, confBalAfter;

    // ── Constructor ───────────────────────────────────────────────
    public ATM_GUI(Map<String, User> db) {
        this.db = db;
        setTitle("JavaBank ATM v2.0");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 680);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildHeader(),   BorderLayout.NORTH);
        add(buildScreen(),   BorderLayout.CENTER);
        add(buildFooter(),   BorderLayout.SOUTH);

        startClock();
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════
    // LAYOUT BUILDERS
    // ══════════════════════════════════════════════════════════════

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(18, 20, 10, 20));

        JLabel logo = new JLabel("🏧  JavaBank ATM");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(GREEN);

        JLabel sub = new JLabel("SECURE BANKING INTERFACE");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(MUTED);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        left.add(logo); left.add(sub);

        lblClock = styledLabel("--:--:--", FONT_MONO, MUTED);
        p.add(left,     BorderLayout.WEST);
        p.add(lblClock, BorderLayout.EAST);
        return p;
    }

    private JPanel buildScreen() {
        mainPanel.setBackground(CARD);
        mainPanel.setBorder(new CompoundBorder(
            new EmptyBorder(0, 14, 0, 14),
            new LineBorder(BORDER, 1, true)
        ));

        mainPanel.add(buildLoginPage(),          "login");
        mainPanel.add(buildMenuPage(),           "menu");
        mainPanel.add(buildWithdrawPage(),       "withdraw");
        mainPanel.add(buildDepositPage(),        "deposit");
        mainPanel.add(buildTransferPage(),       "transfer");
        mainPanel.add(buildTransferConfirmPage(),"transfer_confirm");
        mainPanel.add(buildHistoryPage(),        "history");
        mainPanel.add(buildReceiptPage(),        "receipt");
        mainPanel.add(buildLogoutPage(),         "logout");

        cards.show(mainPanel, "login");
        return mainPanel;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(8, 20, 12, 20));

        JLabel secure = styledLabel("🔐  SECURE CONNECTION", FONT_LABEL, new Color(40, 80, 40));
        lblFooterUser = styledLabel("Not logged in", FONT_LABEL, MUTED);

        p.add(secure,        BorderLayout.WEST);
        p.add(lblFooterUser, BorderLayout.EAST);
        return p;
    }

    // ══════════════════════════════════════════════════════════════
    // PAGE BUILDERS
    // ══════════════════════════════════════════════════════════════

    // ── Login ─────────────────────────────────────────────────────
    private JPanel buildLoginPage() {
        JPanel p = darkPage();

        p.add(pageTitle("🔐  Cardholder Login"), "gbc");

        p.add(fieldLabel("USER ID"), "gbc");
        fldUserId = darkTextField("e.g. U001");
        p.add(fldUserId, "gbc");

        p.add(fieldLabel("PIN"), "gbc");
        fldPin = darkPasswordField("4-digit PIN");
        p.add(fldPin, "gbc");

        lblLoginErr = errorLabel();
        p.add(lblLoginErr, "gbc");

        p.add(primaryBtn("Insert Card  →", e -> doLogin()), "gbc");

        JLabel hint = styledLabel("Demo: U001/1234  •  U002/5678  •  U003/9999",
                new Font("Consolas", Font.PLAIN, 10), new Color(40, 80, 40));
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(hint, "gbc");

        fldPin.addActionListener(e -> doLogin());
        return wrapScroll(p);
    }

    // ── Main Menu ─────────────────────────────────────────────────
    private JPanel buildMenuPage() {
        JPanel p = darkPage();

        // Balance card
        JPanel card = roundedPanel(CARD2, 14);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        card.setMaximumSize(new Dimension(360, 90));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JLabel balLbl = styledLabel("AVAILABLE BALANCE", new Font("Segoe UI", Font.PLAIN, 10), MUTED);
        lblBalance    = styledLabel("₹0.00", FONT_AMOUNT, GREEN);
        lblName       = styledLabel("—",     FONT_LABEL,  MUTED);
        card.add(balLbl); card.add(Box.createVerticalStrut(4));
        card.add(lblBalance); card.add(lblName);

        p.add(card, "gbc");
        p.add(Box.createVerticalStrut(14));

        // 2x2 grid
        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.setOpaque(false);
        grid.add(menuCard("📋", "History",  "View transactions",  e -> goPage("history")));
        grid.add(menuCard("➕", "Deposit",   "Add funds",          e -> goPage("deposit")));
        grid.add(menuCard("💵", "Withdraw",  "Cash out",           e -> goPage("withdraw")));
        grid.add(menuCard("🔁", "Transfer",  "Send funds",         e -> goPage("transfer")));
        p.add(grid, "gbc");

        JButton quitBtn = secondaryBtn("Quit / Log Out", e -> goPage("logout"));
        p.add(quitBtn, "gbc");
        return wrapScroll(p);
    }

    // ── Withdraw ──────────────────────────────────────────────────
    private JPanel buildWithdrawPage() {
        JPanel p = darkPage();
        p.add(backBtn(e -> goPage("menu")), "gbc");
        p.add(pageTitle("💵  Cash Withdrawal"), "gbc");
        p.add(infoLabel("Max ₹50,000 per txn  |  Min balance ₹500 must remain"), "gbc");

        // Quick amounts
        p.add(quickAmounts(new int[]{500, 1000, 5000, 10000},
                v -> fldWdAmt.setText(String.valueOf(v))), "gbc");

        p.add(fieldLabel("ENTER AMOUNT (₹)"), "gbc");
        fldWdAmt = darkPasswordField("0.00");
        fldWdAmt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fldWdAmt.setEchoChar((char) 0);
        p.add(fldWdAmt, "gbc");

        lblWdErr = errorLabel();
        p.add(lblWdErr, "gbc");
        p.add(primaryBtn("Withdraw Cash", e -> doWithdraw()), "gbc");
        p.add(secondaryBtn("Cancel", e -> goPage("menu")), "gbc");
        return wrapScroll(p);
    }

    // ── Deposit ───────────────────────────────────────────────────
    private JPanel buildDepositPage() {
        JPanel p = darkPage();
        p.add(backBtn(e -> goPage("menu")), "gbc");
        p.add(pageTitle("➕  Cash Deposit"), "gbc");
        p.add(infoLabel("Max ₹2,00,000 per transaction"), "gbc");

        p.add(quickAmounts(new int[]{1000, 5000, 10000, 50000},
                v -> fldDepAmt.setText(String.valueOf(v))), "gbc");

        p.add(fieldLabel("ENTER AMOUNT (₹)"), "gbc");
        fldDepAmt = darkPasswordField("0.00");
        fldDepAmt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fldDepAmt.setEchoChar((char) 0);
        p.add(fldDepAmt, "gbc");

        lblDepErr = errorLabel();
        p.add(lblDepErr, "gbc");
        p.add(primaryBtn("Deposit Funds", e -> doDeposit()), "gbc");
        p.add(secondaryBtn("Cancel", e -> goPage("menu")), "gbc");
        return wrapScroll(p);
    }

    // ── Transfer ──────────────────────────────────────────────────
    private JPanel buildTransferPage() {
        JPanel p = darkPage();
        p.add(backBtn(e -> goPage("menu")), "gbc");
        p.add(pageTitle("🔁  Fund Transfer"), "gbc");

        p.add(fieldLabel("BENEFICIARY USER ID"), "gbc");
        fldTrBenef = darkTextField("e.g. U002");
        p.add(fldTrBenef, "gbc");

        p.add(fieldLabel("AMOUNT (₹)"), "gbc");
        fldTrAmt = darkPasswordField("0.00");
        fldTrAmt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fldTrAmt.setEchoChar((char) 0);
        p.add(fldTrAmt, "gbc");

        lblTrErr = errorLabel();
        p.add(lblTrErr, "gbc");
        p.add(primaryBtn("Review Transfer →", e -> doTransferConfirm()), "gbc");
        p.add(secondaryBtn("Cancel", e -> goPage("menu")), "gbc");
        return wrapScroll(p);
    }

    // ── Transfer Confirm ─────────────────────────────────────────
    private JPanel buildTransferConfirmPage() {
        JPanel p = darkPage();
        p.add(pageTitle("✅  Confirm Transfer"), "gbc");

        JPanel box = roundedPanel(CARD2, 12);
        box.setLayout(new GridLayout(4, 2, 8, 10));
        box.setBorder(new EmptyBorder(14, 16, 14, 16));

        confName    = styledLabel("—", FONT_SUB, TEXT);
        confUid     = styledLabel("—", FONT_SUB, TEXT);
        confAmt     = styledLabel("₹0.00", FONT_SUB, GREEN);
        confBalAfter= styledLabel("₹0.00", FONT_SUB, TEXT);

        box.add(styledLabel("To Account",    FONT_LABEL, MUTED)); box.add(confName);
        box.add(styledLabel("User ID",       FONT_LABEL, MUTED)); box.add(confUid);
        box.add(styledLabel("Amount",        FONT_LABEL, MUTED)); box.add(confAmt);
        box.add(styledLabel("Balance After", FONT_LABEL, MUTED)); box.add(confBalAfter);
        p.add(box, "gbc");

        p.add(primaryBtn("Confirm & Send", e -> doTransfer()), "gbc");
        p.add(secondaryBtn("Edit Details", e -> goPage("transfer")), "gbc");
        return wrapScroll(p);
    }

    // ── History ───────────────────────────────────────────────────
    private JPanel buildHistoryPage() {
        JPanel p = darkPage();
        p.add(backBtn(e -> goPage("menu")), "gbc");
        p.add(pageTitle("📋  Transaction History"), "gbc");

        txnListPanel = new JPanel();
        txnListPanel.setLayout(new BoxLayout(txnListPanel, BoxLayout.Y_AXIS));
        txnListPanel.setBackground(CARD);

        JScrollPane scroll = new JScrollPane(txnListPanel);
        scroll.setPreferredSize(new Dimension(360, 320));
        scroll.setBackground(CARD);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        p.add(scroll, "gbc");
        return wrapScroll(p);
    }

    // ── Receipt ───────────────────────────────────────────────────
    private JPanel buildReceiptPage() {
        JPanel p = darkPage();
        p.add(pageTitle("🧾  Transaction Receipt"), "gbc");

        receiptPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        receiptPanel.setBackground(new Color(10, 18, 10));
        receiptPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(30, 60, 30), 1, true),
            new EmptyBorder(14, 16, 14, 16)));

        receiptMsg = styledLabel("✔  Successful!", new Font("Segoe UI", Font.BOLD, 14), GREEN);
        receiptMsg.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(receiptPanel, "gbc");
        p.add(Box.createVerticalStrut(8));
        p.add(receiptMsg, "gbc");
        p.add(primaryBtn("Back to Menu", e -> goPage("menu")), "gbc");
        return wrapScroll(p);
    }

    // ── Logout ────────────────────────────────────────────────────
    private JPanel buildLogoutPage() {
        JPanel p = darkPage();
        p.add(pageTitle("👋  End Session?"), "gbc");
        p.add(infoLabel("Your session will be closed securely."), "gbc");

        JPanel box = roundedPanel(CARD2, 12);
        box.setLayout(new GridLayout(2, 2, 8, 8));
        box.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel loName = styledLabel("—", FONT_SUB, TEXT);
        JLabel loBal  = styledLabel("—", FONT_SUB, GREEN);
        box.add(styledLabel("Account",         FONT_LABEL, MUTED)); box.add(loName);
        box.add(styledLabel("Closing Balance", FONT_LABEL, MUTED)); box.add(loBal);
        p.add(box, "gbc");

        p.add(primaryBtn("Log Out Now", e -> {
            if(currentUser!=null){
                loName.setText(currentUser.getFullName());
                loBal.setText(fmt(currentUser.getBalance()));
            }
            doLogout();
        }), "gbc");
        p.add(secondaryBtn("Stay Logged In", e -> goPage("menu")), "gbc");

        // refresh values when page shown
        return wrapScroll(p);
    }

    // ══════════════════════════════════════════════════════════════
    // ATM OPERATIONS
    // ══════════════════════════════════════════════════════════════

    private void doLogin() {
        String uid = fldUserId.getText().trim().toUpperCase();
        String pin = new String(fldPin.getPassword()).trim();
        lblLoginErr.setVisible(false);

        if (!db.containsKey(uid) || !db.get(uid).validatePin(pin)) {
            loginAttempts++;
            if (loginAttempts >= 3)
                showErr(lblLoginErr, "Card blocked after 3 failed attempts!");
            else
                showErr(lblLoginErr, "Invalid credentials. " + (3 - loginAttempts) + " attempt(s) left.");
            return;
        }
        loginAttempts = 0;
        currentUser   = db.get(uid);
        fldPin.setText("");
        lblFooterUser.setText(currentUser.getFullName() + " (" + uid + ")");
        refreshBalance();
        goPage("menu");
    }

    private void doWithdraw() {
        lblWdErr.setVisible(false);
        double amt = parseAmt(new String(fldWdAmt.getPassword()));
        if (amt <= 0)         { showErr(lblWdErr, "Please enter a valid amount."); return; }
        if (amt > 50000)      { showErr(lblWdErr, "Max withdrawal is ₹50,000 per txn."); return; }
        if (currentUser.getBalance() - amt < 500)
                              { showErr(lblWdErr, "Insufficient funds. Min balance ₹500 required."); return; }

        currentUser.withdraw(amt);
        recordTxn(Transaction.Type.WITHDRAWAL, amt, "");
        fldWdAmt.setText("");
        refreshBalance();
        showReceipt("WITHDRAWAL", amt, null, null);
    }

    private void doDeposit() {
        lblDepErr.setVisible(false);
        double amt = parseAmt(new String(fldDepAmt.getPassword()));
        if (amt <= 0)    { showErr(lblDepErr, "Please enter a valid amount."); return; }
        if (amt > 200000){ showErr(lblDepErr, "Max deposit is ₹2,00,000 per txn."); return; }

        currentUser.deposit(amt);
        recordTxn(Transaction.Type.DEPOSIT, amt, "");
        fldDepAmt.setText("");
        refreshBalance();
        showReceipt("DEPOSIT", amt, null, null);
    }

    private void doTransferConfirm() {
        lblTrErr.setVisible(false);
        String tid = fldTrBenef.getText().trim().toUpperCase();
        double amt = parseAmt(fldTrAmt.getText());

        if (!db.containsKey(tid))         { showErr(lblTrErr, "Beneficiary account not found."); return; }
        if (db.get(tid) == currentUser)   { showErr(lblTrErr, "Cannot transfer to your own account."); return; }
        if (amt <= 0)                     { showErr(lblTrErr, "Please enter a valid amount."); return; }
        if (amt > 100000)                 { showErr(lblTrErr, "Max transfer is ₹1,00,000 per txn."); return; }
        if (currentUser.getBalance() - amt < 500)
                                          { showErr(lblTrErr, "Insufficient funds. Min balance ₹500 required."); return; }

        User target = db.get(tid);
        confName    .setText(target.getFullName());
        confUid     .setText(tid);
        confAmt     .setText(fmt(amt));
        confBalAfter.setText(fmt(currentUser.getBalance() - amt));
        goPage("transfer_confirm");
    }

    private void doTransfer() {
        String tid    = fldTrBenef.getText().trim().toUpperCase();
        double amt    = parseAmt(new String(fldTrAmt.getPassword()));
        User   target = db.get(tid);

        currentUser.withdraw(amt);
        target.deposit(amt);
        recordTxn(Transaction.Type.TRANSFER_OUT, amt, "To: " + target.getFullName());
        target.addTransaction(new Transaction(Transaction.Type.TRANSFER_IN, amt,
                target.getBalance(), "From: " + currentUser.getFullName()).toReceiptLine());

        fldTrBenef.setText(""); fldTrAmt.setText("");
        refreshBalance();
        showReceipt("TRANSFER", amt, target.getFullName(), tid);
    }

    private void doLogout() {
        currentUser = null;
        fldUserId.setText("");
        fldPin.setText("");
        loginAttempts = 0;
        lblFooterUser.setText("Not logged in");
        goPage("login");
    }

    // ══════════════════════════════════════════════════════════════
    // UI HELPERS
    // ══════════════════════════════════════════════════════════════

    private void goPage(String name) {
        if (name.equals("history")) renderHistory();
        if (name.equals("logout") && currentUser != null) {
            // values refreshed in logout page builder action
        }
        cards.show(mainPanel, name);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void refreshBalance() {
        if (currentUser == null) return;
        lblBalance.setText(fmt(currentUser.getBalance()));
        lblName   .setText(currentUser.getFullName());
    }

    private void recordTxn(Transaction.Type type, double amt, String note) {
        Transaction t = new Transaction(type, amt, currentUser.getBalance(), note);
        currentUser.addTransaction(t.toReceiptLine());
    }

    private void showReceipt(String op, double amt, String toName, String toUid) {
        receiptPanel.removeAll();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy  HH:mm:ss"));
        addReceiptRow("Operation",       op);
        addReceiptRow("Date / Time",     ts);
        if (toName != null) addReceiptRow("To Account", toName + " (" + toUid + ")");
        addReceiptRow("Amount",          fmt(amt));
        addReceiptRow("Closing Balance", fmt(currentUser.getBalance()));
        receiptMsg.setText("✔  " + op + " Successful!");
        receiptPanel.revalidate(); receiptPanel.repaint();
        goPage("receipt");
    }

    private void addReceiptRow(String key, String val) {
        JLabel k = styledLabel(key, FONT_LABEL, MUTED);
        JLabel v = styledLabel(val, new Font("Segoe UI", Font.BOLD, 12), TEXT);
        receiptPanel.add(k); receiptPanel.add(v);
    }

    private void renderHistory() {
        txnListPanel.removeAll();
        java.util.List<String> hist = currentUser.getTransactionHistory();
        if (hist.isEmpty()) {
            txnListPanel.add(styledLabel("  No transactions yet.", FONT_MONO, MUTED));
        } else {
            for (int i = hist.size() - 1; i >= 0; i--) {
                String line = hist.get(i);
                boolean isCredit = line.contains("DEPOSIT") || line.contains("TRANSFER_IN") || line.contains("Initial");
                JLabel lbl = new JLabel("<html><body style='width:310px'>" + line + "</body></html>");
                lbl.setFont(FONT_MONO); lbl.setForeground(isCredit ? GREEN : RED);
                lbl.setBorder(new EmptyBorder(7, 10, 7, 10));
                JSeparator sep = new JSeparator();
                sep.setForeground(BORDER); sep.setBackground(BORDER);
                txnListPanel.add(lbl); txnListPanel.add(sep);
            }
        }
        txnListPanel.revalidate(); txnListPanel.repaint();
    }

    private void showErr(JLabel lbl, String msg) { lbl.setText(msg); lbl.setVisible(true); }

    private double parseAmt(String s) {
        try { return Double.parseDouble(s.trim().replaceAll(",","")); }
        catch (NumberFormatException e) { return -1; }
    }

    private String fmt(double n) {
        return "₹" + String.format("%,.2f", n);
    }

    private void startClock() {
        javax.swing.Timer t = new javax.swing.Timer(1000, e -> {
            lblClock.setText(java.time.LocalTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        t.start();
    }

    // ── Component factories ───────────────────────────────────────

    private JPanel darkPage() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));
        return p;
    }

    private JPanel wrapScroll(JPanel inner) {
        JScrollPane sp = new JScrollPane(inner,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(CARD);
        sp.getVerticalScrollBar().setUnitIncrement(10);
        JPanel wrap = new JPanel(new BorderLayout()); wrap.setBackground(CARD);
        wrap.add(sp);
        return wrap;
    }

    private JLabel pageTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE); l.setForeground(GREEN);
        l.setBorder(new EmptyBorder(0, 0, 12, 0));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(MUTED);
        l.setBorder(new EmptyBorder(6, 0, 3, 0));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JLabel infoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(MUTED);
        l.setBorder(new EmptyBorder(0, 0, 10, 0));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JLabel errorLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(RED);
        l.setBorder(new EmptyBorder(2, 0, 4, 0));
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setVisible(false);
        return l;
    }

    private JLabel styledLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font); l.setForeground(color);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField darkTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(10, 16, 10));
        tf.setForeground(TEXT); tf.setCaretColor(GREEN);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setAlignmentX(LEFT_ALIGNMENT);
        // placeholder effect
        tf.setText(placeholder); tf.setForeground(MUTED);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) { tf.setText(""); tf.setForeground(TEXT); }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) { tf.setText(placeholder); tf.setForeground(MUTED); }
            }
        });
        return tf;
    }

    private JPasswordField darkPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setBackground(new Color(10, 16, 10));
        pf.setForeground(TEXT); pf.setCaretColor(GREEN);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pf.setAlignmentX(LEFT_ALIGNMENT);
        pf.setEchoChar('●');
        return pf;
    }

    private JButton primaryBtn(String text, ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN); b.setBackground(GREEN); b.setForeground(BG);
        b.setBorder(new EmptyBorder(12, 20, 12, 20));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.addActionListener(al);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(GREEN_D); }
            public void mouseExited(MouseEvent e)  { b.setBackground(GREEN); }
        });
        return b;
    }

    private JButton secondaryBtn(String text, ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN); b.setBackground(CARD2); b.setForeground(MUTED);
        b.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(10, 20, 10, 20)));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.addActionListener(al);
        return b;
    }

    private JButton backBtn(ActionListener al) {
        JButton b = new JButton("← Back");
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setBackground(CARD); b.setForeground(MUTED);
        b.setBorder(new EmptyBorder(0, 0, 10, 0));
        b.setContentAreaFilled(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.addActionListener(al);
        return b;
    }

    private JPanel menuCard(String icon, String title, String sub, ActionListener al) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(CARD2);
        p.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(14, 12, 14, 12)));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel ico  = new JLabel(icon);  ico .setFont(new Font("Segoe UI",Font.PLAIN,22));
        JLabel ttl  = new JLabel(title); ttl .setFont(FONT_BTN);          ttl.setForeground(TEXT);
        JLabel sbl  = new JLabel(sub);   sbl .setFont(FONT_LABEL);        sbl.setForeground(MUTED);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2)); text.setOpaque(false);
        text.add(ttl); text.add(sbl);

        p.add(ico,  BorderLayout.NORTH);
        p.add(text, BorderLayout.CENTER);
        p.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { al.actionPerformed(null); }
            public void mouseEntered(MouseEvent e) { p.setBackground(new Color(25, 45, 25)); p.setBorder(new CompoundBorder(new LineBorder(GREEN,1,true),new EmptyBorder(14,12,14,12))); }
            public void mouseExited(MouseEvent e)  { p.setBackground(CARD2);                 p.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true),new EmptyBorder(14,12,14,12))); }
        });
        return p;
    }

    private JPanel quickAmounts(int[] vals, java.util.function.Consumer<Integer> onPick) {
        JPanel p = new JPanel(new GridLayout(1, vals.length, 8, 0));
        p.setOpaque(false); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.setAlignmentX(LEFT_ALIGNMENT); p.setBorder(new EmptyBorder(0, 0, 8, 0));
        for (int v : vals) {
            JButton b = new JButton("₹" + (v >= 1000 ? (v/1000)+"K" : v));
            b.setFont(new Font("Segoe UI", Font.BOLD, 11));
            b.setBackground(CARD2); b.setForeground(MUTED);
            b.setBorder(new LineBorder(BORDER, 1, true));
            b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final int fv = v;
            b.addActionListener(e -> { onPick.accept(fv); b.setForeground(GREEN); });
            b.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){b.setBorder(new LineBorder(GREEN,1,true));}
                public void mouseExited(MouseEvent e) {b.setBorder(new LineBorder(BORDER,1,true));}
            });
            p.add(b);
        }
        return p;
    }

    private JPanel roundedPanel(Color bg, int arc) {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    // ══════════════════════════════════════════════════════════════
    // MAIN — Entry point for GUI version
    // ══════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Seed the same database as ATM.java
        Map<String, User> userDatabase = new HashMap<>();

        User alice   = new User("U001", "1234", "Alice Johnson",  85_000.00);
        alice.addTransaction("Initial deposit       +₹85,000.00");
        User bob     = new User("U002", "5678", "Bob Smith",      42_500.50);
        bob.addTransaction("Initial deposit       +₹42,500.50");
        User charlie = new User("U003", "9999", "Charlie Brown", 1_20_000.00);
        charlie.addTransaction("Initial deposit    +₹1,20,000.00");

        userDatabase.put("U001", alice);
        userDatabase.put("U002", bob);
        userDatabase.put("U003", charlie);

        SwingUtilities.invokeLater(() -> new ATM_GUI(userDatabase));
    }
}