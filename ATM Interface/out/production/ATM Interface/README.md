🏧 JavaBank ATM Interface
A fully-featured, console-based ATM simulation built in pure Java (JDK 11+).
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
The JavaBank ATM Interface simulates a real-world Automated Teller Machine (ATM) system.
On startup the user is prompted for a User ID and a PIN. After successful authentication the following operations are unlocked:

#	Operation	Description
1	Transaction History	View timestamped log of all past transactions
2	Withdraw	Withdraw cash with limit and minimum balance checks
3	Deposit	Deposit cash/cheque up to the per-transaction cap
4	Transfer	Transfer funds to another registered account
5	Quit / Log Out	End the session and optionally start a new one
Features
✅ Secure Authentication — User ID + PIN login with a 3-attempt lockout
✅ Minimum Balance Enforcement — ₹500 must remain after any debit
✅ Per-Transaction Limits — Withdrawal ₹50,000 | Deposit ₹2,00,000 | Transfer ₹1,00,000
✅ Timestamped Transaction History — Every event is logged with date & time
✅ Bilateral Transfer Recording — Both sender and receiver history are updated
✅ Formatted Receipts — Clean, readable receipts printed after every operation
✅ Multi-Session Support — After logout, a new user can authenticate immediately
✅ Input Validation — Handles non-numeric input, empty fields, and edge cases gracefully
✅ Separation of Concerns — Each class has one clear responsibility (SRP)
Project Structure
ATM-Interface/
│
├── src/
│   ├── ATM.java              ← Entry point; seeds user database & boots controller
│   ├── User.java             ← Data model: credentials, balance, transaction list
│   ├── Transaction.java      ← Immutable value object for a single transaction event
│   ├── AuthService.java      ← Authentication logic with lockout policy
│   ├── UIHelper.java         ← Console formatting utilities (headers, receipts, menus)
│   └── ATMController.java    ← Core session controller; all 5 ATM operations
│
└── README.md
Class Descriptions
ATM.java — Entry Point
Creates the in-memory HashMap<String, User> acting as the user database
Seeds three demo accounts (Alice, Bob, Charlie)
Instantiates ATMController and calls start()
User.java — User Model
Stores userId, hashed pin, fullName, balance
Exposes thread-safe withdraw() and deposit() methods that return boolean
Maintains a capped ArrayList<String> of transaction records (max 50)
Validates constructor arguments to prevent invalid states
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
Centralises all console formatting so other classes stay clean
ATMController.java — Core ATM Controller
Owns the main session loop
Calls AuthService for login, then routes menu choices to private methods
Each operation (withdraw, deposit, transfer, showHistory) is self-contained
Enforces all business rules (limits, minimum balance, confirmation prompts)
How to Run
Prerequisites
Java JDK 11 or higher installed
A terminal / command prompt
Steps
bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/ATM-Interface.git
cd ATM-Interface/src

# 2. Compile all Java files
javac *.java

# 3. Run the application
java ATM
Windows users: Open Command Prompt, navigate to the src folder, and run the same commands.

Demo Credentials
User ID	PIN	Account Holder	Initial Balance
U001	1234	Alice Johnson	₹85,000.00
U002	5678	Bob Smith	₹42,500.50
U003	9999	Charlie Brown	₹1,20,000.00
You can transfer funds between these accounts during a session to test the bilateral recording feature.

Sample Output
═══════════════════════════════════════════════════════════════
Welcome to JavaBank ATM
═══════════════════════════════════════════════════════════════
Enter User ID  : U001
Enter PIN      : 1234

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
Select option : 2
Design Decisions
Decision	Reason
5 separate classes	Follows Single Responsibility Principle (SRP) — easier to test and extend
HashMap as database	Simulates fast O(1) lookups; easily replaceable with JDBC/JPA later
Transaction as immutable object	Prevents accidental modification of financial records
3-attempt lockout in AuthService	Mirrors real ATM security behaviour
Minimum balance rule	Reflects standard Indian banking policy
Collections.unmodifiableList()	Prevents external code from altering transaction history
Future Enhancements
Persistent storage using file I/O or SQLite / MySQL
PIN change functionality
Mini-statement PDF export using iText library
GUI using Java Swing or JavaFX
AES-256 PIN encryption instead of plain-text comparison
Session timeout after inactivity
OTP-based two-factor authentication
Author
Your Name
B.Tech / MCA — [Your College Name]
Internship at — [Company Name]

Show Image
Show Image

This project was built for educational and internship evaluation purposes.

