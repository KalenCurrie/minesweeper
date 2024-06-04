import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {
    private JFrame frame;
    private JCheckBox darkModeCheckBox;
    private JCheckBox zenModeCheckBox;
    private JTextField bombCountField;
    private JTextField boardSizeField;

    public MainMenu() {
        frame = new JFrame("Minesweeper Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(new GridLayout(6, 1));

        JLabel titleLabel = new JLabel("Minesweeper", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(titleLabel);

        darkModeCheckBox = new JCheckBox("Dark Mode");
        frame.add(darkModeCheckBox);

        zenModeCheckBox = new JCheckBox("Zen Mode");
        frame.add(zenModeCheckBox);

        JLabel bombCountLabel = new JLabel("Number of Bombs (3-100):", JLabel.CENTER);
        frame.add(bombCountLabel);

        bombCountField = new JTextField("10");
        frame.add(bombCountField);

        JLabel boardSizeLabel = new JLabel("Board Size (5-16):", JLabel.CENTER);
        frame.add(boardSizeLabel);

        boardSizeField = new JTextField("8");
        frame.add(boardSizeField);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isDarkMode = darkModeCheckBox.isSelected();
                boolean isZenMode = zenModeCheckBox.isSelected();
                int bombCount = Integer.parseInt(bombCountField.getText());
                int boardSize = Integer.parseInt(boardSizeField.getText());

                if (bombCount < 3 || bombCount > 100) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number of bombs (3-100).");
                    return;
                }

                if (boardSize < 5 || boardSize > 16) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid board size (5-16).");
                    return;
                }

                frame.dispose();
                new Minesweeper(isDarkMode, isZenMode, bombCount, boardSize);
            }
        });
        frame.add(startButton);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}
