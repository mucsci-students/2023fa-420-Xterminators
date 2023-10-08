package xterminators.spellingbee.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import xterminators.spellingbee.model.Rank;

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

    @Test
    public void testShowGuess_PreFoundWord() {
        view.showGuess("offhanded", -1);

        assertEquals(
            "You already found the word \"offhanded\". Try again.\n",
            outContent.toString()
        );
    }

    @Test
    public void testShowGuess_InvalidWord() {
        view.showGuess("bad", 0);

        assertEquals(
            "The word \"bad\" is not a word in the puzzle. Try again.\n",
            outContent.toString()
        );
    }

    @Test
    public void testShowGuess_ValidWord() {
        view.showGuess("offhand", 7);

        assertEquals(
            "You found \"offhand\". You earned 7 points.\n",
            outContent.toString()
        );
    }

    @Test
    public void testShowHelp_General() {
        view.showHelp();

        StringBuilder expectedOut = new StringBuilder();
        for (Command command : Command.values()) {
            expectedOut.append(command.getCommand());
            expectedOut.append(": ");
            expectedOut.append(command.getShortHelp());
            expectedOut.append('\n');
        }

        assertEquals(
            expectedOut.toString(),
            outContent.toString()
        );
    }

    @ParameterizedTest
    @EnumSource
    public void testShowHelp(Command command) {
        view.showHelp(command);

        assertEquals(
            command.getLongHelp() + '\n',
            outContent.toString()
        );
    }

    @Test
    public void testShowMessage() {
        view.showMessage("Message to be displayed to user.");

        assertEquals(
            "Message to be displayed to user.\n",
            outContent.toString()
        );
    }

    @Test
    public void testShowErrorMessage() {
        view.showErrorMessage("A bad error that the user needs to see.");

        // Check that the output is the same string but with bold red ansi
        // control chars. The ansi reset char is needed at the end of the string
        // so that the rest of the game is not red and bold.
        assertEquals(
            "\u001B[1m\u001B[31mA bad error that the user needs to see.\u001B[0m\n",
            outContent.toString()
        );
    }

    // TODO: Add test for showPuzzle

    @Test
    public void testShowRanks() {
        view.showRanks(Rank.GOOD, 85, 1000);

        assertEquals(
            """
            Current Rank: Good - 85 points
            
             Beginner   - 0    points minimum
             Good Start - 20   points minimum
             Moving Up  - 50   points minimum
            *Good       - 80   points minimum
             Solid      - 150  points minimum
             Nice       - 250  points minimum
             Great      - 400  points minimum
             Amazing    - 500  points minimum
             Genius     - 700  points minimum
             Queen Bee  - 1000 points minimum
            """,
            outContent.toString()
        );
    }
}
