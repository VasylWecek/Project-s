package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.HighScore;

public class HighScoreManager {
    private List<HighScore> highScores;  
    private final String fileName = "highscores.dat";

    public HighScoreManager() {
        highScores = loadHighScores();
    }

    
    public void addHighScore(String playerName, int score, String difficulty, int timeElapsed) {
        HighScore entry = new HighScore(playerName, score, difficulty, timeElapsed);
        highScores.add(entry);
        saveHighScores();
    }

    
    public List<HighScore> getHighScores() {
        return highScores;
    }

    
    private void saveHighScores() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Error saving records!");
            e.printStackTrace();
        }
    }

    
    private List<HighScore> loadHighScores() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (List<HighScore>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
