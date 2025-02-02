package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.DifficultyManager;  
import view.HighScoreView;

public class MainMenuView extends JFrame {

    public MainMenuView() {
        
        setTitle("Coronavirus AntiPlague");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        
        JButton newGameButton = new JButton("New Game");
        JButton highScoresButton = new JButton("High Scores");
        JButton exitButton = new JButton("Exit");

        
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDifficultySelection();  
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

        
        panel.add(newGameButton);
        panel.add(highScoresButton);
        panel.add(exitButton);

        
        add(panel);
        setVisible(true);
    }

    
    private void openDifficultySelection() {
        String[] options = {"Easy", "Medium", "Hard"};
        String difficulty = (String) JOptionPane.showInputDialog(
                this,
                "Choose difficulty level",
                "Difficulty Level",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0] 
        );

        
        if (difficulty != null) {
            System.out.println("Selected difficulty level: " + difficulty);
            
            int finalScore = DifficultyManager.getFinalScore(difficulty);
            System.out.println("Assigned points for difficulty " + difficulty + ": " + finalScore);

            
            view.GameView.open(difficulty, finalScore);  
        } else {
            System.out.println("No difficulty level selected!");
        }
    }

    
    private void showHighScores() {
        int finalScore = 100;  
        String difficulty = "Medium";  
        int timeElapsed = 120;  

        String playerName = JOptionPane.showInputDialog(this, "Enter your name for the leaderboard:");

        if (playerName != null && !playerName.trim().isEmpty()) {
            HighScoreView.showHighScoreView(finalScore, difficulty, timeElapsed, playerName);
        } else {
            JOptionPane.showMessageDialog(this, "Player name is required.");
        }
    }

    public static void main(String[] args) {
        new MainMenuView();  
    }
}
