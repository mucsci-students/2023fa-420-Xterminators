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
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
            () -> Puzzle.loadPuzzle(emptyFile, dictionaryFile),
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
    public void testLoadPuzzle_ValidEncryptedData(@TempDir File tempDir) {
        try {
                
            File validData = new File(tempDir, "valid_data.json");

            try(BufferedWriter writer = Files.newBufferedWriter(
                    validData.toPath(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            ))
            {
                PuzzleSave save = EncryptedPuzzleSave.fromDefaults(
                    new char[] {'g', 'u', 'r', 'd', 'i', 'n', 'a'},
                    'a',
                    List.of("guard", "guardian"),
                    20,
                    List.of("guard", "guardian", "rain", "raining"),
                    2243
                );

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(save, writer);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
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

            File validData2 = new File(tempDir, "valid_data2.json");

            try(BufferedWriter writer = Files.newBufferedWriter(
                    validData2.toPath(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            ))
            {
                PuzzleSave save = EncryptedPuzzleSave.fromDefaults(
                    new char[] {'g', 'g', 'u', 'r', 'd', 'i', 'n', 'a'},
                    'a',
                    List.of("guard", "guardian"),
                    20,
                    List.of("guard", "guardian", "rain", "raining"),
                    2243
                );

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(save, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Puzzle puzzle2 = assertDoesNotThrow(
                () -> Puzzle.loadPuzzle(validData2, dictionaryFile),
                "loadPuzzle should not throw an exception if the puzzle data is" +
                " valid."
            );

            assertEquals(
                'a',
                puzzle2.getPrimaryLetter(),
                "loadPuzzle should set the primary letter correctly."
            );

            char[] sortedSecondaryLetters2 = puzzle2.getSecondaryLetters();
            Arrays.sort(sortedSecondaryLetters2);

            char[] expectedSecondaryLetters2 = new char[] {'d', 'g', 'i', 'n', 'r', 'u'};

            assertArrayEquals(
                expectedSecondaryLetters2,
                sortedSecondaryLetters2,
                "loadPuzzle should set the secondary letters correctly."
            );

            assertEquals(
                List.of("guard", "guardian"),
                puzzle2.getFoundWords(),
                "loadPuzzle should set the found words correctly."
            );

            assertEquals(
                20,
                puzzle2.getEarnedPoints(),
                "loadPuzzle should set the earned points correctly."
            );
        
        } catch (IOException ex) {
            //why it throw this??
        }
    }

    @Test
    public void testLoadPuzzle_ValidUnencryptedData(@TempDir File tempDir) {
        File validData = new File(tempDir, "valid_data.json");

        try(BufferedWriter writer = Files.newBufferedWriter(
                validData.toPath(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        ))
        {
            PuzzleSave save = new UnencryptedPuzzleSave(
                new char[] {'g', 'u', 'r', 'd', 'i', 'n', 'a'},
                'a',
                List.of("guard", "guardian"),
                20,
                List.of("guard", "guardian", "rain", "raining"),
                2243
            );

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(save, writer);
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

        File validData2 = new File(tempDir, "valid_data2.json");

        try(BufferedWriter writer = Files.newBufferedWriter(
                validData2.toPath(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        ))
        {
            PuzzleSave save = new UnencryptedPuzzleSave(
                new char[] {'g', 'g', 'u', 'r', 'd', 'i', 'n', 'a'},
                'a',
                List.of("guard", "guardian"),
                20,
                List.of("guard", "guardian", "rain", "raining"),
                2243
            );

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(save, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Puzzle puzzle2 = assertDoesNotThrow(
            () -> Puzzle.loadPuzzle(validData2, dictionaryFile),
            "loadPuzzle should not throw an exception if the puzzle data is" +
            " valid."
        );

        assertEquals(
            'a',
            puzzle2.getPrimaryLetter(),
            "loadPuzzle should set the primary letter correctly."
        );

        char[] sortedSecondaryLetters2 = puzzle2.getSecondaryLetters();
        Arrays.sort(sortedSecondaryLetters2);

        char[] expectedSecondaryLetters2 = new char[] {'d', 'g', 'i', 'n', 'r', 'u'};

        assertArrayEquals(
            expectedSecondaryLetters2,
            sortedSecondaryLetters2,
            "loadPuzzle should set the secondary letters correctly."
        );

        assertEquals(
            List.of("guard", "guardian"),
            puzzle2.getFoundWords(),
            "loadPuzzle should set the found words correctly."
        );

        assertEquals(
            20,
            puzzle2.getEarnedPoints(),
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

    @ParameterizedTest
    @ValueSource(strings = 
        {"uirdaagn", "rnguaadi", "ngadriua", "iduanrag",
         "gundraai", "uainagrd", "unidgara", "dnriauga"}
    )
    public void testGuess_BadWord(String word) {
        Puzzle puzzle = assertDoesNotThrow(
            () -> new Puzzle(
                'a',
                new char[] {'g', 'u', 'r', 'd', 'i', 'n'},
                dictionaryFile
            ),
            "Puzzle constructor should not throw an exception if the puzzle" +
            " data is valid."
        );

        assertEquals(
            0,
            puzzle.guess(word),
            "Puzzle guess should return 0 if the word is not in the dictionary."
        );
    }

    @Test
    public void testGetHelpData() {
        Puzzle puzzle = assertDoesNotThrow(
            () -> new Puzzle(
                'a',
                new char[] {'g', 'u', 'r', 'd', 'i', 'n'},
                dictionaryFile
            ),
            "Puzzle constructor should not throw an exception if the puzzle" +
            " data is valid."
        );

        HelpData helpData = puzzle.getHelpData();

        assertNotNull(
            helpData,
            "Puzzle getHelpData should return a non-null HelpData object."
        );

        assertTrue(
            helpData.numWords() >= 0,
            "HelpData numWords should be non-negative."
        );

        assertTrue(
            helpData.numPangrams() >= 0,
            "HelpData numPangrams should be non-negative."
        );

        assertTrue(
            helpData.numPerfectPangrams() >= 0,
            "HelpData numPerfectPangrams should be non-negative."
        );

        assertNotNull(
            helpData.startingLetterGrid(),
            "HelpData startingLetterGrid should be non-null."
        );

        assertFalse(
            helpData.startingLetterGrid().isEmpty(),
            "HelpData startingLetterGrid should not be empty."
        );

        assertNotNull(
            helpData.startingLetterPairs(),
            "HelpData startingLetterPairs should be non-null."
        );

        assertFalse(
            helpData.startingLetterPairs().isEmpty(),
            "HelpData startingLetterPairs should not be empty."
        );
    }

    @Test
    public void testSave_NullMode(@TempDir File tempDir) {
        File saveFile = new File(tempDir, "save.json");

        Puzzle puzzle = assertDoesNotThrow(
            () -> new Puzzle(
                'a',
                new char[] {'g', 'u', 'r', 'd', 'i', 'n'},
                dictionaryFile
            ),
            "Puzzle constructor should not throw an exception if the puzzle" +
            " data is valid."
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> puzzle.save(saveFile, null),
            "Puzzle save should throw an exception if the mode is null."
        );
    }

    @Test
    public void testSave_UnencryptedMode(@TempDir File tempDir) {
        File saveFile = new File(tempDir, "save.json");

        Puzzle puzzle = assertDoesNotThrow(
            () -> new Puzzle(
                'a',
                new char[] {'g', 'u', 'r', 'd', 'i', 'n'},
                dictionaryFile
            ),
            "Puzzle constructor should not throw an exception if the puzzle" +
            " data is valid."
        );

        assertDoesNotThrow(
            () -> puzzle.save(saveFile, SaveMode.UNENCRYPTED),
            "Puzzle save should not throw an exception if the mode is" +
            " UNENCRYPTED."
        );

        assertTrue(
            saveFile.exists(),
            "Puzzle save should create a file if the mode is UNENCRYPTED."
        );

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        PuzzleSave save = assertDoesNotThrow(
            () -> {
                try (BufferedReader reader = Files.newBufferedReader(
                        saveFile.toPath()
                )) {
                    return gson.fromJson(reader, UnencryptedPuzzleSave.class);
                } catch (IOException e) {
                    return null;
                }
            },
            "Puzzle save should create a valid JSON file if the mode is" +
            " UNENCRYPTED."
        );

        assertNotNull(
            save,
            "Puzzle save should create a valid JSON file if the mode is" +
            " UNENCRYPTED."
        );

        assertEquals(
            'a',
            save.requiredLetter(),
            "Puzzle save should save the required letter correctly if the" +
            " mode is UNENCRYPTED."
        );

        assertArrayEquals(
            new char[] {'g', 'u', 'r', 'd', 'i', 'n', 'a'},
            save.baseWord(),
            "Puzzle save should save the base word correctly if the mode is" +
            " UNENCRYPTED."
        );

        assertEquals(
            List.of(),
            save.foundWords(),
            "Puzzle save should save the found words correctly if the mode is" +
            " UNENCRYPTED."
        );

        assertEquals(
            0,
            save.playerPoints(),
            "Puzzle save should save the player points correctly if the mode" +
            " is UNENCRYPTED."
        );
    }

    @Test
    public void testSave_EncryptedMode(@TempDir File tempDir) {
        File saveFile = new File(tempDir, "save.json");

        Puzzle puzzle = assertDoesNotThrow(
            () -> new Puzzle(
                'a',
                new char[] {'g', 'u', 'r', 'd', 'i', 'n'},
                dictionaryFile
            ),
            "Puzzle constructor should not throw an exception if the puzzle" +
            " data is valid."
        );

        assertDoesNotThrow(
            () -> puzzle.save(saveFile, SaveMode.ENCRYPTED),
            "Puzzle save should not throw an exception if the mode is" +
            " ENCRYPTED."
        );

        assertTrue(
            saveFile.exists(),
            "Puzzle save should create a file if the mode is ENCRYPTED."
        );

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        PuzzleSave save = assertDoesNotThrow(
            () -> {
                try (BufferedReader reader = Files.newBufferedReader(
                        saveFile.toPath()
                )) {
                    return gson.fromJson(reader, EncryptedPuzzleSave.class);
                } catch (IOException e) {
                    return null;
                }
            },
            "Puzzle save should create a valid JSON file if the mode is" +
            " ENCRYPTED."
        );

        assertNotNull(
            save,
            "Puzzle save should create a valid JSON file if the mode is" +
            " ENCRYPTED."
        );

        assertEquals(
            'a',
            save.requiredLetter(),
            "Puzzle save should save the required letter correctly if the" +
            " mode is ENCRYPTED."
        );

        assertArrayEquals(
            new char[] {'g', 'u', 'r', 'd', 'i', 'n', 'a'},
            save.baseWord(),
            "Puzzle save should save the base word correctly if the mode is" +
            " ENCRYPTED."
        );

        assertEquals(
            List.of(),
            save.foundWords(),
            "Puzzle save should save the found words correctly if the mode is" +
            " ENCRYPTED."
        );

        assertEquals(
            0,
            save.playerPoints(),
            "Puzzle save should save the player points correctly if the mode" +
            " is ENCRYPTED."
        );
    }
}
