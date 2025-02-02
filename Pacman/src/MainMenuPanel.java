import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainMenuPanel extends JPanel {

    private HighScoreManager highScoreManager;

    public MainMenuPanel(PacmanGame game) {
        this.highScoreManager = new HighScoreManager();

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Main Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton newGameButton = new JButton("New Game");
        JButton highScoresButton = new JButton("High Scores");
        JButton exitButton = new JButton("Exit");

        buttonPanel.add(newGameButton);
        buttonPanel.add(highScoresButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.showGridSizeSelection();
            }
        });

        highScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHighScores();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void showHighScores() {
        List<HighScore> highScores = highScoreManager.getHighScores();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (HighScore highScore : highScores) {
            listModel.addElement(highScore.toString());
        }

        JList<String> scoreList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(scoreList);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }
}
