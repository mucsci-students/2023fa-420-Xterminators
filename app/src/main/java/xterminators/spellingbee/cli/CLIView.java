package xterminators.spellingbee.cli;

import java.util.List;

import xterminators.spellingbee.model.Rank;

/**
 * The view of the CLI mode of the Spelling Bee game. This class takes
 * instructions and data from the controller and displays output accordingly.
 */
public class CLIView {
    /**
     * Displays the given list of found words for the puzzle.
     * 
     * @param foundWords The list of found words
     */
    public void showFoundWords(List<String> foundWords) {
        // TODO: Implement show found words
    }

    /**
     * Displays the result of guessing the given word, given the points earned.
     * 
     * @param word The word guesses
     * @param points The points earned from the word
     */
    public void showGuess(String word, int points) {
        // TODO: Implement show guess function
    }

    /**
     * Displays the general help for all commands.
     */
    public void showHelp() {
        // TODO: Implement show general help
    }

    /**
     * Displays the full help for a given command.
     * 
     * @param command The command to display help for
     */
    public void showHelp(Command command) {
        // TODO: Implement show help for specific command
    }

    /**
     * Displays the given status message to the user.
     * 
     * @param message The status message to be displayed
     */
    public void showMessage(String message) {
        // TODO: Implement show message
    }

    /**
     * Displays the given error message to the user.
     * 
     * @param message The error message to be displayed
     */
    public void showErrorMessage(String message) {
        // TODO: Implement show error message
    }

    /**
     * Displays the puzzle to the user along with the rank and current points of
     * the puzzle.
     * 
     * @param primaryLetter The required letter of the puzzle
     * @param secondaryLetters The secondary letters of the puzzle
     * @param rank The current rank of the puzzle
     * @param points The current number of earned points
     */
    public void showPuzzle(char primaryLetter, char[] secondaryLetters,
                           Rank rank, int points)
    {
        // TODO: Implement show puzzle
    }

    /**
     * Displays all ranks to the user with the number of points needed to earn
     * each rank. Also highlights the current ranks of the puzzle.
     * 
     * @param rank The current rank of the puzzle
     * @param totalPoints The number of total possible points in the puzzle
     */
    public void showRanks(Rank rank, int totalPoints) {
        // TODO: Implement show ranks
    }
}