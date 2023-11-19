package xterminators.spellingbee.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.TreeMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class HighScoresTest {

    //***** Non-Test Functions *****/

    private void deleteScoreFile() {
        String userHome = System.getProperty("user.home");
        String filePath = userHome + File.separator + "HighScores.json";

        File scoreFile = new File(filePath);

        if (scoreFile.exists()) {
            scoreFile.delete();
        }
    }

    @BeforeEach 
    public void setup() {
        // Always delete score file so that
        // test knows that one doesn't exist
        deleteScoreFile();
    }

    @AfterEach 
    public void tearDown() {
        deleteScoreFile();
    }

    //***** End Non-Test Functions *****/

    @Test 
    public void saveNewHighScore_EmptyUsername() {
        HighScores highScores = new HighScores();

        assertFalse(highScores.saveScore("", 10));
    }

    @Test 
    public void saveNewHighScore_ValidUsername() {
        HighScores highScores = new HighScores();

        assertTrue(highScores.saveScore("xterminator", 10));
        assertEquals(highScores.getScores().size(), 1);
    }

    @Test 
    public void saveHighScores_EmptyScoreList() {
        HighScores highScores = new HighScores();

        assertEquals(highScores.getScores().size(), 0);
        assertTrue(highScores.saveScores());
    }
     
    @Test 
    public void loadHighScores_NoFileExists() {

        HighScores highScores = new HighScores();
        highScores.loadScores();

        assertNotNull(highScores.getScores());
        assertEquals(highScores.getScores().size(), 0);
    }

    @Test 
    public void loadHighScores_FileExists() {

        HighScores highScores = new HighScores();

        highScores.saveScore("xterminator", 10);
        highScores.saveScores();

        // high scores are loaded in constructor
        HighScores highScores1 = new HighScores();
        TreeMap<String, Integer> scores = highScores1.getScores();
        assertEquals(scores.size(), 1);
        Map.Entry<String, Integer> firstEntry = scores.firstEntry();
        assertEquals(firstEntry.getKey(), "xterminator");
        assertEquals(firstEntry.getValue(), 10);        
    }

    @Test 
    public void isHighScore_NotHighScore() {
        HighScores highScore = new HighScores();
        for (int i = 0; i < 10; i++) {
            highScore.saveScore("xterminator" + i, 10 + i);
        }
        assertEquals(highScore.getScores().size(), 10);
        assertFalse(highScore.isHighScore(5));
    }

    @Test 
    public void isHighScore_IsHighScore() {
        HighScores highScore = new HighScores();
        for (int i = 0; i < 10; i++) {
            highScore.saveScore("xterminator" + i, 10 + i);
        }
        assertEquals(highScore.getScores().size(), 10);
        assertTrue(highScore.isHighScore(25));
    }
}
