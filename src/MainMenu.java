import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {
    private JFrame frame;
    private JCheckBox darkModeCheckBox;
    private JCheckBox zenModeCheckBox;
    private JCheckBox modifiersCheckBox;
    private JTextField bombCountField;
    private JTextField boardSizeField;

    public MainMenu() {
        frame = new JFrame("MineModder Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("MineModder", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(titleLabel, gbc);

        darkModeCheckBox = new JCheckBox("Dark Mode");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(darkModeCheckBox, gbc);

        zenModeCheckBox = new JCheckBox("Zen Mode");
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(zenModeCheckBox, gbc);

        modifiersCheckBox = new JCheckBox("Enable Modifiers");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.add(modifiersCheckBox, gbc);

        JLabel bombCountLabel = new JLabel("Number of Bombs (3-100):", JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        frame.add(bombCountLabel, gbc);

        bombCountField = new JTextField("10", 10);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        frame.add(bombCountField, gbc);

        JLabel boardSizeLabel = new JLabel("Board Size (5-16):", JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        frame.add(boardSizeLabel, gbc);

        boardSizeField = new JTextField("8", 10);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        frame.add(boardSizeField, gbc);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isDarkMode = darkModeCheckBox.isSelected();
                boolean isZenMode = zenModeCheckBox.isSelected();
                boolean areModifiersEnabled = modifiersCheckBox.isSelected();
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
                new Minesweeper(isDarkMode, isZenMode, areModifiersEnabled, bombCount, boardSize);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        frame.add(startButton, gbc);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}
