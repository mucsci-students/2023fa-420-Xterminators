/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package xterminators.spellingbee;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import xterminators.spellingbee.cli.CLIController;
import xterminators.spellingbee.cli.CLIFactory;
import xterminators.spellingbee.cli.CLIView;
import xterminators.spellingbee.gui.GuiFactory;
import xterminators.spellingbee.gui.GuiView;
import xterminators.spellingbee.ui.Controller;
import xterminators.spellingbee.ui.UIFactory;

public class App {
    public static void main(String[] args) {
        File dictionaryFile = Paths.get(
            "src",
            "main",
            "resources",
            "dictionaries",
            "dictionary_optimized.txt")
            .toFile();

        File rootsDictionaryFile = Paths.get(
            "src",
            "main",
            "resources",
            "dictionaries",
            "dictionary_roots.txt")
            .toFile();
        
        UIFactory factory = null;
        
        if (Arrays.asList(args).stream().anyMatch(s -> s.equalsIgnoreCase("--cli"))) {
            factory = new CLIFactory(dictionaryFile, rootsDictionaryFile);
        } else {
            factory = new GuiFactory(dictionaryFile, rootsDictionaryFile);
        }

        Controller controller = factory.createController();
        controller.run();
    }
}
