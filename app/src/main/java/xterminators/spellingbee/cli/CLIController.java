package xterminators.spellingbee.cli;

import java.io.File;

import xterminators.spellingbee.model.Puzzle;

/**
 * The controller of the CLI mode of the Spelling Bee game. This class takes
 * input from the user and interacts with the model (Puzzle) accordingly. The
 * controller also interacts with the view (CLIView) to display changes to the
 * user.
 */
public class CLIController {
    /** The full dictionary of valid guess words to be used. */
    private File dictionaryFile;
    /** The full dictionary of valid root words to be used. */
    private File rootsDictionaryFile;
    /** The puzzle with which the controller interacts. */
    private Puzzle puzzle;
    /** The view which displays output and data to the user. */
    private CLIView view;
    
    /**
     * Constructs a new CLIController which connects to the given CLIView, and
     * use the given dictionary files for Puzzle construction.
     * 
     * @param view The view for the controller to output to
     * @param dictionaryFile The full dictionary of valid words to guess
     * @param rootsDictionaryFile The full dictionary of valid root words
     */
    public CLIController(CLIView view, File dictionaryFile, File rootsDictionaryFile) {
        // TODO: Implement View and Dictionaries Constructor
    }

    /**
     * Enters the command execution loop for the Spelling Bee game. The
     * controller will read in commands from the user, process them, and sends
     * output to the view to be displayed.
     */
    public void run() {
        // TODO: Implement Command Read-Execute Loop
    }

    /**
     * Get the list of found words from the puzzle and sends it to the view to
     * be displayed.
     */
    private void foundWords() {
        // TODO: Implement display found words function
    }

    /**
     * Guesses the given word in the puzzle and sends result to view to be
     * displayed.
     * 
     * @param word The word to be guessed
     */
    private void guess(String word) {
        // TODO: Implement guess word function
    }

    /**
     * Sends command to view to display general command help info.
     */
    private void help() {
        // TODO: Implement general help function
    }

    /**
     * Sends command to view to display command help for a specific command
     * @param command The command to display help for
     */
    private void help(String command) {
        // TODO: Implement help function for specific command
    }

    /**
     * Creates a new random puzzle with a base word form the dictionary of base
     * words. Sends command to view to display the new puzzle.
     */
    private void newPuzzle() {
        // TODO: Implement New Random Puzzle
    }

    /**
     * Creates a new puzzle with the given base word. Sends command to view to
     * display the new puzzle.
     * 
     * @param word The base word for the new puzzle
     */
    private void newPuzzle(String word) {
        // TODO: Implement New Puzzle from root
    }

    /**
     * Gets the current rank from the puzzle and sends it to view to be displayed.
     */
    private void ranks() {
        // TODO: Implement Ranks Function
    }

    /**
     * Saves the puzzle to a json file at the given file path.
     * 
     * @param filePath the path to save the puzzle to
     */
    private void save(String filePath) {
        // TODO: Implement Save to given file
    }

    /**
     * Saves the puzzle to a json file at a default location.
     */
    private void save() {
        // TODO: Implement default save
    }

    /**
     * Sends a command to view to display the puzzle.
     */
    private void show() {
        // TODO: Implement show puzzle function
    }

    /**
     * Shuffles the letter in the puzzle, then sends a message to view to
     * redisplay the puzzle.
     */
    private void shuffle() {
        // TODO: Implement shuffle function
    }
}
