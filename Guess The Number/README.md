# 🎯 Guess The Number — Java Swing Edition

<div align="center">

![Java](https://img.shields.io/badge/Java-8%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-5C2D91?style=for-the-badge&logo=java&logoColor=white)
![No Dependencies](https://img.shields.io/badge/Dependencies-None-brightgreen?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)

**A modern, dark-themed number guessing game built with pure Java Swing.**
No external libraries. No build tools. Just one `.java` file — compile and play.

</div>

---

## 📸 Preview

```
┌──────────────────────────────────────┐
│           [ MINI GAME ]              │
│                                      │
│       Guess The Number               │
│    Pick a number between 1 and 100   │
│                                      │
│      ● ● ● ○ ○ ○ ○                  │
│                                      │
│   ┌──────────────────────────┐       │
│   │           42             │       │
│   └──────────────────────────┘       │
│                                      │
│   ┌──────────────────────────┐       │
│   │          Guess           │       │
│   └──────────────────────────┘       │
│                                      │
│   📉  Too High!  4 attempts left     │
│                                      │
│   ┌──────────────────────────┐       │
│   │       Restart Game       │       │
│   └──────────────────────────┘       │
│                                      │
│          Total wins: 3               │
└──────────────────────────────────────┘
```

> Dark purple gradient background · Frosted glass card · Color-coded feedback · Sound effects

---

## ✨ Features

### 🎮 Gameplay
- Random secret number generated between **1 and 100** each round
- **7 attempts** per game to find the correct number
- Instant feedback — **Too High**, **Too Low**, **Correct**, or **Game Over**
- Attempts remaining shown after every guess
- Win counter persists across rounds for the whole session

### 🎨 UI & Design
- **Custom gradient background** — deep navy to purple, painted via `GradientPaint`
- **Frosted glass card** — semi-transparent rounded panel sitting over the gradient
- **Gradient "MINI GAME" badge** at the top, painted with `Graphics2D`
- **Attempt dot indicators** — purple for current, red for used, gray for remaining
- **Color-coded message panel** that changes based on result:
  - 🟣 Blue/purple — info / start message
  - 🟡 Amber — too low
  - 🔴 Red — too high or game over
  - 🟢 Green — correct / win
- **Custom-painted input field** with solid dark background so text is always crisp white
- **Purple glow border** on input when focused
- **Gradient Guess button** with press-down click animation
- **Outline Restart button** with hover highlight effect
- All components use **anti-aliased `Graphics2D`** rendering — no jagged edges

### 🔊 Sound Effects
- **Win sound** — rising 3-note chime (C → E → G) using `javax.sound.sampled`
- **Wrong guess sound** — short low buzz on every incorrect guess
- Sounds are synthesized in real time — **no audio files needed**

---

## 🗂️ Project Structure

```
GuessNumberGUI/
│
├── GuessNumberGUI.java        ← Everything in one file
│   │
│   ├── GuessNumberGUI()       ← Constructor: builds entire UI
│   ├── doGuess()              ← Game logic: validates input, checks number
│   ├── restart()              ← Resets game state and UI
│   ├── updateDots()           ← Refreshes attempt dot indicators
│   ├── setMessage()           ← Updates feedback panel color + text
│   ├── playTone()             ← Synthesizes and plays a beep tone
│   ├── playWinSound()         ← 3-note win chime
│   ├── playWrongSound()       ← Single wrong-guess buzz
│   ├── createBadge()          ← Gradient "MINI GAME" label
│   ├── createGradientButton() ← Custom painted gradient Guess button
│   ├── createOutlineButton()  ← Ghost/outline style Restart button
│   ├── styleInputFocus()      ← Focus border highlight on input
│   ├── RoundedPanel           ← Inner class: custom rounded panel
│   └── RoundedLineBorder      ← Inner class: custom rounded border
│
└── README.md
```

> **Single file. Zero dependencies. Pure Java SE.**

---

## 🚀 Getting Started

### Prerequisites

| Tool | Minimum Version | Download |
|------|----------------|----------|
| JDK  | Java 8 or higher | [adoptium.net](https://adoptium.net) |

That's it. Java Swing and `javax.sound.sampled` are both part of the standard JDK — nothing else to install.

---

### ▶️ Running the Game

**Step 1 — Clone the repository**
```bash
git clone https://github.com/your-username/GuessNumberGUI.git
cd GuessNumberGUI
```

**Step 2 — Compile**
```bash
javac GuessNumberGUI.java
```

**Step 3 — Run**
```bash
java GuessNumberGUI
```

The game window opens instantly. That's all it takes.

---

### 🪟 Windows
```cmd
javac GuessNumberGUI.java
java GuessNumberGUI
```

### 🍎 macOS / 🐧 Linux
```bash
javac GuessNumberGUI.java
java GuessNumberGUI
```

Works identically on all platforms — no path setup or config needed.

---

### 💻 Running from an IDE

**IntelliJ IDEA**
1. `File → Open` → select the project folder
2. Right-click `GuessNumberGUI.java` → `Run 'GuessNumberGUI.main()'`

**Eclipse**
1. `File → New → Java Project` → paste the file into `src/`
2. Right-click the file → `Run As → Java Application`

**VS Code**
1. Install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
2. Open the folder → click ▶️ Run above the `main` method

---

## 🎮 How to Play

```
1.  The game picks a secret number between 1 and 100
2.  Type your guess in the input box
3.  Press the Guess button  OR  hit Enter
4.  Read the feedback:
        📈  Too Low   →  your guess is below the secret number
        📉  Too High  →  your guess is above the secret number
        🎉  Correct   →  you win!
        😢  Game Over →  you ran out of attempts
5.  You have 7 attempts per round
6.  Press Restart Game to start a new round at any time
7.  Your total wins are shown at the bottom of the card
```

---

## 🏗️ How It Works — Technical Overview

### Game Logic Flow

```
User types number → doGuess() is called
        │
        ├─── Invalid input (not 1–100)?
        │         └── Show info message, return early
        │
        ├─── Correct guess?
        │         └── Show win message, disable button,
        │             increment wins, play win sound
        │
        ├─── All 7 attempts used?
        │         └── Show game over + reveal number,
        │             disable button, play wrong sound
        │
        └─── Wrong guess?
                  └── Show Too High / Too Low + attempts left,
                      update dots, play wrong sound
```

### Custom Painting System

Every visual component is drawn manually with `Graphics2D` instead of relying on the system Look & Feel — this is what gives the app its polished dark appearance on every OS:

| Component | Painting technique |
|-----------|-------------------|
| Background | `GradientPaint` diagonal fill |
| Card panel | `fillRoundRect` with alpha color |
| Badge | `GradientPaint` horizontal fill |
| Attempt dots | `fillOval` with dynamic color swap |
| Input field | `fillRoundRect` solid dark bg via `paintComponent` override |
| Guess button | `GradientPaint` fill + manual centered text |
| Restart button | `drawRoundRect` stroke + hover fill |
| Message panel | `RoundedPanel` with swappable background color |

### Sound Synthesis

Sounds are generated in real time using raw PCM sine wave math — no `.wav` or `.mp3` files required:

```java
// Sine wave with linear decay envelope
buf[i] = (byte)(Math.sin(2π * i * freq / sampleRate) * 127 * vol * envelope);
```

Played via `javax.sound.sampled.SourceDataLine` on a background thread so the UI stays fully responsive.

---

## 🐛 Troubleshooting

**`javac: command not found`**
→ JDK is not installed or not on your PATH. Download from [adoptium.net](https://adoptium.net) and add `JAVA_HOME/bin` to your system PATH.

**Text in the input field is not visible / appears dark**
→ Make sure you're using the latest version of the file. This was fixed by overriding `paintComponent` on the `JTextField` to draw a solid dark rounded background directly, bypassing the Look & Feel color override.

**No sound plays**
→ Check your system volume. On some Linux setups the default audio output line may be unavailable — the game silently catches this exception and continues without sound.

**Window looks unstyled or gray**
→ The `getCrossPlatformLookAndFeelClassName()` call in `main()` ensures consistent rendering. If you accidentally remove it, the OS native L&F may override the custom colors.

**`UnsupportedClassVersionError` on run**
→ The `.class` file was compiled with a newer JDK than the one running it. Make sure both `javac -version` and `java -version` show the same major version (8 or higher).

---

## 🔮 Future Improvements

- [ ] Difficulty modes — Easy (10 attempts), Normal (7), Hard (4)
- [ ] Guess history list showing all previous attempts with arrows
- [ ] High score leaderboard saved to a local `.txt` file
- [ ] Proximity "hot / cold" color meter as guesses get closer
- [ ] Timer mode — race against the clock for bonus points
- [ ] Animated win celebration overlay
- [ ] Dark / light theme toggle

---

## 📦 Dependencies

| Library | Source | Used for |
|---------|--------|----------|
| `javax.swing` | JDK built-in | All UI components (JFrame, JPanel, JButton, etc.) |
| `java.awt` | JDK built-in | Graphics2D, colors, fonts, layout managers |
| `javax.sound.sampled` | JDK built-in | Real-time sound synthesis and playback |
| `java.util.Random` | JDK built-in | Generating the secret number each round |

**Zero external dependencies.** Everything ships with the standard JDK.

---

## 📄 License

```
MIT License — Copyright (c) 2025

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

## 👤 Author

Designed and built as a student project demo.
If you found this useful, drop a ⭐ — it helps others find it!

---

<div align="center">

**Built with Java · Pure Swing · No dependencies · Single file**

`javac GuessNumberGUI.java && java GuessNumberGUI`

</div>
