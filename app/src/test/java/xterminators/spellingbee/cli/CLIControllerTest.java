package xterminators.spellingbee.cli;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
