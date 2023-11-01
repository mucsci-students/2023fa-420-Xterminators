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
            "You have not found any words yet." + System.lineSeparator(),
            outContent.toString()
        );
    }

    @Test
    public void testShowFoundWords_SingleWord() {
        List<String> foundWords = List.of("offhanded");

        view.showFoundWords(foundWords);

        assertEquals(
            "You have found 1 word:" + System.lineSeparator() +
            "offhanded"              + System.lineSeparator(),
            outContent.toString()
        );
    }

    @Test
    public void testShowFoundWords_MutipleWords() {
        List<String> foundWords = List.of("offhanded", "offhand", "hand");

        view.showFoundWords(foundWords);

        assertEquals(
            "You have found 3 words:" + System.lineSeparator() +
            "offhanded"               + System.lineSeparator() +
            "offhand"                 + System.lineSeparator() +
            "hand"                    + System.lineSeparator(),
            outContent.toString()
        );
    }

    @Test
    public void testShowGuess_PreFoundWord() {
        view.showGuess("offhanded", -1);

        assertEquals(
            "You already found the word \"offhanded\". Try again."
                + System.lineSeparator(),
            outContent.toString()
        );
    }

    @Test
    public void testShowGuess_InvalidWord() {
        view.showGuess("bad", 0);

        assertEquals(
            "The word \"bad\" is not a word in the puzzle. Try again."
                + System.lineSeparator(),
            outContent.toString()
        );
    }

    @Test
    public void testShowGuess_ValidWord() {
        view.showGuess("offhand", 7);

        assertEquals(
            "You found \"offhand\". You earned 7 points."
                + System.lineSeparator(),
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
            expectedOut.append(System.lineSeparator());
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
            command.getLongHelp() + System.lineSeparator(),
            outContent.toString()
        );
    }

    @Test
    public void testShowMessage() {
        view.showMessage("Message to be displayed to user.");

        assertEquals(
            "Message to be displayed to user." + System.lineSeparator(),
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
            "\u001B[1m\u001B[31mA bad error that the user needs to see.\u001B[0m"
                + System.lineSeparator(),
            outContent.toString()
        );
    }

    @Test
    public void testShowPuzzle() {
        view.showPuzzle(
            'o',
            new char[] {'f', 'h', 'a', 'n', 'd', 'e'},
            Rank.BEGINNER,
            0
        );

        assertEquals(
            "    +---+"                + System.lineSeparator() +
            "+---+ f +---+"            + System.lineSeparator() +
            "| e +---+ h |"            + System.lineSeparator() +
            "+---+ o +---+"            + System.lineSeparator() +
            "| d +---+ a |"            + System.lineSeparator() +
            "+---+ n +---+"            + System.lineSeparator() +
            "    +---+"                + System.lineSeparator() +
                                         System.lineSeparator() +            
            "Current Rank  : Beginner" + System.lineSeparator() +
            "Current Points: 0"        + System.lineSeparator(),
            outContent.toString()
        );
    }

    @Test
    public void testShowRanks() {
        view.showRanks(Rank.GOOD, 85, 1000);

        assertEquals(
            "Current Rank: Good - 85 points"    + System.lineSeparator() +
                                                  System.lineSeparator() +
            " Beginner   - 0    points minimum" + System.lineSeparator() +
            " Good Start - 20   points minimum" + System.lineSeparator() +
            " Moving Up  - 50   points minimum" + System.lineSeparator() +
            "*Good       - 80   points minimum" + System.lineSeparator() +
            " Solid      - 150  points minimum" + System.lineSeparator() +
            " Nice       - 250  points minimum" + System.lineSeparator() +
            " Great      - 400  points minimum" + System.lineSeparator() +
            " Amazing    - 500  points minimum" + System.lineSeparator() +
            " Genius     - 700  points minimum" + System.lineSeparator() +
            " Queen Bee  - 1000 points minimum" + System.lineSeparator(),
            outContent.toString()
        );
    }
}
