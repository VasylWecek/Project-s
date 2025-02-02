import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscores.ser";
    private List<HighScore> highScores;

    public HighScoreManager() {
        highScores = loadHighScores();
    }

    public void addHighScore(HighScore highScore) {
        highScores.add(highScore);
        Collections.sort(highScores, (hs1, hs2) -> Integer.compare(hs2.getScore(), hs1.getScore()));
        if (highScores.size() > 10) {
            highScores.remove(highScores.size() - 1);
        }
    }

    public List<HighScore> loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            return (List<HighScore>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<HighScore> getHighScores() {
        return highScores;
    }
}
