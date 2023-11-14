package xterminators.spellingbee.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class EncryptedPuzzleSaveTest {
    @Test
    public void testEncryptDecrypt() {
        PuzzleSave puzzleSave = assertDoesNotThrow(
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
}
