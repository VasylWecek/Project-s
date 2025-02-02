package model;

import java.io.Serializable;

public class HighScore implements Serializable {
    private String playerName;
    private int score;
    private String difficulty;
    private int timeElapsed;

    public HighScore(String playerName, int score, String difficulty, int timeElapsed) {
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
        this.timeElapsed = timeElapsed;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    @Override
    public String toString() {
        return playerName + " - " + score + " - " + difficulty + " - " + timeElapsed + "s";
    }
}
