package xterminators.spellingbee.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import xterminators.spellingbee.model.Puzzle.PuzzleData;

public class PuzzleTest {
    private static final File dictionaryFile = Paths.get(
        "src", "main", "resources", "dictionaries", "dictionary_optimized.txt"
    ).toFile();

    @Test
    public void testLoadPuzzle_BadData() {
        PuzzleData tooSmall = new PuzzleData(
            new char[] {'a', 'o', 'f', 'h'},
            'a',
            List.of(),
            0,
            0
        );

        assertThrows(IllegalArgumentException.class, 
            () -> new Puzzle(tooSmall, dictionaryFile),
            "Puzzle constructor should throw an exception if the puzzle data is"
            + " has too few letters."
        );

        PuzzleData tooBig = new PuzzleData(
            new char[] {'g', 'u', 'a', 'r', 'd', 'i', 'n', 's'},
            'a',
            List.of(),
            0,
            0
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new Puzzle(tooBig, dictionaryFile),
            "Puzzle constructor should throw an exception if the puzzle data is"
            + " has too many letters."
        );

        PuzzleData badReqLetter = new PuzzleData(
            new char[] {'g', 'u', 'a', 'r', 'd', 'i', 'n'},
            'z',
            List.of(),
            0,
            0
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new Puzzle(badReqLetter, dictionaryFile),
            "Puzzle constructor should throw an exception if the puzzle data is"
            + " has a required letter that is not in the puzzle."
        );
    }

    @Test
    public void testLoadPuzzle_ValidData() {
        PuzzleData validData = new PuzzleData(
            new char[] {'g', 'u', 'a', 'r', 'd', 'i', 'n'},
            'g',
            List.of("guardian", "guard"),
            20,
            0
        );

        Puzzle puzzle = assertDoesNotThrow(
            () -> new Puzzle(validData, dictionaryFile),
            "Puzzle constructor should not throw an exception if the puzzle data"
            + " is valid."
        );

        assertEquals(
            puzzle.getPrimaryLetter(),
            'g',
            "Puzzle constructor should set the primary letter correctly."
        );

        char[] sortedSecondaryLetters = puzzle.getSecondaryLetters();
        Arrays.sort(sortedSecondaryLetters);

        char[] expectedSecondaryLetters = new char[] {'a', 'd', 'i', 'n', 'r', 'u'};

        assertTrue(
            Arrays.equals(sortedSecondaryLetters, expectedSecondaryLetters),
            "Puzzle constructor should set the secondary letters correctly."
        );

        assertEquals(
            puzzle.getFoundWords(),
            validData.foundWords(),
            "Puzzle constructor should set the found words correctly."
        );

        assertEquals(
            puzzle.getEarnedPoints(),
            validData.playerPoints(),
            "Puzzle constructor should set the earned points correctly."
        );
    }
}
