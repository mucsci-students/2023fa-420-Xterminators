package xterminators.spellingbee.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EncryptedPuzzleSaveTest {
    private PuzzleSave puzzleSave;

    @BeforeEach
    public void setUp() {
        puzzleSave = assertDoesNotThrow(
            () -> new EncryptedPuzzleSave(
                new char[] {'g', 'u', 'a', 'r', 'd', 'i', 'n'},
                'a',
                List.of("guard","guardian"),
                20,
                List.of("guard","guardian","raid","rain","raining"),
                29
            ),
            "EncryptedPuzzleSave constructor should not throw an exception"
        );
    }

    @AfterEach
    public void tearDown() {
        puzzleSave = null;
    }

    @Test
    public void testEncryptDecrypt() {
        List<String> validWords = assertDoesNotThrow(
            () -> puzzleSave.validWords(),
            "EncryptedPuzzleSave.validWords() should not throw an exception"
        );

        assertEquals(
            List.of("guard","guardian","raid","rain","raining"),
            validWords,
            "EncryptedPuzzleSave.validWords() should return the correct list" 
            + "of valid words"
        );
    }

    @Test
    public void testBaseWord() {
        char[] baseWord = puzzleSave.baseWord();

        assertArrayEquals(
            new char[] {'g', 'u', 'a', 'r', 'd', 'i', 'n'},
            baseWord,
            "EncryptedPuzzleSave.baseWord() should return the correct base word"
        );
    }

    @Test
    public void testRequiredLetter() {
        char requiredLetter = puzzleSave.requiredLetter();

        assertEquals(
            'a',
            requiredLetter,
            "EncryptedPuzzleSave.requiredLetter() should return the correct"
            + "required letter"
        );
    }

    @Test
    public void testFoundWords() {
        List<String> foundWords = puzzleSave.foundWords();

        assertEquals(
            List.of("guard","guardian"),
            foundWords,
            "EncryptedPuzzleSave.foundWords() should return the correct list"
            + "of found words"
        );
    }

    @Test
    public void testPlayerPoints() {
        int playerPoints = puzzleSave.playerPoints();

        assertEquals(
            20,
            playerPoints,
            "EncryptedPuzzleSave.playerPoints() should return the correct"
            + "player points"
        );
    }

    @Test
    public void testMaxPoints() {
        int maxPoints = puzzleSave.maxPoints();

        assertEquals(
            29,
            maxPoints,
            "EncryptedPuzzleSave.maxPoints() should return the correct max"
            + "points"
        );
    }
}
