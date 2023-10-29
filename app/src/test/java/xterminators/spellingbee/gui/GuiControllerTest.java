package xterminators.spellingbee.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import xterminators.spellingbee.gui.GuiController;
import xterminators.spellingbee.gui.GuiView;

import xterminators.spellingbee.model.Rank;
import xterminators.spellingbee.model.Puzzle;


public class GuiControllerTest {
    GuiView view;
    GuiController controller;

    @BeforeEach
    public void setup() {
        File dictionaryFile = new File(
            Paths.get("src", "main", "resources", "dictionaries", "dictionary_optimized.txt").toString()
        );

        File rootsDictionaryFile = new File(
            Paths.get("src", "main", "resources", "dictionaries", "dictionary_roots.txt").toString()
        );

        view = mock(GuiView.class);

        controller = new GuiController(view, dictionaryFile, rootsDictionaryFile);
    }

    @AfterEach
    public void tearDown() {
        view = null;
        controller = null;

        // Sets the singleton instance of Puzzle to null to emulate a fresh
        // instance of the program.
        try {
            Field instance = Puzzle.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void assertValidInitialPuzzle(Puzzle p) {
        assertNotNull(p.getPrimaryLetter());
        assertNotNull(p.getSecondaryLetters());
        assertNotNull(p.getFoundWords());
        assertEquals(p.getEarnedPoints(), 0);
        assertEquals(p.getRank(), Rank.BEGINNER);
    }

    @Test 
    public void testCreateNewPuzzleEmptyInput() {
        try {
            controller.createNewPuzzle("", 'a');
        } catch (Exception e) {}

        assertNotNull(Puzzle.getInstance(),
                        "Puzzle null after createNewPuzzle(\"\", 'a').");
        assertValidInitialPuzzle(Puzzle.getInstance());
    }

    @Test
    public void testCreateNewPuzzleWithBaseWord() {
        try {
            controller.createNewPuzzle("violent", 'l');
        } catch (Exception e) {}

        Puzzle p = Puzzle.getInstance();

        assertNotNull(p, 
                        "Puzzle null after createNewPuzzle(\"violent\", 'l').");
        assertValidInitialPuzzle(p);
    }

    @Test 
    public void testCreateNewPuzzleWithInvalidBaseWord() {
        // Test short base word
        assertThrows(IllegalArgumentException.class, () -> {
                    controller.createNewPuzzle("vvv", 'v');
                    });
        // Test base word not in dictionary
        assertThrows(IllegalArgumentException.class, () -> {
                    controller.createNewPuzzle("1234567", '3');
                    });
        // Test base word without 7 unique letters
        assertThrows(IllegalArgumentException.class, () -> {
                    controller.createNewPuzzle("bookkeeper", 'b');
                    });
    }

    @Test 
    public void testCreateNewPuzzleWithInvalidPrimaryLetter() {
        // Test primary letter not in base word
        assertThrows(IllegalArgumentException.class, () -> {
                    controller.createNewPuzzle("violent", 'b');
                    });
    }

    @RepeatedTest(5)
    public void testCreateNewRandomPuzzle() {
        try {
            controller.createNewPuzzle();
        } catch (Exception e) {}

        assertNotNull(Puzzle.getInstance(),
                        "Puzzle null after createNewPuzzle().");
        assertValidInitialPuzzle(Puzzle.getInstance());
    }

    @Test 
    public void testShufflePuzzleWithNoPuzzle() {
        assertThrows(NullPointerException.class, () -> {
            controller.shuffleLetters();
        });
    }

    @Test 
    public void testShufflePuzzle() {
        try {
            controller.createNewPuzzle();
        } catch (Exception e) {}

        Puzzle p = Puzzle.getInstance();
        String original = "";
        for (char c : p.getSecondaryLetters()) {
            original += c;
        }
        controller.shuffleLetters();
        String reshuffled = "";
        for (char c : p.getSecondaryLetters()) {
            reshuffled += c;
        }
        assertNotEquals("Puzzle letter order unchanged after shuffle", 
                        original, reshuffled);
    }

    @Test 
    public void testSavePuzzle() {
        try {
            // Create random puzzle because
            // order of characters in filename can
            // change regardless of word
            controller.createNewPuzzle();

            String result = controller.savePuzzle();

            assertNotNull(result);
            // Can't assert whole string because 
            // savePuzzle() makes filename in order
            // of characters in puzzle letter array
            assertEquals("File created:", result.substring(0, 13));
            
            String filename = result.substring(13, 12);
            Path filepath = Paths.get(filename);
            Files.delete(filepath);
        } catch (Exception e) {}
    }

    @Test 
    public void testLoadPuzzle() {
        try {
            controller.createNewPuzzle();
            String result = controller.savePuzzle();

            // Get substring "1234567.json" from
            // result string "File created: 1234567.json"
            String filename = result.substring(13, 12);

            assertEquals("Succesfully loaded " + filename + "!",
                        controller.loadPuzzle(filename));

            Path filepath = Paths.get(filename);
            Files.delete(filepath);
        } catch (Exception e) {}
    }

    @Test 
    public void testGuessNull() {
        try {
            controller.createNewPuzzle();
        } catch (Exception e) {}
        
        assertEquals("\"\" was not a valid word. Try again!", 
                    controller.guessWord(null));
    }

    @Test 
    public void testGuessWithNullPuzzle() {
        assertEquals("No puzzle is loaded.",
                    controller.guessWord("word"));
    }

    @Test 
    public void testGuessEmptyString() {
        try {
            controller.createNewPuzzle();
        } catch (Exception e) {}

        assertEquals("\"\" was not a valid word. Try again!",
                    controller.guessWord(""));
    }

    @Test 
    public void testGuessInvalidWord() {
        try {
            controller.createNewPuzzle("violent", 'o');
        } catch (Exception e) {}

        assertEquals("\"live\" was not a valid word. Try again!",
                    controller.guessWord("live"));
    }

    @Test 
    public void testGuessValidWord() {
        try {
            controller.createNewPuzzle("violent", 'l');
        } catch (Exception e) {}

        assertEquals("Good job! Your word was worth 1 point.",
                    controller.guessWord("live"));
    }

    @Test
    public void testGuessFoundWord() {
        try {
            controller.createNewPuzzle("violent", 'l');
        } catch (Exception e) {}
        controller.guessWord("live");

        assertEquals("You already found this word.",
                    controller.guessWord("live"));
    }

    @Test 
    public void testGuessValidWordWithRankAdvancement() {
        try {
            controller.createNewPuzzle("violent", 'l');
        } catch (Exception e) {}
        controller.guessWord("violent");
        controller.guessWord("liven");
        controller.guessWord("novel");
        controller.guessWord("little");

        assertEquals(controller.guessWord("lentil"),
                    "Good job! Your word was worth 6 points." + 
                    "You reached a new rank! Your rank is now " +
                    "Good Start.");
    }
}
