package xterminators.spellingbee.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map;
import java.util.Collections;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HighScores {

    private static final int MAX_STORED_SCORES = 10;

    // Use TreeMap so elements are ordered
    private static TreeMap<String, Integer> scores;

    private static int lowestHighScore;

    private static final String FILE_NAME = "HighScores.json";

    public HighScores() {
        scores = new TreeMap<>(new ValueComparator());
        lowestHighScore = Integer.MAX_VALUE;
        loadScores();
    }

    /**
     * Returns the current high scores.
     */
    public TreeMap<String, Integer> getScores() {
        return scores;
    }

    /**
     * Loads the current high scores from the HighScores.json file.
     */
    public static void loadScores() {
        File scoreFile = new File(System.getProperty("user.home") + 
            File.separator + FILE_NAME);

        if (!scoreFile.exists()) {
            return;
        }

        try {
            Scanner reader = new Scanner(scoreFile);

            StringBuilder json = new StringBuilder();

            while(reader.hasNextLine()) {
                json.append(reader.nextLine());
            }

            reader.close();

            Gson gson = new Gson();
            Type type = new TypeToken<TreeMap<String, Integer>>() {}.getType();
            TreeMap<String, Integer> unsortedScores = 
                gson.fromJson(json.toString(), type);

            scores = 
                new TreeMap<>(new ValueComparator(unsortedScores));
            scores.putAll(unsortedScores);

            if (scores.size() > MAX_STORED_SCORES) {
                for (int i = MAX_STORED_SCORES; i < scores.size(); ++i) {
                    scores.pollLastEntry();
                }
            }

            for (Integer value : scores.values()) {
                if (value < lowestHighScore)
                    lowestHighScore = value;
            }

        } catch (FileNotFoundException ex) {
            // this can be ignored because it cannot ever be thrown
            // it could potentially be thrown by new Scanner()
        }

        if (lowestHighScore == Integer.MAX_VALUE) {
            // We could get here if there are no high scores saved
            // We are assuming that the user is not capable
            // of getting Integer.MAX_VALUE points
            lowestHighScore = 0;
        }
    }

    /**
     * Saves the current scores to the HighScores.json file.
     */
    public boolean saveScores() {
        try {
            String userHome = System.getProperty("user.home");
            String filePath = userHome + File.separator + FILE_NAME;
            File saveLocation = new File(filePath);

            Gson gson = new Gson();
            String json = gson.toJson(scores);

            FileWriter writer = new FileWriter(saveLocation);
            writer.write(json);
            writer.close();
            loadScores();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Adds a high score with the given username and score
     * to the high scores list, and then saves the high scores
     * list to the high scores file.
     * 
     * @param userName The username to save the high score with.
     * @param score The score of the high score.
     */
    public boolean saveScore(String userName, int score) {
        if (userName == null || userName.isEmpty()) {
            return false;
        }
        
        boolean foundKey = false;
        if (scores.size() > 0 ) {
            for (Map.Entry<String, Integer> e : scores.entrySet()) {
                if (e.getKey().equals(userName) && e.getValue() < score) {
                    e.setValue(score);
                    foundKey = true;
                    break;
                }
            }
        }

        if (!foundKey) {
            scores.put(userName, score);
        }

        if (score < lowestHighScore) {
            lowestHighScore = score;
        }
        return saveScores();
    }

    /**
     * Checks if the given score is greater than or equal to
     * the lowest high score, or if the high score list isn't full.
     * 
     * If it is greater than the lowest high score, or the score list
     * is not full, this will return true.
     */
    public boolean isHighScore(int score) {
        return score >= lowestHighScore || scores.size() < MAX_STORED_SCORES;
    }
}