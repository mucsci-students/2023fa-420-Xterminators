/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package xterminators.spellingbee;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import xterminators.spellingbee.cli.CLIController;
import xterminators.spellingbee.cli.CLIView;
import xterminators.spellingbee.gui.GuiController;

public class App {
    public static void main(String[] args) throws IOException {
        // Case Insensitive check for --cli in args
        if (Arrays.asList(args).stream().anyMatch(s -> s.equalsIgnoreCase("--cli"))) {
            File dictionaryFile = new File(
                Paths.get("src", "main", "resources", "dictionary_optimized.txt").toString()
            );

            File rootsDictionaryFile = new File(
                Paths.get("src", "main", "resources", "dictionary_roots.txt").toString()
            );

            CLIView cliView = new CLIView();
            CLIController cliController = new CLIController(cliView, dictionaryFile, rootsDictionaryFile);

            cliController.run();
        } else {
            GuiController ui = new GuiController();
            ui.InitUI();
        }
    }
}
