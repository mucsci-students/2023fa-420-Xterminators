package xterminators.spellingbee.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

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
}
