package xterminators.spellingbee.cli;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import xterminators.spellingbee.utils.CharArrayOrderlessMatcher;

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

    @Test
    public void testHelp_General() {
        queueCommand("help");
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view).showHelp();
        verifyNoMoreInteractions(view);
    }

    @ParameterizedTest
    @EnumSource
    public void testHelp(Command command) {
        queueCommand("help " + command.getCommand());
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view).showHelp(command);
        verifyNoMoreInteractions(view);
    }

    @RepeatedTest(5)
    public void testNewRandom() {
        queueCommand("new");
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view).showPuzzle(
            anyChar(),
            any(char[].class),
            eq(Rank.BEGINNER),
            eq(0)
        );
        verifyNoMoreInteractions(view);
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
        verifyNoMoreInteractions(view);
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
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testNew_NotWord() {
        queueCommand("new ofhande o");
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view).showErrorMessage(
            "The word for a new puzzle must be a real word. Please try again."
        );
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testNew_NotPangram() {
        queueCommand("new open o");
        queueCommand("new guardians n");
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view, times(2)).showErrorMessage(
            "The word for the puzzle must be a pangram. Please try again."
        );
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testNew_ValidWord() {
        queueCommand("new offhanded o");
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view).showPuzzle(
            eq('o'),
            argThat(new CharArrayOrderlessMatcher(new char[] {'f', 'h', 'a', 'n', 'd', 'e'})),
            eq(Rank.BEGINNER),
            eq(0)
        );
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testShow_PuzzleExists() {
        queueCommand("new offhanded o");
        queueCommand("show");
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view, times(2)).showPuzzle(
            eq('o'), 
            argThat(new CharArrayOrderlessMatcher(new char[] {'f', 'h', 'a', 'n', 'd', 'e'})),
            eq(Rank.BEGINNER),
            eq(0)
        );
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testShow_NoPuzzle() {
        queueCommand("show");
        queueCommand("exit");
        loadCommands();

        controller.run();

        verify(view).showErrorMessage(
            "There is no puzzle to show. Please make or load a puzzle and try again."
        );
        verifyNoMoreInteractions(view);
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
