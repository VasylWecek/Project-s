package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.GameController;

public class DifficultyView extends JFrame {

    public DifficultyView() {
        
        setTitle("Selecting the level of difficulty");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        
        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");

        
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectDifficulty("Easy");
            }
        });

        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectDifficulty("Medium");
            }
        });

        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectDifficulty("Hard");
            }
        });

        
        panel.add(easyButton);
        panel.add(mediumButton);
        panel.add(hardButton);

        
        add(panel);
        setVisible(true);
    }

    
    private void selectDifficulty(String difficulty) {
        JOptionPane.showMessageDialog(this, "Selected difficulty: " + difficulty);
        GameController.startNewGame(difficulty.toLowerCase()); 
        dispose();
    }


    
    public static void open() {
        new DifficultyView();
    }
}
