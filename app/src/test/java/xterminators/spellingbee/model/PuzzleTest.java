package xterminators.spellingbee.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.gson.JsonSyntaxException;

public class PuzzleTest {
    private static final File dictionaryFile = Paths.get(
        "src", "main", "resources", "dictionaries", "dictionary_optimized.txt"
    ).toFile();

    private static final File rootsDictionaryFile = Paths.get(
        "src", "main", "resources", "dictionaries", "dictionary_roots.txt"
    ).toFile();

    @Test
    public void testLoadPuzzle_EmptyFile(@TempDir File tempDir) {
        File emptyFile = new File(tempDir, "empty.json");

        assertThrows(
            FileNotFoundException.class,
            () -> Puzzle.loadPuzzle(tempDir, emptyFile),
            "loadPuzzle should throw an exception if the puzzle file is empty."
        );
    }

    @Test
    public void testLoadPuzzle_BadJSON(@TempDir File tempDir) {
        File plainText = new File(tempDir, "text.txt");

        try(FileWriter writer = new FileWriter(plainText)) {
            writer.write("This is not valid JSON. This is text.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThrows(
            JsonSyntaxException.class,
            () -> Puzzle.loadPuzzle(plainText, dictionaryFile),
            "loadPuzzle should throw an exception if the puzzle file is not" +
            " valid JSON."
        );

        File badData = new File(tempDir, "bad_data.json");

        try (FileWriter writer = new FileWriter(badData)) {
            writer.write("{\n");
            writer.write("\t\"message\": \"This is not valid puzzle data.\",\n");
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThrows(
            JsonSyntaxException.class,
            () -> Puzzle.loadPuzzle(badData, dictionaryFile),
            "loadPuzzle should throw an exception if the puzzle file is not" +
            " valid puzzle data."
        );
    }

    @Test
    public void testLoadPuzzle_BadData(@TempDir File tempDir) {
        File tooSmall = new File(tempDir, "too_small.json");

        try (FileWriter writer = new FileWriter(tooSmall)) {
            writer.write("{\n");
            writer.write("\t\"baseWord\": [\'a\', \'o\', \'f\', \'h\'],\n");
            writer.write("\t\"requiredLetter\": \'a\',\n");
            writer.write("\t\"foundWords\": [],\n");
            writer.write("\t\"playerPoints\": 0,\n");
            writer.write("\t\"maxPoints\": 0\n");
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThrows(IllegalArgumentException.class, 
            () -> Puzzle.loadPuzzle(tooSmall, dictionaryFile),
            "loadPuzzle should throw an exception if the puzzle data base word" +
            " is too small."
        );

        File tooBig = new File(tempDir, "too_big.json");

        try (FileWriter writer = new FileWriter(tooBig)) {
            writer.write("{\n");
            writer.write("\t\"baseWord\": [\'g\', \'u\', \'a\', \'r\', \'d\', \'i\', \'n\', \'s\'],\n");
            writer.write("\t\"requiredLetter\": \'a\',\n");
            writer.write("\t\"foundWords\": [],\n");
            writer.write("\t\"playerPoints\": 0,\n");
            writer.write("\t\"maxPoints\": 0\n");
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThrows(
            IllegalArgumentException.class,
            () -> Puzzle.loadPuzzle(tooBig, dictionaryFile),
            "loadPuzzle should throw an exception if the puzzle data base word" +
            " is too big."
        );

        File badReqLetter = new File(tempDir, "bad_req_letter.json");

        try (FileWriter writer = new FileWriter(badReqLetter)) {
            writer.write("{\n");
            writer.write("\t\"baseWord\": [\'g\', \'u\', \'a\', \'r\', \'d\', \'i\', \'n\'],\n");
            writer.write("\t\"requiredLetter\": \'z\',\n");
            writer.write("\t\"foundWords\": [],\n");
            writer.write("\t\"playerPoints\": 0,\n");
            writer.write("\t\"maxPoints\": 0\n");
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThrows(
            IllegalArgumentException.class,
            () -> Puzzle.loadPuzzle(badReqLetter, dictionaryFile),
            "loadPuzzle should throw an exception if the puzzle data required" +
            " letter is not in the base word."
        );
    }

    @Test
    public void testLoadPuzzle_ValidData(@TempDir File tempDir) {
        File validData = new File(tempDir, "valid_data.json");

        try (FileWriter writer = new FileWriter(validData)) {
            writer.write("{\n");
            writer.write("\t\"baseWord\": [\"g\",\"u\",\"r\",\"d\",\"i\",\"n\",\"a\"],\n");
            writer.write("\t\"requiredLetter\": \'a\',\n");
            writer.write("\t\"foundWords\": [\"guard\",\"guardian\"],\n");
            writer.write("\t\"playerPoints\": 20,\n");
            writer.write("\t\"maxPoints\": 2243\n");
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Puzzle puzzle = assertDoesNotThrow(
            () -> Puzzle.loadPuzzle(validData, dictionaryFile),
            "loadPuzzle should not throw an exception if the puzzle data is" +
            " valid."
        );

        assertEquals(
            'a',
            puzzle.getPrimaryLetter(),
            "loadPuzzle should set the primary letter correctly."
        );

        char[] sortedSecondaryLetters = puzzle.getSecondaryLetters();
        Arrays.sort(sortedSecondaryLetters);

        char[] expectedSecondaryLetters = new char[] {'d', 'g', 'i', 'n', 'r', 'u'};

        assertArrayEquals(
            expectedSecondaryLetters,
            sortedSecondaryLetters,
            "loadPuzzle should set the secondary letters correctly."
        );

        assertEquals(
            List.of("guard", "guardian"),
            puzzle.getFoundWords(),
            "loadPuzzle should set the found words correctly."
        );

        assertEquals(
            20,
            puzzle.getEarnedPoints(),
            "loadPuzzle should set the earned points correctly."
        );
    }

    @Test
    public void testIsPangram_NotPangram() {
        try {
            PuzzleBuilder builder = new PuzzleBuilder(dictionaryFile, rootsDictionaryFile);
            builder.setRootAndRequiredLetter("guardian", 'a');

            Puzzle puzzle = builder.build();

            Method isPangram = Puzzle.class.getDeclaredMethod("isPangram", String.class);
            isPangram.setAccessible(true);
            assertFalse(
                (boolean) isPangram.invoke(puzzle, "zipline"),
                "isPangram should return false if the word is not a pangram."
            );

            assertFalse(
                (boolean) isPangram.invoke(puzzle, "guard"),
                "isPangram should return false if the word is not a pangram."
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIsPangram_Pangram() {
        try {
            PuzzleBuilder builder = new PuzzleBuilder(dictionaryFile, rootsDictionaryFile);
            builder.setRootAndRequiredLetter("guardian", 'a');

            Puzzle puzzle = builder.build();

            Method isPangram = Puzzle.class.getDeclaredMethod("isPangram", String.class);
            isPangram.setAccessible(true);
            assertTrue(
                (boolean) isPangram.invoke(puzzle, "guardian"),
                "isPangram should return true if the word is a pangram."
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
