import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UserFunctions {

    private Puzzle puzzle;

    public UserFunctions() {
        puzzle = null;
    }

    /** The path to the dictionary file for the game. */
    public static final String DICTIONARY_PATH = "src\\dictionary_optimized.txt";
    /** The path to the dictionary of valid starting words. */
    public static final String ROOT_DICTIONARY_PATH = "src\\dictionary_roots.txt";

    /** Command to exit the program. */
    public static final String EXIT_COMMAND = "exit";
    /** Command to print found words. */
    public static final String FOUND_COMMAND = "found";
    /** Command to print commands. */
    public static final String HELP_COMMAND = "help";
    /** Command to load a saved puzzle. */
    public static final String LOAD_COMMAND = "load";
    /** Command to create a new puzzle. */
    public static final String NEW_COMMAND = "new";
    /** Command to save the current puzzle. */
    public static final String SAVE_COMMAND = "save";
    /** Command to reprint the puzzle. */
    public static final String SHOW_COMMAND = "show";
    /** Command to rearrange the puzzle letters on the display. */
    public static final String SHUFFLE_COMMAND = "shuffle";
    /** Command to guess a word. Necessary in case one of the commands is a valid word. */
    public static final String GUESS_COMMAND = "guess";
    /** Command to show the ranks of the puzzle and current rank. */
    public static final String RANK_COMMAND = "rank";

    /** 
     * Processes the given input and performs the appropriate function.
     *
     * @param input The command being processed.
     * @return If the quit command was given, returns false; otherwise, returns true.
     */
    public boolean parseCommand(String input) {
        if (input == null) {
            return true;
        }
        // This is in case we want to allow passing in the seed word for the new puzzle 
        // directly with the "new" command
        String command;
        String[] parameters = null;
        if (input.contains(" ")) {
            String[] inputs = input.split(" ");
            command = inputs[0];
            parameters = Arrays.copyOfRange(inputs, 1, inputs.length);
        } else {
            command = input;
        }

        switch (command.toLowerCase()) {
            case EXIT_COMMAND:
                //TODO call save puzzle function (is that in Puzzle or UserFunctions?)
                return false;
            case HELP_COMMAND:
                printCommands();
                break;
            case NEW_COMMAND:
                if (parameters == null) {
                    createNewPuzzle();
                } else if (parameters.length == 1) {
                    System.out.println(
                        "Not enough arguments for " + NEW_COMMAND + ", the word " +
                        "must be followed by the required letter."
                    );
                } else if (parameters[1].length() != 1) {
                    System.out.println(
                        "The arguments for " + NEW_COMMAND + " must be the word" +
                        "followed by the required letter."
                    );
                } else if (parameters.length > 2) {
                    System.out.println(
                        "Too many arguments for " + NEW_COMMAND + ", the " +
                        "arguments must be the word followed by the required " +
                        "letter."
                    );
                } else {
                    createNewPuzzle(parameters[0], parameters[1].charAt(0));
                }
                break;
            case SHUFFLE_COMMAND:
                shuffleLetters();
                break;
            case FOUND_COMMAND:
                showFoundWords();
                break;
            case SAVE_COMMAND:
                savePuzzle();
                break;
            case LOAD_COMMAND:
                loadPuzzle();
                break;
            case SHOW_COMMAND:
                printPuzzle();
                break;
            case RANK_COMMAND:
                showRanks();
                break;
            case GUESS_COMMAND:
                // loops through all guesses, if no guess is provided, drop 
                // through to guess GUESS_COMMAND
                if (parameters != null && parameters.length > 0) {
                    for (String word : parameters) {
                        guessWord(word);
                        break;
                    }
                    break;
                }
            default:
                guessWord(command);
                break;
        }

        return true;
    }

    private String getNewLineCharacter() {
        String newline = "\n";
        String osName = System.getProperty("os.name");
        // check the OS because Windows is stupid and uses two characters for a newline
        if (osName.startsWith("Windows")) {
            newline = "\r\n";
        }

        return newline;
    }

    private void printCommands() {
        String newline = getNewLineCharacter();

        String help = "Commands " + newline +
        "Exit the game       : " + EXIT_COMMAND + newline + 
        "See found words     : " + FOUND_COMMAND + newline + 
        "Guess a word        : " + GUESS_COMMAND + newline +
        "Print this list     : " + HELP_COMMAND + newline +
        "Load saved puzzle   : " + LOAD_COMMAND + newline + 
        "Create new puzzle   : " + NEW_COMMAND + newline + 
        "Save current puzzle : " + SAVE_COMMAND + newline + 
        "Reprint the puzzle  : " + SHOW_COMMAND + newline +
        "Shuffle letters     : " + SHUFFLE_COMMAND + newline;

        System.out.println(help); 
    }

    private void createNewPuzzle(String seedWord, char requiredLetter) {
        System.out.println("Generating new puzzle ...");

        String newLine = getNewLineCharacter();
        try {
            FileReader dictionaryFile = new FileReader(DICTIONARY_PATH);
            puzzle = Puzzle.fromWord(seedWord, requiredLetter, dictionaryFile);
            printPuzzle();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + newLine + 
                               "Puzzle not generated. Please try again.");
        } catch (FileNotFoundException e) {
            System.out.println("The full dictionary file could not be found." + 
                               "Puzzle not generated.");
        } catch (IOException e) {
            System.out.println(e.getMessage() + newLine + "Puzzle not generated.");
        }
    }

    private void createNewPuzzle() {
        System.out.println("Generating new puzzle ...");

        String newLine = getNewLineCharacter();
        try {
            FileReader dictionaryFile = new FileReader(DICTIONARY_PATH);
            FileReader rootWordsFile = new FileReader(ROOT_DICTIONARY_PATH);
            puzzle = Puzzle.randomPuzzle(rootWordsFile, dictionaryFile);
            printPuzzle();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + newLine + 
                               "Puzzle not generated. Please try again.");
        } catch (FileNotFoundException e) {
            System.out.println("A dictionary file could not be found." + 
                               "Puzzle not generated.");
        } catch (IOException e) {
            System.out.println(e.getMessage() + newLine + "Puzzle not generated.");
        }
    }

    private void shuffleLetters() {
        puzzle.shuffle();
        System.out.println("Shuffled outer letters. Display again to see.");
    }

    private void showFoundWords() {
        if (puzzle == null) {
            return;
        }

        String newline = getNewLineCharacter();
        String output = "";
        for (String word : puzzle.getFoundWords()) {
            output += word + newline;
        }
        
        System.out.println(output);
    }

    private void savePuzzle() {
        //TODO save the current state of the puzzle
    }

    private void loadPuzzle() {
        //TODO load the saved puzzle (assuming there's only one?)
    }

    /*
     * Takes a guess, checks if it's a valid word,
     * and prints the appropriate output.
     * 
     * @param word The word that's being guessed.
     */
    private void guessWord(String word) {
        if (puzzle == null) {
            return;
        }

        Rank prevRank = puzzle.getRank();

        int wasValid = puzzle.guess(word);

        if (wasValid < 0) {
            System.out.println("You already found this word.");
        } else if (wasValid == 0) {
            System.out.println("\"" + word + "\" was not a valid word. Try again!");
        } else {
            System.out.println("Good job! Your word was worth " + wasValid + " points.");
        }

        Rank curRank = puzzle.getRank();
        if (!curRank.equals(prevRank)) {
            System.out.println(
                "You reached a new rank! Your rank is now " +
                curRank.getRankName() + "."
            );
        }
    }

    /*
     * Prints the puzzle in this format with box characters:
     *     +---+    
     * +---| 0 |---+
     * | 5 +---+ 1 |
     * +---| P |---+
     * | 4 +---+ 2 |
     * +---| 3 |---+
     *     +---+    
     * 0-5 are the indexes of the letters in puzzle.getSecondaryLetters(),
     * and P is the primary letter which is required for every word.
     */
    private void printPuzzle() {
        if (puzzle == null) {
            return; 
        }
        char[] letters = puzzle.getSecondaryLetters();
        String newline = getNewLineCharacter();
        String horizontalLine = "\u2500";
        String verticalLine = "\u2502";
        String topLeftCorner = "\u250C";
        String topRightCorner = "\u2510";
        String bottomLeftCorner = "\u2514";
        String bottomRightCorner = "\u2518";
        String leftTJunction = " \u251C";
        String rightTJunction = "\u2524 ";

        String tripleHorizontal = horizontalLine + horizontalLine + horizontalLine;
        String verticalWithSpaces = " " + verticalLine + " ";

        String display = 
        "     " + topLeftCorner + tripleHorizontal + topRightCorner + "     " +
        newline +
        " " + topLeftCorner + tripleHorizontal + rightTJunction + letters[0] + 
        leftTJunction + tripleHorizontal + topRightCorner + " " + newline + 
        verticalWithSpaces + letters[5] + leftTJunction + tripleHorizontal + 
        rightTJunction + letters[1] + verticalWithSpaces + newline +
        leftTJunction + tripleHorizontal + rightTJunction + 
        puzzle.getPrimaryLetter() + leftTJunction + tripleHorizontal + 
        rightTJunction + newline +
        verticalWithSpaces + letters[4] + leftTJunction + tripleHorizontal + 
        rightTJunction + letters[2] + verticalWithSpaces + newline +
        " " + bottomLeftCorner + tripleHorizontal + rightTJunction + 
        letters[3] + leftTJunction + tripleHorizontal + bottomRightCorner + 
        " " + newline + 
        "     " + bottomLeftCorner + tripleHorizontal + bottomRightCorner + 
        "     ";

        System.out.println(display);
        System.out.println(
            newline + "Current Rank: " +
            puzzle.getRank().getRankName() + newline
        );
        System.out.println("Type \"" + GUESS_COMMAND + "\" and a word to guess the word.");
        System.out.println("Type \"" + HELP_COMMAND + "\" to see all commands.");
    }

    private void showRanks() {
        if (puzzle == null) {
            return;
        }

        int totalPoints = puzzle.getTotalPoints();
        Rank curRank = puzzle.getRank();

        System.out.println(
            "Current Rank: " + curRank.getRankName() + " - " +
            puzzle.getEarnedPoints() + " points\n"
        );

        for (Rank rank : Rank.values()) {
            if (rank.equals(curRank)) {
                System.out.println(
                    "*" + rank.getRankName() + " - " +
                    rank.getRequiredPoints(totalPoints) + " points minimum"
                );
            } else {
                System.out.println(
                    " " + rank.getRankName() + " - " +
                    rank.getRequiredPoints(totalPoints) + " points minimum"
                );
            }
        }
    }

}