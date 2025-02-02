package controller;

import view.GameView;

public class GameController {

    public static void startNewGame(String difficulty) {
        System.out.println("The game starts at the level: " + difficulty);

        
        int finalScore = controller.DifficultyManager.getFinalScore(difficulty);

        
        GameView.open(difficulty, finalScore);  
    }
}
