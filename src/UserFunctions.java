import java.util.List;

public class UserFunctions {

    private Puzzle puzzle;

    public UserFunctions() {
        puzzle = null;
    }

    /** Command to exit the program. */
    private static final String EXIT_COMMAND = "exit";
    /** Command to print found words. */
    private static final String FOUND_COMMAND = "found";
    /** Command to print commands. */
    private static final String HELP_COMMAND = "help";
    /** Command to load a saved puzzle. */
    private static final String LOAD_COMMAND = "load";
    /** Command to create a new puzzle. */
    private static final String NEW_COMMAND = "new";
    /** Command to save the current puzzle. */
    private static final String SAVE_COMMAND = "save";
    /** Command to reprint the puzzle. */
    private static final String SHOW_COMMAND = "show";
    /** Command to rearrange the puzzle letters on the display. */
    private static final String SHUFFLE_COMMAND = "shuffle";

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
        String command = input;
        String parameter = "";
        if (input.contains(" ")) {
            String[] inputs = input.split(" ");
            command = inputs[0];
            parameter = inputs[1];
        }

        switch (command.toLowerCase()) {
            case EXIT_COMMAND:
                //TODO call save puzzle function (is that in Puzzle or UserFunctions?)
                return false;
            case HELP_COMMAND:
                printCommands();
                break;
            case NEW_COMMAND:
                if (parameter == null) {
                    parameter = "";
                }
                createNewPuzzle(parameter);
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
        "Print this list     : " + HELP_COMMAND + newline +
        "Load saved puzzle   : " + LOAD_COMMAND + newline + 
        "Create new puzzle   : " + NEW_COMMAND + newline + 
        "Save current puzzle : " + SAVE_COMMAND + newline + 
        "Reprint the puzzle  : " + SHOW_COMMAND + newline +
        "Shuffle letters     : " + SHUFFLE_COMMAND + newline;

        System.out.println(help); 
    }

    private void createNewPuzzle(String seedWord) {
        puzzle = new Puzzle(seedWord);
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

    private void guessWord(String word) {
        if (puzzle == null) {
            return;
        }

        int wasValid = puzzle.guess(word);

        if (wasValid < 0) {
            System.out.println("You already found this word.");
        } else if (wasValid == 0) {
            System.out.println("Your guess was not a valid word. Try again!");
        } else {
            System.out.println("Good job! Your word was worth " + wasValid + " points.");
        }
    }

    private void printDisplay() {
        //TODO print the display
        //  maybe store display as a List<String> with each string being a row of the display?
        /* 
         * Display options
         *    A
         *  A   A
         *    A
         *  A   A
         *    A
         *      +---+
         *  +---| A |---+
         *  | A +---+ A |
         *  +---| A |---+
         *  | A +---+ A |
         *  +---| A |---+
         *      +---+
         *      ┌───┐     
         *  ┌───┤ A ├───┐
         *  │ A ├───┤ A │
         *  ├───┤ A ├───┤
         *  │ A ├───┤ A │ 
         *  └───┤ A ├───┘
         *      └───┘     
         *              
         */
    }

}