public class UserFunctions {

    // This needs the Puzzle class
    // private Puzzle puzzle;

    private List<String> display;

    // public UserFunctions(Puzzle p) {
    //  puzzle = p;
    //  display = new List<>();
    //}

    public UserFunctions() {
        //_puzzle = new Puzzle();
        display = new List<>();
    }

    private String exitCommand = "exit";
    private String foundCommand = "found";
    private String helpCommand = "help";
    private String loadCommand = "load";
    private String newCommand = "new";
    private String saveCommand = "save";
    private String showCommand = "show";
    private String shuffleCommand = "shuffle";

    /* 
     * Processes the given input and performs the appropriate function.
     * Parameters:
     *   input: The command being processed.
     * Returns:
     *   If the quit command was given, returns false; otherwise, returns true.
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
            case exitCommand:
                //TODO call save puzzle function (is that in Puzzle or UserFunctions?)
                return false;
            case helpCommand:
                printCommands();
                break;
            case newCommand:
                if (inputs.length > 1)
                    createNewPuzzle(inputs[1]);
                else
                    createNewPuzzle("");
                break;
            case shuffleCommand:
                shuffleLetters();
                break;
            case foundCommand:
                showFoundWords();
                break;
            case saveCommand:
                savePuzzle();
                break;
            case loadCommand:
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
        "Exit the game       : " + exitCommand + newline + 
        "See found words     : " + foundCommand + newline + 
        "Print this list     : " + helpCommand + newline +
        "Load saved puzzle   : " + loadCommand + newline + 
        "Create new puzzle   : " + newCommand + newline + 
        "Save current puzzle : " + saveCommand + newline + 
        "Reprint the puzzle  : " + showCommand + newline +
        "Shuffle letters     : " + shuffleCommand + newline;

        System.out.println(help); 
    }

    private void createNewPuzzle(String seedWord) {
        //TODO initialize global _puzzle variable
        //puzzle = new Puzzle();
    }

    private void shuffleLetters() {
        //TODO shuffle the letters on the display
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