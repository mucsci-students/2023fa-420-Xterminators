public class UserFunctions {

    private Puzzle puzzle;

    private List<String> display;

    // public UserFunctions(Puzzle p) {
    //  puzzle = p;
    //  display = new List<>();
    //}

    public UserFunctions() {
        puzzle = new Puzzle();
        display = new List<>();
    }

    private static final String EXIT_COMMAND = "exit";
    private static final String FOUND_COMMAND = "found";
    private static final String HELP_COMMAND = "help";
    private static final String LOAD_COMMAND = "load";
    private static final String NEW_COMMAND = "new";
    private static final String SAVE_COMMAND = "save";
    private static final String SHOW_COMMAND = "show";
    private static final String SHUFFLE_COMMAND = "shuffle";

    /** 
     * Processes the given input and performs the appropriate function.
     *
     * @param input The command being processed.
     * @return If the quit command was given, returns false; otherwise, returns true.
     */
    public bool parseCommand(String input) {
        if (input == null) 
            return true;

        // This is in case we want to allow passing in the seed word for the new puzzle 
        // directly with the "new" command
        String command = input;
        if (input.contains(" "))
        {
            String[] inputs = input.split(" ");
            command = inputs[0];
        }

        switch (command.toLowerCase()) {
            case EXIT_COMMAND:
                //TODO call save puzzle function (is that in Puzzle or UserFunctions?)
                return false;
            case HELP_COMMAND:
                printCommands();
                break;
            case NEW_COMMAND:
                if (inputs.length > 1)
                    createNewPuzzle(inputs[1]);
                else
                    createNewPuzzle("");
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
        }

        return true;
    }

    private void printCommands() {
        String newline = "\n";
        String osName = System.getProperty("os.name");
        // check the OS because Windows is stupid and uses two characters for a newline
        if (osName.startsWith("Windows"))
            newline = "\r\n";

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
        //TODO initialize global _puzzle variable
        //puzzle = new Puzzle();
    }

    private void shuffleLetters() {
        puzzle.shuffle();
        System.out.println("Shuffled outer letters. Display again to see.");
    }

    private void showFoundWords() {
        //TODO show the found word list using Puzzle.getFoundWords()
    }

    private void savePuzzle() {
        //TODO save the current state of the puzzle
    }

    private void loadPuzzle() {
        //TODO load the saved puzzle (assuming there's only one?)
    }

    private void guessWord(String word) {
        //TODO check if the word is in puzzle.validWords
    }

    private void printDisplay() {
        //TODO print the display
        //  maybe store display as a List<String> with each string being a row of the display?
    }

}