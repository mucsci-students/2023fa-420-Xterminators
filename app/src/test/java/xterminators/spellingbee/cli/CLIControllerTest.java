package xterminators.spellingbee.cli;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;

public class CLIControllerTest {
    CLIView view;
    CLIController controller;

    @BeforeEach
    public void setup() {
        File dictionaryFile = new File(
            Paths.get("src", "main", "resources", "dictionary_optimized.txt").toString()
        );

        File rootsDictionaryFile = new File(
            Paths.get("src", "main", "resources", "dictionary_roots.txt").toString()
        );

        view = mock(CLIView.class);

        controller = new CLIController(view, dictionaryFile, rootsDictionaryFile);
    }
}
