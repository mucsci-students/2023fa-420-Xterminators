package xterminators.spellingbee.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
