import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Minesweeper {
    private class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 50;
    int numRows;
    int numCols;
    int boardWidth;
    int boardHeight;
    
    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JLabel timerLabel = new JLabel();
    JLabel scoreLabel = new JLabel();

    int mineCount;
    MineTile[][] board;
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;
    boolean isZenMode;
    boolean areModifiersEnabled;
    int score = 0;
    Timer timer;
    Timer modifierTimer;
    int timeLeft = 900;

    public Minesweeper(boolean isDarkMode, boolean isZenMode, boolean areModifiersEnabled, int mineCount, int boardSize) {
        this.isZenMode = isZenMode;
        this.areModifiersEnabled = areModifiersEnabled;
        this.mineCount = mineCount;
        this.numRows = boardSize;
        this.numCols = boardSize;
        this.boardWidth = numCols * tileSize;
        this.boardHeight = numRows * tileSize;
        this.board = new MineTile[numRows][numCols];

        setupGame();
        if (isDarkMode) {
            applyDarkMode();
        }
        if (!isZenMode) {
            startTimer();
        }
        if (areModifiersEnabled) {
            startModifiers();
        }
    }

    void setupGame() {
        frame.setSize(boardWidth + 20, boardHeight + 100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 15));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("MineModder: " + mineCount);
        textLabel.setOpaque(true);

        timerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("Time Left: 15:00");

        scoreLabel.setFont(new Font("Arial", Font.BOLD, 15));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setText("Score: 0");

        textPanel.setLayout(new GridLayout(1, 3)); // Removed the modifier label
        textPanel.add(textLabel);
        textPanel.add(timerLabel);
        textPanel.add(scoreLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel, BorderLayout.CENTER);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 20));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText().equals("🚩")) {
                                return;
                            }

                            if (mineList.contains(tile)) {
                                revealMines();
                            } else {
                                checkMine(tile.r, tile.c);
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (!tile.getText().equals("🚩")) {
                                tile.setText("🚩");
                            } else {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }
        generateMines();
        frame.setVisible(true);
    }

    void generateMines() {
        mineList = new ArrayList<>();

        while (mineList.size() < mineCount) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile randomTile = board[r][c];

            if (!mineList.contains(randomTile)) {
                mineList.add(randomTile);
            }
        }
    }

    void applyDarkMode() {
        Color darkColor = Color.DARK_GRAY;
        Color lightColor = Color.LIGHT_GRAY;
        Color whiteColor = Color.WHITE;

        frame.getContentPane().setBackground(darkColor);
        textPanel.setBackground(darkColor);
        textLabel.setBackground(lightColor);
        textLabel.setForeground(whiteColor);
        timerLabel.setBackground(lightColor);
        timerLabel.setForeground(whiteColor);
        scoreLabel.setBackground(lightColor);
        scoreLabel.setForeground(whiteColor);
        boardPanel.setBackground(darkColor);

        for (Component c : boardPanel.getComponents()) {
            if (c instanceof JButton) {
                c.setBackground(darkColor);
                c.setForeground(whiteColor);
            }
        }
    }

    void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                int minutes = timeLeft / 60;
                int seconds = timeLeft % 60;
                timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));

                if (timeLeft <= 0) {
                    timer.stop();
                    gameOver = true;
                    textLabel.setText("Time's Up!");
                    showEndGamePopup("Time's Up! Your Score: " + score);
                }
            }
        });
        timer.start();
    }

    void startModifiers() {
        modifierTimer = new Timer(60000, new ActionListener() { // every 60 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                applyRandomModifier();
            }
        });
        modifierTimer.start();
    }

    void applyRandomModifier() {
        String[] modifiers = {
            "Timer Speed Up",
            "Remove Flags",
            "Extra Mines"
        };

        String selectedModifier = modifiers[random.nextInt(modifiers.length)];
        showTemporaryMessage("Modifier: " + selectedModifier, 3000); // Show modifier for 3 seconds

        switch (selectedModifier) {
            case "Timer Speed Up":
                if (timer != null) {
                    timer.setDelay(500); // Speed up timer by making it tick every 0.5 seconds
                }
                break;
            case "Remove Flags":
                for (int r = 0; r < numRows; r++) {
                    for (int c = 0; c < numCols; c++) {
                        MineTile tile = board[r][c];
                        if (tile.getText().equals("🚩")) {
                            tile.setText("");
                        }
                    }
                }
                break;
            case "Extra Mines":
                addExtraMines();
                break;
        }
    }

    void addExtraMines() {
        int additionalMines = random.nextInt(5) + 1; // Add 1 to 5 extra mines
        while (additionalMines > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile tile = board[r][c];
            if (!mineList.contains(tile) && !tile.getText().equals("🚩")) {
                mineList.add(tile);
                additionalMines--;
            }
        }
    }

    void showTemporaryMessage(String message, int duration) {
        JLabel messageLabel = new JLabel(message, JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setOpaque(true);
        messageLabel.setBackground(Color.YELLOW);
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);
        frame.revalidate();
        frame.repaint();

        Timer messageTimer = new Timer(duration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().remove(messageLabel);
                frame.revalidate();
                frame.repaint();
            }
        });
        messageTimer.setRepeats(false);
        messageTimer.start();
    }

    void revealMines() {
        for (MineTile tile : mineList) {
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over!");
        showEndGamePopup("Game Over! Your Score: " + score);
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }

        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }

        tile.setEnabled(false);
        tilesClicked++;
        score += 200;
        scoreLabel.setText("Score: " + score);

        int minesFound = 0;

        minesFound += countMine(r - 1, c - 1);
        minesFound += countMine(r - 1, c);
        minesFound += countMine(r - 1, c + 1);

        minesFound += countMine(r, c - 1);
        minesFound += countMine(r, c + 1);

        minesFound += countMine(r + 1, c - 1);
        minesFound += countMine(r + 1, c);
        minesFound += countMine(r + 1, c + 1);

        if (minesFound > 0) {
            tile.setText(String.valueOf(minesFound));
        } else {
            tile.setText("");

            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);

            checkMine(r, c - 1);
            checkMine(r, c + 1);

            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
            showEndGamePopup("You Win! Your Score: " + score);
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }

    void showEndGamePopup(String message) {
        for (MineTile tile : mineList) {
            if (tile.getText().equals("🚩")) {
                score += 2000;
            }
        }

        int option = JOptionPane.showOptionDialog(frame,
                message + " Do you want to exit or return to the main menu?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Exit", "Main Menu"},
                "Exit");

        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else {
            frame.dispose();
            new MainMenu();
        }
    }
}
