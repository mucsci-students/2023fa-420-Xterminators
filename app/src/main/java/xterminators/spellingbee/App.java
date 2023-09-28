/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package xterminators.spellingbee;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        // Case Insensitive check for --cli in args
        if (Arrays.asList(args).stream().anyMatch(s -> s.equalsIgnoreCase("--cli"))) {
            // This is probably bad practice but is the least bad way of
            // leveraging current code.
            // Calls the CLI with no args
            SpellingBeeCLI ui = new SpellingBeeCLI();
            ui.InitUI();
        }
        else {
            GuiController ui = new GuiController();
            ui.InitUI();
        }
    }
}
