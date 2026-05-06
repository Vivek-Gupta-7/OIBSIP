import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;
import javax.sound.sampled.*;

public class GuessNumberGUI {

    private int numberToGuess;
    private int attempts = 0;
    private final int MAX_ATTEMPTS = 7;
    private int wins = 0;
    private boolean gameOver = false;

    // Colors
    private final Color BG_TOP        = new Color(15, 12, 41);
    private final Color BG_BOTTOM     = new Color(48, 43, 99);
    private final Color CARD_BG       = new Color(255, 255, 255, 18);
    private final Color CARD_BORDER   = new Color(255, 255, 255, 30);
    private final Color ACCENT_PURPLE = new Color(124, 58, 237);
    private final Color ACCENT_BLUE   = new Color(79, 70, 229);
    private final Color TEXT_PRIMARY  = new Color(224, 231, 255);
    private final Color TEXT_MUTED    = new Color(255, 255, 255, 100);
    private final Color MSG_WIN_BG    = new Color(16, 185, 129, 40);
    private final Color MSG_WIN_FG    = new Color(110, 231, 183);
    private final Color MSG_LOW_BG    = new Color(245, 158, 11, 40);
    private final Color MSG_LOW_FG    = new Color(253, 230, 138);
    private final Color MSG_HIGH_BG   = new Color(239, 68, 68, 40);
    private final Color MSG_HIGH_FG   = new Color(252, 165, 165);
    private final Color MSG_INFO_BG   = new Color(99, 102, 241, 40);
    private final Color MSG_INFO_FG   = new Color(165, 180, 252);
    private final Color DOT_INACTIVE  = new Color(255, 255, 255, 38);
    private final Color DOT_USED      = new Color(239, 68, 68);
    private final Color DOT_ACTIVE    = new Color(124, 58, 237);

    private JLabel msgLabel;
    private JPanel msgPanel;
    private JTextField inputField;
    private JButton guessBtn;
    private JLabel scoreLabel;
    private JPanel[] dots;
    private JFrame frame;

    public GuessNumberGUI() {
        frame = new JFrame("Guess The Number");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(480, 580);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Gradient background panel
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_TOP, getWidth(), getHeight(), BG_BOTTOM);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setLayout(new GridBagLayout());

        // Card panel
        RoundedPanel card = new RoundedPanel(20, CARD_BG, CARD_BORDER);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(360, 460));
        card.setBorder(new EmptyBorder(30, 32, 28, 32));

        // Badge
        JLabel badge = createBadge("MINI GAME");

        // Title
        JLabel title = new JLabel("Guess The Number");
        title.setFont(loadFont(22f, Font.BOLD));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitle = new JLabel("Pick a number between 1 and 100");
        subtitle.setFont(loadFont(12f, Font.PLAIN));
        subtitle.setForeground(new Color(255, 255, 255, 100));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Dots
        JPanel dotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 0));
        dotsPanel.setOpaque(false);
        dotsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dots = new JPanel[MAX_ATTEMPTS];
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            dots[i] = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }
            };
            dots[i].setPreferredSize(new Dimension(13, 13));
            dots[i].setBackground(DOT_INACTIVE);
            dots[i].setOpaque(false);
            dotsPanel.add(dots[i]);
        }

       // Input field
inputField = new JTextField() {
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(30, 25, 60));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
        g2.dispose();
        super.paintComponent(g);
    }
};
inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
inputField.setFont(loadFont(22f, Font.BOLD));
inputField.setHorizontalAlignment(JTextField.CENTER);
inputField.setOpaque(false);
inputField.setBackground(new Color(0, 0, 0, 0));
inputField.setForeground(Color.WHITE);
inputField.setSelectedTextColor(Color.WHITE);
inputField.setSelectionColor(new Color(124, 58, 237));
inputField.setDisabledTextColor(Color.WHITE);
inputField.setCaretColor(new Color(180, 150, 255));
inputField.setBorder(new CompoundBorder(
    new RoundedLineBorder(new Color(255, 255, 255, 50), 12, 2),
    new EmptyBorder(10, 12, 10, 12)
));
styleInputFocus(inputField);

        // Guess button
        guessBtn = createGradientButton("Guess", ACCENT_PURPLE, ACCENT_BLUE);

        // Message area
        msgPanel = new RoundedPanel(12, MSG_INFO_BG, new Color(0, 0, 0, 0));
        msgPanel.setLayout(new BorderLayout());
        msgPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        msgPanel.setBorder(new EmptyBorder(0, 12, 0, 12));
        msgLabel = new JLabel("You have " + MAX_ATTEMPTS + " attempts — good luck!");
        msgLabel.setFont(loadFont(13f, Font.BOLD));
        msgLabel.setForeground(MSG_INFO_FG);
        msgLabel.setHorizontalAlignment(JLabel.CENTER);
        msgPanel.add(msgLabel, BorderLayout.CENTER);

        // Restart button
        JButton restartBtn = createOutlineButton("Restart Game");

        // Score
        scoreLabel = new JLabel("Total wins: 0");
        scoreLabel.setFont(loadFont(12f, Font.PLAIN));
        scoreLabel.setForeground(new Color(255, 255, 255, 100));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assembly
        card.add(badge);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));
        card.add(dotsPanel);
        card.add(Box.createVerticalStrut(18));
        card.add(inputField);
        card.add(Box.createVerticalStrut(12));
        card.add(guessBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(msgPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(restartBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(scoreLabel);

        background.add(card);
        frame.setContentPane(background);

        // Actions
        guessBtn.addActionListener(e -> doGuess());
        restartBtn.addActionListener(e -> restart());
        inputField.addActionListener(e -> doGuess());

        restart();
        frame.setVisible(true);
    }

    private void doGuess() {
        if (gameOver) return;
        int guess;
        try {
            guess = Integer.parseInt(inputField.getText().trim());
            if (guess < 1 || guess > 100) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            setMessage("Enter a valid number from 1 to 100", MSG_INFO_BG, MSG_INFO_FG);
            return;
        }

        attempts++;
        updateDots();
        inputField.setText("");

        if (guess == numberToGuess) {
            wins++;
            scoreLabel.setText("Total wins: " + wins);
            setMessage("Correct! You win! The number was " + numberToGuess, MSG_WIN_BG, MSG_WIN_FG);
            guessBtn.setEnabled(false);
            gameOver = true;
            playWinSound();
        } else if (attempts >= MAX_ATTEMPTS) {
            setMessage("Game over! The number was " + numberToGuess, MSG_HIGH_BG, MSG_HIGH_FG);
            guessBtn.setEnabled(false);
            gameOver = true;
            playWrongSound();
        } else {
            int left = MAX_ATTEMPTS - attempts;
            String s = left == 1 ? " attempt left" : " attempts left";
            if (guess < numberToGuess) {
                setMessage("Too low!  " + left + s, MSG_LOW_BG, MSG_LOW_FG);
            } else {
                setMessage("Too high!  " + left + s, MSG_HIGH_BG, MSG_HIGH_FG);
            }
            playWrongSound();
        }
    }

    private void restart() {
        numberToGuess = new Random().nextInt(100) + 1;
        attempts = 0;
        gameOver = false;
        inputField.setText("");
        guessBtn.setEnabled(true);
        updateDots();
        setMessage("You have " + MAX_ATTEMPTS + " attempts — good luck!", MSG_INFO_BG, MSG_INFO_FG);
    }

    private void updateDots() {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            if (i < attempts) dots[i].setBackground(DOT_USED);
            else if (i == attempts) dots[i].setBackground(DOT_ACTIVE);
            else dots[i].setBackground(DOT_INACTIVE);
            dots[i].repaint();
        }
    }

    private void setMessage(String text, Color bg, Color fg) {
        msgLabel.setText(text);
        msgLabel.setForeground(fg);
        ((RoundedPanel) msgPanel).setBgColor(bg);
        msgPanel.repaint();
    }

    // ── Sound ──────────────────────────────────────────────────────────────────
    private void playTone(float freq, int ms, float vol) {
        new Thread(() -> {
            try {
                int sampleRate = 44100;
                byte[] buf = new byte[sampleRate * ms / 1000];
                for (int i = 0; i < buf.length; i++) {
                    double angle = 2.0 * Math.PI * i * freq / sampleRate;
                    double env = 1.0 - (double) i / buf.length;
                    buf[i] = (byte) (Math.sin(angle) * 127 * vol * env);
                }
                AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(af); line.start();
                line.write(buf, 0, buf.length);
                line.drain(); line.close();
            } catch (Exception ignored) {}
        }).start();
    }

    private void playWinSound() {
        playTone(523, 100, 0.4f);
        try { Thread.sleep(110); } catch (Exception ignored) {}
        playTone(659, 100, 0.4f);
        try { Thread.sleep(110); } catch (Exception ignored) {}
        playTone(784, 200, 0.4f);
    }

    private void playWrongSound() { playTone(220, 150, 0.3f); }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private Font loadFont(float size, int style) {
        return new Font("Segoe UI", style, (int) size);
    }

    private JLabel createBadge(String text) {
        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_PURPLE, getWidth(), 0, ACCENT_BLUE);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                super.paintComponent(g);
            }
        };
        badge.setText(text);
        badge.setFont(loadFont(11f, Font.BOLD));
        badge.setForeground(Color.WHITE);
        badge.setHorizontalAlignment(JLabel.CENTER);
        badge.setOpaque(false);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        badge.setPreferredSize(new Dimension(100, 24));
        badge.setMaximumSize(new Dimension(100, 24));
        return badge;
    }

    private JButton createGradientButton(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
            }
        };
        btn.setFont(loadFont(15f, Font.BOLD));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { btn.setLocation(btn.getX(), btn.getY() + 1); }
            public void mouseReleased(MouseEvent e) { btn.setLocation(btn.getX(), btn.getY() - 1); }
        });
        return btn;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.setColor(new Color(255, 255, 255, 30));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(getModel().isRollover() ? Color.WHITE : new Color(255, 255, 255, 150));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
            }
        };
        btn.setFont(loadFont(13f, Font.PLAIN));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private void styleInputFocus(JTextField field) {
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                    new RoundedLineBorder(ACCENT_PURPLE, 12, 2),
                    new EmptyBorder(10, 12, 10, 12)
                ));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                    new RoundedLineBorder(new Color(255, 255, 255, 50), 12, 2),
                    new EmptyBorder(10, 12, 10, 12)
                ));
            }
        });
    }

    // ── Inner classes ──────────────────────────────────────────────────────────
    static class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        private Color borderColor;

        RoundedPanel(int radius, Color bgColor, Color borderColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        void setBgColor(Color c) { this.bgColor = c; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
            if (borderColor.getAlpha() > 0) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius * 2, radius * 2);
            }
            super.paintComponent(g);
        }
    }

    static class RoundedLineBorder extends AbstractBorder {
        private Color color;
        private int radius;
        private int thickness;

        RoundedLineBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(thickness, thickness, thickness, thickness); }

        @Override
        public Insets getBorderInsets(Component c, Insets i) {
            i.set(thickness, thickness, thickness, thickness);
            return i;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
 }
            catch (Exception ignored) {}
            new GuessNumberGUI();
        });
    }
}