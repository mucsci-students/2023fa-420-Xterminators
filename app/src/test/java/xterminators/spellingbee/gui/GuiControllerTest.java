package xterminators.spellingbee.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;

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
        controller.createNewPuzzle("", 'a');

        assertNotNull("Puzzle null after createNewPuzzle(\"\", 'a').", 
                        controller.getPuzzle());
        assertValidInitialPuzzle(controller.getPuzzle());
    }

    @Test
    public void testCreateNewPuzzleWithBaseWord() {
        controller.createNewPuzzle("violent", 'l');

        assertNotNull("Puzzle null after createNewPuzzle(\"violent\", 'l').", 
                        controller.getPuzzle());
        assertValidInitialPuzzle(controller.getPuzzle());
    }

    @Test 
    public void testCreateNewPuzzleWithInvalidBaseWord() {
        // Test short base word
        assertThrows(IllegalArgumentException, 
                    controller.createNewPuzzle("vvv", 'v'));
        // Test base word not in dictionary
        assertThrows(IllegalArgumentException, 
                    controller.createNewPuzzle("1234567", '3'));
        // Test base word without 7 unique letters
        assertThrows(IllegalArgumentException,
                    controller.createNewPuzzle("bookkeeper", 'b'));
    }

    @Test 
    public void testCreateNewPuzzleWithInvalidPrimaryLetter() {
        // Test primary letter not in base word
        assertThrows(IllegalArgumentException,
                    controller.createNewPuzzle("violent", 'b'));
    }

    @RepeatedTest(5)
    public void testCreateNewRandomPuzzle() {
        controller.createNewPuzzle();

        assertNotNull("Puzzle null after createNewPuzzle().", 
                        controller.getPuzzle());
        assertValidInitialPuzzle(controller.getPuzzle());
    }

    @Test 
    public void testShufflePuzzleWithNoPuzzle() {
        assertThrows(NullPointerException, controller.shuffle());
    }

    @Test 
    public void testShufflePuzzle() {
        controller.createNewPuzzle();

        Puzzle p = controller.getPuzzle();
        String original = p.getSecondaryLetters();
        controller.shuffle();
        String reshuffled = p.getSecondaryLetters();
        assertNotEquals("Puzzle letter order unchanged after shuffle", 
                        original, reshuffled);
    }

    @Test 
    public void testGuessNull() {
        controller.createNewPuzzle();
        
        assertEquals(controller.guess(null), 
                    "\"\" was not a valid word. Try again!");
    }

    @Test 
    public void testGuessEmptyString() {
        controller.createNewPuzzle();

        assertEquals(controller.guess(""), 
                    "\"\" was not a valid word. Try again!");
    }

    @Test 
    public void testGuessInvalidWord() {
        controller.createNewPuzzle("violent", 'o');

        assertEquals(controller.guess("live"), 
                    "\"live\" was not a valid word. Try again!");
    }

    @Test 
    public void testGuessValidWord() {
        controller.createNewPuzzle("violent", 'l');

        assertEquals(controller.guess("live"),
                    "Good job! Your word was worth 1 point.");
    }

    @Test
    public void testGuessFoundWord() {
        controller.createNewPuzzle("violent", 'l');
        controller.guess("live");

        assertEquals(controller.guess("live"),
                    "You already found this word.");
    }

    @Test 
    public void testGuessValidWordWithRankAdvancement() {
        controller.createNewPuzzle("violent", 'l');
        controller.guess("violent");
        controller.guess("liven");
        controller.guess("novel");
        controller.guess("little");

        assertEquals(controller.guess("lentil"),
                    "Good job! Your word was worth 6 points." + 
                    "You reached a new rank! Your rank is now " +
                    "Good Start.");
    }
}
