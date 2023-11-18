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
        scores = new TreeMap<>();
        lowestHighScore = Integer.MAX_VALUE;
        loadScores();
    }

    public TreeMap<String, Integer> getScores() {
        return scores;
    }

    public static void loadScores() {
        File scoreFile = new File(System.getProperty("user.home"));

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
            scores = gson.fromJson(json.toString(), type);

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
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean saveScore(String userName, int score) {
        if (scores.size() >= MAX_STORED_SCORES) {

        }
        scores.put(userName, score);
        return saveScores();
    }

    public boolean isHighScore(int score) {
        return score >= lowestHighScore || scores.size() < MAX_STORED_SCORES;
    }
}