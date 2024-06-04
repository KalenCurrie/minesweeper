import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {
    private class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 70;
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

    int mineCount; // number of mines
    MineTile[][] board;
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0; // goal is to click all tiles except the ones containing mines
    boolean gameOver = false;
    boolean isZenMode;
    int score = 0;
    Timer timer;
    int timeLeft = 900; // 15 minutes in seconds

    public Minesweeper(boolean isDarkMode, boolean isZenMode, int mineCount, int boardSize) {
        this.isZenMode = isZenMode;
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
    }

    void setupGame() {
        frame.setSize(boardWidth, boardHeight + 100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("Time Left: 15:00");

        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setText("Score: 0");

        textPanel.setLayout(new GridLayout(1, 3));
        textPanel.add(textLabel);
        textPanel.add(timerLabel);
        textPanel.add(scoreLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols)); //8x8
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();

                        //left click
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText().equals("🚩")) {
                                return;
                            }

                            //check if tile clicked contains a mine
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

        for (MineTile[] row : board) {
            for (MineTile tile : row) {
                tile.setBackground(lightColor);
                tile.setForeground(whiteColor);
            }
        }
    }

    void revealMines() {
        for (MineTile tile : mineList) {
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over!");
        if (!isZenMode) {
            showEndGamePopup("Game Over! Your Score: " + score);
        } else {
            showEndGamePopup("Game Over!");
        }
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
        tilesClicked += 1;

        if (!isZenMode) {
            score += 200;
            scoreLabel.setText("Score: " + score);
        }

        int minesFound = 0;

        //top 3
        minesFound += countMine(r-1, c-1);  //top left
        minesFound += countMine(r-1, c);    //top
        minesFound += countMine(r-1, c+1);  //top right

        //left and right
        minesFound += countMine(r, c-1);    //left
        minesFound += countMine(r, c+1);    //right

        //bottom 3
        minesFound += countMine(r+1, c-1);  //bottom left
        minesFound += countMine(r+1, c);    //bottom
        minesFound += countMine(r+1, c+1);  //bottom right

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");
            
            //top 3
            checkMine(r-1, c-1);    //top left
            checkMine(r-1, c);      //top
            checkMine(r-1, c+1);    //top right

            //left and right
            checkMine(r, c-1);      //left
            checkMine(r, c+1);      //right

            //bottom 3
            checkMine(r+1, c-1);    //bottom left
            checkMine(r+1, c);      //bottom
            checkMine(r+1, c+1);    //bottom right
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
            if (!isZenMode) {
                calculateFinalScore();
                showEndGamePopup("You Win! Your Score: " + score);
            } else {
                showEndGamePopup("You Win!");
            }
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

    void calculateFinalScore() {
        for (MineTile tile : mineList) {
            if (tile.getText().equals("🚩")) {
                score += 2000;
            }
        }
        scoreLabel.setText("Score: " + score);
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

    void showEndGamePopup(String message) {
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
