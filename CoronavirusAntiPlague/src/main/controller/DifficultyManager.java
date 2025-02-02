package controller;

public class DifficultyManager {

    
    public static int getFinalScore(String difficulty) {
        int finalScore = 0;

        
        System.out.println("Received difficulty: " + difficulty);

        switch (difficulty) {
            case "Easy":
                finalScore = 100;
                break;
            case "Medium":
                finalScore = 50;
                break;
            case "Hard":
                finalScore = 10;
                break;
            default:
                finalScore = 0; 
                System.out.println("No corresponding difficulty found.");
        }

        
        System.out.println("Assigned points for difficulty " + difficulty + ": " + finalScore);

        return finalScore;
    }
}
