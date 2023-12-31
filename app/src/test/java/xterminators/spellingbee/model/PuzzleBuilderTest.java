package xterminators.spellingbee.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PuzzleBuilderTest {
    private static final File dictionaryFile = Paths.get(
        "src", "main", "resources", "dictionaries", "dictionary_optimized.txt"
    ).toFile();

    private static final File rootsDictionaryFile = Paths.get(
        "src", "main", "resources", "dictionaries", "dictionary_roots.txt"
    ).toFile();

    @Test
    public void testNew_BadFullDictionary() {
        FileNotFoundException exception = assertThrows(
            FileNotFoundException.class,
            () -> new PuzzleBuilder(
                new File("badFullDict.txt"),
                rootsDictionaryFile
            ),
            "The PuzzleBuilder constructor should throw a FileNotFoundException " +
            "if the full dictionary does not exist."
        );

        assertTrue(exception.getMessage().contains("full dictionary"));
    }

    @Test
    public void testNew_BadRootDictionary() {
        FileNotFoundException exception = assertThrows(
            FileNotFoundException.class,
            () -> new PuzzleBuilder(
                dictionaryFile,
                new File("badRootDictionary.txt")
            ),
            "The PuzzleBuilder constructor should throw a FileNotFoundException " +
            "if the root dictionary does not exist."
        );

        assertTrue(exception.getMessage().contains("root dictionary"));
    }

    @Test
    public void testNew_ValidDictionaries() {
        PuzzleBuilder builder = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );
    }

    @Test
    public void testSetRootWord_BadRoots() {
        PuzzleBuilder builder = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );

        assertFalse(
            builder.setRootWord(null),
            "setRootWord on a null root should return false."
        );

        assertFalse(
            builder.setRootWord(""),
            "setRootWord on an empty string root should return false."
        );

        assertFalse(
            builder.setRootWord("hand"),
            "setRootWord on a too short root should return false."
        );

        assertFalse(
            builder.setRootWord("entries"),
            "setRootWord on a root with too few unique letters should return " +
            "false."
        );

        assertFalse(
            builder.setRootWord("guardians"),
            "setRootWord on root with too many unique letters should return " +
            "false."
        );
    }

    @Test
    public void testSetRootWord_FileError(@TempDir File tempDir) {
        File badFullDict = new File(tempDir, "badFullDict.txt");
        
        try (FileWriter badFullDictWriter = new FileWriter(badFullDict)) {
            badFullDictWriter.write("This is not a valid dictionary.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        File badRootDict = new File(tempDir, "badRootDict.txt");

        try (FileWriter badRootDictWriter = new FileWriter(badRootDict)) {
            badRootDictWriter.write("This is not a valid root dictionary.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        PuzzleBuilder builder = assertDoesNotThrow(
            () -> new PuzzleBuilder(badFullDict, badRootDict),
            "The PuzzleBuilder constructor should not throw if the dictionary " +
            "files exist."
        );

        badFullDict.delete();
        badRootDict.delete();

        assertFalse(
            builder.setRootWord("offhanded"),
            "setRootWord should return false if the root dictionary file does " +
            "not exist."
        );
    }

    @Test
    public void testSetRootWord_ValidWord() {
        PuzzleBuilder builder = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );

        assertTrue(
            builder.setRootWord("offhanded"),
            "setRootWord should return true for a valid root word."
        );

        assertTrue(
            builder.setRootWord("violent"),
            "setRootWord should return true for a valid root word."
        );

        assertTrue(
            builder.setRootWord("guardian"),
            "setRootWord should return true for a valid root word."
        );
    }

    @Test
    public void testSetRootAndRequired_BadWord() {
        PuzzleBuilder builder = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );

        assertFalse(
            builder.setRootAndRequiredLetter(null, '\0'),
            "setRootAndRequiredLetter on a null root should return false."
        );

        assertFalse(
            builder.setRootAndRequiredLetter("", '\0'),
            "setRootAndRequiredLetter on an empty string root should return " +
            "false."
        );

        assertFalse(
            builder.setRootAndRequiredLetter("hand", 'a'),
            "setRootAndRequiredLetter on a too short root should return false."
        );

        assertFalse(
            builder.setRootAndRequiredLetter("entries", 't'),
            "setRootAndRequiredLetter on a root with too few unique letters " +
            "should return false."
        );

        assertFalse(
            builder.setRootAndRequiredLetter("guardians", 'g'),
            "setRootAndRequiredLetter on root with too many unique letters " +
            "should return false."
        );
    }

    @Test
    public void testSetRootAndRequired_BadRequiredLetter() {
        PuzzleBuilder builder = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );

        assertFalse(
            builder.setRootAndRequiredLetter("offhanded", '\0'),
            "setRootAndRequiredLetter with a non-alphabetical character should " +
            "return false."
        );

        assertFalse(
            builder.setRootAndRequiredLetter("offhanded", 'z'),
            "setRootAndRequiredLetter with a character not in the root should " +
            "return false."
        );
    }

    @Test
    public void testSetRootAndRequired_ValidWordAndRequiredLetter() {
        PuzzleBuilder builder = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );

        assertTrue(
            builder.setRootAndRequiredLetter("offhanded", 'a'),
            "setRootWord should return true for a valid root word and required " +
            "letter."
        );

        assertTrue(
            builder.setRootAndRequiredLetter("violent", 'i'),
            "setRootWord should return true for a valid root word and required " +
            "letter."
        );

        assertTrue(
            builder.setRootAndRequiredLetter("guardian", 'd'),
            "setRootWord should return true for a valid root word and required " +
            "letter."
        );
    }

    @Test
    public void testBuild_SetRootAndLetter() {
        PuzzleBuilder builder = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );

        assertTrue(
            builder.setRootAndRequiredLetter("offhanded", 'a'),
            "setRootWord should return true for a valid root word and required " +
            "letter."
        );

        Puzzle puzzle = assertDoesNotThrow(
            () -> builder.build(),
            "build should not throw if the builder has a valid root word and " +
            "required letter."
        );

        assertPuzzle(
            puzzle,
            'a',
            new char[] {'d', 'e', 'f', 'h', 'n', 'o'},
            0,
            List.of(),
            Rank.BEGINNER
        );

        assertTrue(
            builder.setRootAndRequiredLetter("violent", 'i'),
            "setRootWord should return true for a valid root word and required " +
            "letter."
        );

        puzzle = assertDoesNotThrow(
            () -> builder.build(),
            "build should not throw if the builder has a valid root word and " +
            "required letter."
        );

        assertPuzzle(
            puzzle,
            'i',
            new char[] {'e', 'l', 'n', 'o', 't', 'v'},
            0,
            List.of(),
            Rank.BEGINNER
        );

        assertTrue(
            builder.setRootAndRequiredLetter("guardian", 'd'),
            "setRootWord should return true for a valid root word and required " +
            "letter."
        );

        puzzle = assertDoesNotThrow(
            () -> builder.build(),
            "build should not throw if the builder has a valid root word and " +
            "required letter."
        );

        assertPuzzle(
            puzzle,
            'd',
            new char[] {'a', 'g', 'i', 'n', 'r', 'u'},
            0,
            List.of(),
            Rank.BEGINNER
        );
    }

    @Test
    public void testBuild_SetRoot() {
        Random rng = new Random(0l);

        PuzzleBuilder builder = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );

        assertTrue(
            builder.setRootWord("offhanded"),
            "setRootWord should return true for a valid root word."
        );

        Puzzle puzzle = assertDoesNotThrow(
            () -> builder.build(rng),
            "build should not throw if the builder has a valid root word."
        );

        assertPuzzle(
            puzzle,
            'd',
            new char[] {'a', 'e', 'f', 'h', 'n', 'o'},
            0,
            List.of(),
            Rank.BEGINNER
        );

        assertTrue(
            builder.setRootWord("violent"),
            "setRootWord should return true for a valid root word."
        );

        puzzle = assertDoesNotThrow(
            () -> builder.build(rng),
            "build should not throw if the builder has a valid root word."
        );

        assertPuzzle(
            puzzle,
            'o',
            new char[] {'e', 'i', 'l', 'n', 't', 'v'},
            0,
            List.of(),
            Rank.BEGINNER
        );

        assertTrue(
            builder.setRootWord("guardian"),
            "setRootWord should return true for a valid root word."
        );

        puzzle = assertDoesNotThrow(
            () -> builder.build(rng),
            "build should not throw if the builder has a valid root word."
        );

        assertPuzzle(
            puzzle,
            'd',
            new char[] {'a', 'g', 'i', 'n', 'r', 'u'},
            0,
            List.of(),
            Rank.BEGINNER
        );
    }

    @Test
    public void testBuild_Random() {
        Random rng = new Random(0l);

        PuzzleBuilder builder1 = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );

        Puzzle puzzle = assertDoesNotThrow(
            () -> builder1.build(rng),
            "build should not throw if the builder has a root dictionary."
        );

        // invocation primary: c (rand line 32661, rand prim index 4)

        assertPuzzle(
            puzzle,
            'c',
            new char[] {'i', 'n', 'o', 't', 'v', 'a'},
            0,
            List.of(),
            Rank.BEGINNER
        );

        PuzzleBuilder builder2 = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );

        puzzle = assertDoesNotThrow(
            () -> builder2.build(rng),
            "build should not throw if the builder has a root dictionary."
        );

        // thripple primary: t (rand line 65010, rand prim index 0)

        assertPuzzle(
            puzzle,
            't',
            new char[] {'e', 'h', 'i', 'l', 'p', 'r'},
            0,
            List.of(),
            Rank.BEGINNER
        );

        PuzzleBuilder builder3 = assertDoesNotThrow(
            () -> new PuzzleBuilder(dictionaryFile, rootsDictionaryFile),
            "The PuzzleBuilder constructor should not throw if the dictionaries " +
            "are valid."
        );

        puzzle = assertDoesNotThrow(
            () -> builder3.build(rng),
            "build should not throw if the builder has a root dictionary."
        );

        // wisplike primary: e (rand line 73511, rand prim index 6)

        assertPuzzle(
            puzzle,
            'e',
            new char[] {'i', 'k', 'l', 'p', 's', 'w'},
            0,
            List.of(),
            Rank.BEGINNER
        );
    }

    /**
     * Asserts a Puzzle has all the given attributes.
     * 
     * @param puzzle
     * @param primaryLetter
     * @param secondaryLetters
     * @param totalPoints
     * @param earnedPoints
     * @param foundWords
     * @param rank
     */
    void assertPuzzle(
        Puzzle puzzle,
        char primaryLetter,
        char[] secondaryLetters,
        int earnedPoints,
        List<String> foundWords,
        Rank rank
    ) {
        assertEquals(
            puzzle.getPrimaryLetter(),
            primaryLetter,
            "The primary letter of the puzzle did not match expected."
        );

        char[] sortedPuzzleSecondaryLetters = puzzle.getSecondaryLetters();
        Arrays.sort(sortedPuzzleSecondaryLetters);
        Arrays.sort(secondaryLetters);
        assertTrue(
            Arrays.equals(sortedPuzzleSecondaryLetters, secondaryLetters),
            "The secondary letters of the puzzle did not match expected." +
            "Expected: " + Arrays.toString(secondaryLetters) + "\n" + "Actual: "
            + Arrays.toString(sortedPuzzleSecondaryLetters)
        );

        assertEquals(
            puzzle.getEarnedPoints(),
            earnedPoints,
            "The earned points of the puzzle did not match expected."
        );

        assertEquals(
            puzzle.getFoundWords(),
            foundWords,
            "The found words of the puzzle did not match expected."
        );

        assertEquals(
            puzzle.getRank(),
            rank,
            "The rank of the puzzle did not match expected."
        );
    }
}
