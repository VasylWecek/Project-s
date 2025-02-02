package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.HighScore;
import model.HighScoreManager;

public class HighScoreView extends JFrame {
    private HighScoreManager highScoreManager;
    private JList<String> highScoresList; 

    public HighScoreView(int finalScore, String difficulty, int timeElapsed, String playerName) {
        highScoreManager = new HighScoreManager();

        setTitle("High Score");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        highScoresList = new JList<>();
        displayHighScores();

        JScrollPane scrollPane = new JScrollPane(highScoresList); 
        add(scrollPane, BorderLayout.CENTER);

        
        highScoreManager.addHighScore(playerName, finalScore, difficulty, timeElapsed);
    }

    
    private void displayHighScores() {
        List<HighScore> highScores = highScoreManager.getHighScores(); 
        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (HighScore score : highScores) {
            listModel.addElement(String.format("%s - %d (Difficulty: %s, Time: %d seconds)",
                    score.getPlayerName(), score.getScore(), score.getDifficulty(), score.getTimeElapsed()));
        }

        highScoresList.setModel(listModel); 
    }

    
    public static void showHighScoreView(int finalScore, String difficulty, int timeElapsed, String playerName) {
        SwingUtilities.invokeLater(() -> {
            HighScoreView highScoreView = new HighScoreView(finalScore, difficulty, timeElapsed, playerName);
            highScoreView.setVisible(true); 
        });
    }
}
