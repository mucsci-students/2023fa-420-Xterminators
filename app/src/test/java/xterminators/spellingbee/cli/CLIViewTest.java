package xterminators.spellingbee.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CLIViewTest {
    private CLIView view;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setup() {
        view = new CLIView();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        view = null;
    }

    @Test
    public void testShowFoundWords_Empty() {
        List<String> foundWords = List.of();

        view.showFoundWords(foundWords);

        assertEquals(
            "You have not found any words yet.\n",
            outContent.toString()
        );
    }

    @Test
    public void testShowFoundWords_SingleWord() {
        List<String> foundWords = List.of("offhanded");

        view.showFoundWords(foundWords);

        assertEquals(
            "You have found 1 word:\noffhanded\n",
            outContent.toString()
        );
    }

    @Test
    public void testShowFoundWords_MutipleWords() {
        List<String> foundWords = List.of("offhanded", "offhand", "hand");

        view.showFoundWords(foundWords);

        assertEquals(
            "You have found 3 words:\noffhanded\noffhand\nhand\n",
            outContent.toString()
        );
    }
}
