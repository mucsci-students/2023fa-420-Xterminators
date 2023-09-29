package xterminators.spellingbee.cli;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import xterminators.spellingbee.model.Rank;

public class CLIControllerTest {
    CLIView view;
    CLIController controller;

    ArrayList<String> queuedCommands;

    @BeforeEach
    public void setup() {
        File dictionaryFile = new File(
            Paths.get("src", "main", "resources", "dictionary_optimized.txt").toString()
        );

        File rootsDictionaryFile = new File(
            Paths.get("src", "main", "resources", "dictionary_roots.txt").toString()
        );

        view = mock(CLIView.class);

        queuedCommands = new ArrayList<>();

        controller = new CLIController(view, dictionaryFile, rootsDictionaryFile);
    }

    @AfterEach
    public void tearDown() {
        queuedCommands = null;
        view = null;
        controller = null;
    }

    @Test
    public void testExit() {
        queueCommand("exit");
        loadCommands();

        controller.run();

        verifyNoInteractions(view);
    }

    @RepeatedTest(5)
    public void testNewRandom() {
        queueCommand("new");
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view).showPuzzle(anyChar(), any(char[].class), eq(Rank.BEGINNER), eq(0));
    }

    @Test
    public void testNew_TooFewArgs() {
        queueCommand("new offhanded");
        queueCommand("new o");
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view, times(2)).showErrorMessage(
            "Too Few Arguments for New. Please try again."
        );
    }

    @Test
    public void testNew_BadOrder() {
        queueCommand("new offhanded o");
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view).showErrorMessage(
            "New Arguments are in the wrong order. Please try again."
        );
    }

    @Test
    public void testNew_NotWord() {
        queueCommand("new ofhande o");
        queueCommand("exit");

        controller.run();

        verify(view).showErrorMessage(
            "The word for a new puzzle must be a real word. Please try again."
        );
    }

    @Test
    public void testNew_NotPangram() {
        queueCommand("new open o");
        queueCommand("new guardians n");
        queueCommand("exit");

        controller.run();

        verify(view, times(2)).showErrorMessage(
            "The word for the puzzle must be a pangram. Please try again."
        );
    }

    @Test
    public void testNew_ValidWord() {
        queueCommand("new offhanded o");
        queueCommand("exit");

        controller.run();

        verify(view).showPuzzle(
            'o',
            new char[] {'f', 'h', 'a', 'n', 'd', 'e'},
            Rank.BEGINNER,
            0
        );
    }

    /**
     * Adds a command string to be queued and executed by the controller. Has no
     * effect if loadCommands is never called.
     * 
     * @param command The command to be added to the buffer
     */
    void queueCommand(String command) {
        queuedCommands.add(command);
    }

    /**
     * Takes all queued commands, places them in a input stream, and sets
     * standard in to be that input stream.
     */
    void loadCommands() {
        StringBuilder commands = new StringBuilder();
        queuedCommands.stream().forEach(s -> commands.append(s).append('\n'));
        ByteArrayInputStream testIn = new ByteArrayInputStream(commands.toString().getBytes());
        System.setIn(testIn);
    }
}
