🏧 JavaBank ATM Interface
A fully-featured ATM simulation built in pure Java, offering both a console-based terminal version and a Java Swing GUI desktop version.
Developed as part of an internship assignment demonstrating object-oriented design, clean architecture, and real-world banking logic.

📌 Table of Contents
Project Overview
Features
Project Structure
Class Descriptions
How to Run
Demo Credentials
Sample Output
Design Decisions
Future Enhancements
Author
Project Overview
The JavaBank ATM Interface simulates a real-world Automated Teller Machine system built entirely in Java. The project comes in two versions — a console application and a Swing-based desktop GUI — both sharing the same core business logic classes.

On startup the user is prompted for a User ID and PIN. After successful authentication, the following operations are unlocked:

#	Operation	Description
1	Transaction History	View timestamped log of all past transactions
2	Withdraw	Withdraw cash with limit and minimum balance checks
3	Deposit	Deposit cash/cheque up to the per-transaction cap
4	Transfer	Transfer funds to another registered account
5	Quit / Log Out	End the session securely
Features
✅ Secure Authentication — User ID + PIN login with a 3-attempt lockout policy
✅ Minimum Balance Enforcement — ₹500 must remain after any debit transaction
✅ Per-Transaction Limits — Withdrawal ₹50,000 | Deposit ₹2,00,000 | Transfer ₹1,00,000
✅ Timestamped Transaction History — Every event logged with full date and time
✅ Bilateral Transfer Recording — Both sender and receiver histories are updated
✅ Formatted Receipts — Clean receipt printed after every operation
✅ Multi-Session Support — After logout, a new user can authenticate immediately
✅ Input Validation — Handles invalid input, empty fields, and all edge cases gracefully
✅ Java Swing GUI — Full desktop window with dark green theme, live clock, quick-amount buttons, and colour-coded history
✅ Separation of Concerns — Each class has one clear responsibility (Single Responsibility Principle)
Project Structure
ATM-Interface/
│
├── src/                          ← All Java source files (console + GUI in same folder)
│   ├── ATM.java                  ← Console entry point — run this for terminal version
│   ├── ATM_GUI.java              ← Swing GUI entry point — run this for desktop window
│   ├── User.java                 ← Data model: credentials, balance, transaction list
│   ├── Transaction.java          ← Immutable value object for a single transaction event
│   ├── AuthService.java          ← Authentication logic with 3-attempt lockout policy
│   ├── UIHelper.java             ← Console formatting utilities (headers, receipts, menus)
│   └── ATMController.java        ← Core session controller; drives all 5 ATM operations
│
└── README.md
Both ATM.java (console) and ATM_GUI.java (Swing window) share the same User.java and Transaction.java classes — same business logic, two different interfaces.

Class Descriptions
ATM.java — Console Entry Point
Creates the in-memory HashMap<String, User> acting as the user database
Seeds three demo accounts (Alice, Bob, Charlie) with opening balances
Instantiates ATMController and calls start() to begin the terminal session
ATM_GUI.java — Swing GUI Entry Point
Standalone desktop window built with Java Swing (no external libraries needed)
Dark terminal-style theme (green on black) with a live clock in the header
Uses CardLayout to switch between 9 screens: Login, Menu, Withdraw, Deposit, Transfer, Confirm, History, Receipt, Logout
Directly uses User.java and Transaction.java — no duplication of business logic
Includes quick-amount buttons (₹500 / ₹1K / ₹5K / ₹10K), hover effects on menu cards, and colour-coded transaction history (green = credit, red = debit)
User.java — User Model
Stores userId, pin, fullName, balance
Exposes withdraw() and deposit() methods that return boolean
Maintains a capped ArrayList<String> of transaction records (max 50 entries)
Validates all constructor arguments to prevent invalid account states
Transaction.java — Transaction Value Object
Immutable record with type (enum), amount, balanceAfter, note, timestamp
toReceiptLine() generates a formatted single-line receipt string
Transaction types: DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN
AuthService.java — Authentication Service
Separates login logic from the controller (Single Responsibility Principle)
Allows up to 3 PIN attempts before locking out the session
Returns the authenticated User object or null on failure
UIHelper.java — Console UI Utilities
Pure static helper — cannot be instantiated
Provides: printHeader(), printSubHeader(), printSuccess(), printError(), printMainMenu(), pause()
Centralises all console formatting so controller and service classes stay clean
ATMController.java — Core ATM Controller
Owns the main session loop
Calls AuthService for login, then routes menu choices to private operation methods
Each operation (withdraw, deposit, transfer, showHistory) is self-contained
Enforces all business rules: limits, minimum balance, and confirmation prompts
How to Run
Prerequisites
Java JDK 11 or higher installed
IntelliJ IDEA (recommended) or any Java IDE
No external libraries or dependencies required
▶ Option 1 — Console Version (Terminal)
In IntelliJ:

Open the project and navigate to src/ATM.java
Right-click ATM.java → click Run 'ATM.main()'
The ATM runs inside the IntelliJ terminal panel
Via command line:

bash
cd ATM-Interface/src
javac *.java
java ATM
🖥️ Option 2 — Java Swing GUI (Desktop Window)
In IntelliJ:

Navigate to src/ATM_GUI.java
Right-click ATM_GUI.java → click Run 'ATM_GUI.main()'
A desktop ATM window opens instantly — no browser or server needed
Via command line:

bash
cd ATM-Interface/src
javac *.java
java ATM_GUI
Both files are inside the same src folder and compile together with javac *.java. ATM_GUI.java automatically uses User.java and Transaction.java from the same folder.

Demo Credentials
User ID	PIN	Account Holder	Opening Balance
U001	1234	Alice Johnson	₹85,000.00
U002	5678	Bob Smith	₹42,500.50
U003	9999	Charlie Brown	₹1,20,000.00
Tip: Log in as Alice (U001), transfer ₹2,000 to U002, then log out and log in as Bob (U002) to see the transfer appear in his history — bilateral recording in action.

Sample Output
Console Version
═══════════════════════════════════════════════════════════════
  Welcome to JavaBank ATM
═══════════════════════════════════════════════════════════════
  Enter User ID  : U001
  Enter PIN      : ****

  ✔ Welcome, Alice Johnson!

═══════════════════════════════════════════════════════════════
  JavaBank ATM — Main Menu
═══════════════════════════════════════════════════════════════
  Account Holder : Alice Johnson
  Available Bal  : ₹85,000.00
───────────────────────────────────────────────────────────────
   [1]  Transaction History
   [2]  Withdraw
   [3]  Deposit
   [4]  Transfer
   [5]  Quit / Log Out
───────────────────────────────────────────────────────────────
Swing GUI Version
A 420×680 desktop window opens with a dark green ATM theme
Live clock displayed in the top-right corner
Balance card shows account holder name and current balance
All 5 operations accessible via clickable menu cards
Receipts, confirmations, and error messages shown on-screen
Design Decisions
Decision	Reason
7 source files, all in src/	Everything compiles together with one command — simple for a console + GUI project
ATM_GUI.java reuses User and Transaction	No code duplication — same business logic powers both interfaces
HashMap as in-memory database	Simulates fast O(1) lookups; easily replaceable with JDBC/JPA for a real database
Transaction as immutable object	Prevents accidental modification of financial records after they are created
3-attempt lockout in AuthService	Mirrors real ATM card-blocking security behaviour
Minimum balance rule (₹500)	Reflects standard Indian banking policy
Collections.unmodifiableList()	Prevents external code from altering the transaction history list
javax.swing.Timer used explicitly	Avoids ambiguity with java.util.Timer which is also available in the JDK
getPassword() instead of getText()	Follows Java security best practices for handling PIN and sensitive input fields
Future Enhancements
 Persistent storage using file I/O or SQLite / MySQL
 PIN change functionality
 Mini-statement PDF export using the iText library
 Connect GUI to a REST API backend built with Spring Boot
 AES-256 PIN encryption instead of plain-text comparison
 Session timeout after a period of inactivity
 OTP-based two-factor authentication
 Unit tests using JUnit 5 for all business logic methods
Author
Your Name Vivek kr. Gupta, B.Tech— [The Assam Kaziranga University] Internship at — [OASIS INFOBYTE]


This project was built for educational and internship evaluation purposes.

