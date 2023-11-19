package xterminators.spellingbee.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Map;
import com.google.gson.Gson;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.InputEvent;
import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonSyntaxException;

import xterminators.spellingbee.model.HelpData;
import xterminators.spellingbee.model.Puzzle;
import xterminators.spellingbee.model.PuzzleBuilder;
import xterminators.spellingbee.model.Rank;
import xterminators.spellingbee.model.SaveMode;
import xterminators.spellingbee.model.HighScores;
import xterminators.spellingbee.ui.Controller;

/**
 * The controller of the CLI mode of the Spelling Bee game. This class takes
 * input from the user and interacts with the model (Puzzle) accordingly. The
 * controller also interacts with the view (CLIView) to display changes to the
 * user.
 */
public class CLIController extends Controller {
    /** The full dictionary of valid guess words to be used. */
    private File dictionaryFile;
    /** The full dictionary of valid root words to be used. */
    private File rootsDictionaryFile;
    /** The view which displays output and data to the user. */
    private CLIView view;
    /** The helpdata for the puzzle. */
    private HelpData helpData;

    private HighScores highScores;
    
    /**
     * Constructs a new CLIController which connects to the given CLIView, and
     * use the given dictionary files for Puzzle construction.
     * 
     * @param view The view for the controller to output to
     * @param dictionaryFile The full dictionary of valid words to guess
     * @param rootsDictionaryFile The full dictionary of valid root words
     */
    public CLIController(CLIView view, File dictionaryFile, File rootsDictionaryFile) {
        this.view = view;
        this.dictionaryFile = dictionaryFile;
        this.rootsDictionaryFile = rootsDictionaryFile;
        highScores = new HighScores();
    }

    /**
     * Enters the command execution loop for the Spelling Bee game. The
     * controller will read in commands from the user, process them, and sends
     * output to the view to be displayed.
     */
    @Override
    public void run() {
        view.showMessage("Welcome to the Spelling Bee!");
        String introCommands = String.format(
            "Type \"%s\" to create a new puzzle, or \"%s\" to see all commands."
            + " Use \"%s\" followed by words to guess them.",
            Command.NEW.keyword,
            Command.HELP.keyword,
            Command.GUESS.keyword
        );
        view.showMessage(introCommands);

        Scanner scanner = new Scanner(System.in);
        List<String> availableCommands = new ArrayList<>();
        availableCommands.add("exit");
        availableCommands.add("found_words");
        availableCommands.add("guess");
        availableCommands.add("help");
        availableCommands.add("load");
        availableCommands.add("new");
        availableCommands.add("rank");
        availableCommands.add("save");
        availableCommands.add("show");
        availableCommands.add("shuffle");
        availableCommands.add("hint");
        

        inputLoop:
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            String partialCommand = "";
            //in order for tab completion to work, you must end the command with "tab".
            if (input.endsWith("tab")) {
                if(input.endsWith("tab")){
                    partialCommand = input.substring(0, input.length() - 3);
                }
                else {
                    while(input == input.substring(0, input.length() - 1) + " "){
                        input = input.substring(0, input.length() - 1);
                    }
                    partialCommand = input;
                }
                

                String completedCommand = autoCompleteCommand(partialCommand, availableCommands);

                if (completedCommand != null) {
                    System.out.println("Invoking " + completedCommand);
                    input = completedCommand;
                } else {
                    System.out.println("No unique command found for " + partialCommand);
                    continue;
                }
            }
            String[] tokens = input.split(" ");

            Optional<Command> optCommand = Command.fromString(tokens[0]);

            if (optCommand.isEmpty()) {
                view.showErrorMessage(
                    "The command entered is invalid. Please consult \'" +
                    Command.HELP.keyword + "\' for valid commands."
                );
                continue;
            }

            Command curCommand = optCommand.orElseThrow();

            List<String> arguments;

            if (tokens.length > 1) {
                arguments = List.of(Arrays.copyOfRange(tokens, 1, tokens.length));
            } else {
                arguments = Collections.emptyList();
            }

            switch (curCommand) {
                case EXIT -> {
                   break inputLoop;
                }
                case FOUND_WORDS -> {
                    foundWords();
                }
                case GUESS -> {
                    if (arguments.isEmpty()) {
                        view.showErrorMessage(
                            "You must enter a word to guess. Please try again."
                        );
                    } else {
                        arguments.forEach(this::guess);
                    }
                }
                case HELP -> {
                    if (arguments.isEmpty()) {
                        help();
                    } else {
                        help(arguments.get(0));
                    }
                }
                case LOAD -> {
                    if (arguments.isEmpty()) {
                        view.showErrorMessage(
                            "You must include a file to load the save from. " +
                            "Please try again."
                        );
                    } else if (arguments.size() > 1) {
                        view.showErrorMessage(
                            "Too many arguments for load. Please try again."
                        );
                    } else {
                        load(arguments.get(0));
                    }
                }
                case NEW -> {
                    if (arguments.isEmpty()) {
                        newPuzzle();
                    } else if (arguments.size() == 1) {
                        view.showErrorMessage(
                            "Too Few Arguments for New. Please try again."
                        );
                    } else if (arguments.size() > 2) {
                        view.showErrorMessage(
                            "Too many arguments for new puzzle. Please try again."
                        );
                    } else if (arguments.get(0).length() == 1
                               || arguments.get(1).length() != 1)
                    {
                        view.showErrorMessage(
                            "New Arguments are in the wrong order. Please try again."
                        );
                    } else {
                        newPuzzle(arguments.get(0), arguments.get(1).charAt(0));
                    }
                }
                case RANK -> {
                    ranks();
                }
                case SAVE -> {
                    save(arguments);
                }
                case SHOW -> {
                    show();
                }
                case SHUFFLE -> {
                    shuffle();
                }
                case HINT -> {
                    hint();
                }
                case SAVESCORE -> {
                    if (arguments.isEmpty()) {
                        view.showErrorMessage("A name must be provided for the high score.");
                    } else {
                        saveScore(arguments.get(0));
                    }
                }
                case VIEWSCORES -> {
                    viewScores();
                }
            }
        }

        scanner.close();
    }

    /**
     * Get the list of found words from the puzzle and sends it to the view to
     * be displayed.
     */
    private void foundWords() {
        Puzzle puzzle = Puzzle.getInstance();

        if (puzzle == null) {
            view.showErrorMessage(
                "There is no puzzle in progress. Please make or load a puzzle and try again."
            );
            return;
        }

        // The new List is needed for testing, as getFoundWords returns a view
        // of the foundWords list, and not an independent list.
        List<String> foundWords = new ArrayList<>(puzzle.getFoundWords());
        view.showFoundWords(foundWords);
    }

    /**
     * Guesses the given word in the puzzle and sends result to view to be
     * displayed.
     * 
     * @param word The word to be guessed
     */
    private void guess(String word) {
        Puzzle puzzle = Puzzle.getInstance();

        if (puzzle == null) {
            view.showErrorMessage(
                "There is no puzzle in progress. Please make or load a puzzle and try again."
            );
            return;
        }

        int points = puzzle.guess(word);
        view.showGuess(word, points);
    }

    /**
     * Sends command to view to display general command help info.
     */
    private void help() {
        view.showHelp();
    }

    /**
     * Sends command to view to display command help for a specific command
     * @param command The command to display help for
     */
    private void help(String command) {
        Optional<Command> optCommand = Command.fromString(command);

        if (optCommand.isEmpty()) {
            view.showErrorMessage(
                "The command \'" + command + "\' is not a valid command. " +
                "Please try again."
            );
        } else {
            view.showHelp(optCommand.orElseThrow());
        }
    }

    /**
     * Loads the file at the given file path into a puzzle and resumes the puzzle.
     * 
     * @param filePath The path of the save file
     */
    private void load(String filePath){
        File savedFile = new File(filePath);

        try {
            Puzzle puzzle = Puzzle.loadPuzzle(savedFile, dictionaryFile);

            show();
        } catch (FileNotFoundException e) {
            view.showErrorMessage(
                "The file could not be found. Please try again."
            );
        } catch (IOException e) {
            view.showErrorMessage("There was an IO error.");
        } catch (JsonSyntaxException e) {
            view.showErrorMessage(
                "The file was not a valid json representation of a puzzle. " +
                "Please try again."
            );
        }
    }

    /**
     * Creates a new random puzzle with a base word form the dictionary of base
     * words. Sends command to view to display the new puzzle.
     */
    private void newPuzzle() {
        Puzzle puzzle = Puzzle.getInstance();

        try {
            PuzzleBuilder builder = new PuzzleBuilder(dictionaryFile, rootsDictionaryFile);
            puzzle = builder.build();
        } catch (FileNotFoundException e) {
            if (e.getMessage().contains(rootsDictionaryFile.getName())) {
                view.showErrorMessage(
                    "Could not find dictionary of root words. No puzzle created."
                );
            } else if (e.getMessage().contains(dictionaryFile.getName())) {
                view.showErrorMessage(
                    "Could not find dictionary of valid words. No puzzle created."
                );
            }
            return;
        } catch (IOException e) {
            view.showErrorMessage(
                "IO error while creating new puzzle. No puzzle created."
            );
            return;
        }

        view.showPuzzle(
            puzzle.getPrimaryLetter(),
            puzzle.getSecondaryLetters(),
            puzzle.getRank(),
            puzzle.getEarnedPoints()
        );

    
    }

    public void hint(){
        Puzzle puzzle = Puzzle.getInstance();
        helpData = puzzle.getHelpData();

        char[] baseWord = new char[7];
        baseWord[6] = puzzle.getPrimaryLetter();
        char [] nonRequiredLetters = puzzle.getSecondaryLetters();
        for(int i = 0; i < baseWord.length - 1; i++){
            baseWord[i] = nonRequiredLetters[i];
        }
        Map<Pair<Character, Integer>, Long> trying = helpData.startingLetterGrid();
        
        int maxWordSize = 0;
        for(Map.Entry<Pair<Character, Integer>, Long> entry : trying.entrySet()){
            int wordSize = entry.getKey().getRight();
            if(wordSize > maxWordSize) maxWordSize = wordSize;
        }
        //Initialize the matrix
        String[][] grid = new String[9][maxWordSize - 1];
        //Populate the matrix with dashes
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < maxWordSize - 1; col ++){
                grid[row][col] = "-";
            }
        }
        grid[0][0] = " ";
        grid[8][0] = "\u03A3";
        grid[0][maxWordSize - 2] = "\u03A3";
        //Put the puzzle letters into the matrix
        for(int i = 1; i < 8; i++){
            grid[i][0] = "" + baseWord[i - 1];
        }
        //Puts the word length into the matrix
        for(int i = 1; i < maxWordSize - 2; i ++){
            grid[0][i] = (i + 3) + "";
        }
        //This for loop maps the number of words to their size and letters
        //in the grid.
        for(Map.Entry<Pair<Character, Integer>, Long> work : trying.entrySet()){
            for(int k = 1; k < 8; k++){
                if((work.getKey().getLeft()+ "").equals(grid[k][0])){
                    grid[k][work.getKey().getRight() - 3] = (work.getValue() + "");
                }
            }
        }
        // This calculates the sum of the words that start with each letter.
        int sumValLet;
        for(int row = 1; row < 8; row++){
            sumValLet = 0;
            for(int col = 1; col < maxWordSize - 2; col ++){
                if (!grid[row][col].equals("-")){
                    sumValLet = sumValLet + Integer.parseInt(grid[row][col]);
                }
            }
            grid[row][maxWordSize - 2] = sumValLet + "";
        }
        //Calculate the sum of words of each length
        int sumValCol;
        for(int col = 1; col < maxWordSize - 1; col++){
            sumValCol = 0;
            for(int row = 1; row < 8; row ++){
                if (!grid[row][col].equals("-")){
                    sumValCol = sumValCol + Integer.parseInt(grid[row][col]);
                }
            }
            grid[8][col] = sumValCol + "";
        }

        //Initialize the two letter list matrix and populate with '-'
        String [][] twoLetList = new String[7][7];
        for(int i = 0; i < 7; i++){
            for(int k = 0; k < 7; k ++){
                twoLetList[i][k] = "-";
            }
        }

        //Places the starting letter pairs into rows based on starting letter
        for(Map.Entry<String, Long> item : helpData.startingLetterPairs().entrySet()){
            for(int i = 0; i < 7; i++){
                if((baseWord[i] + "").equals(item.getKey().charAt(0) + "")){
                    for(int k = 0; k < 7; k++){
                        if((baseWord[k] + "").equals(item.getKey().charAt(1) + "")){
                            twoLetList[i][k] = item + "";
                        }
                    }
                }
            }
        }

        //Print everything out
        System.out.println("Spelling Bee Grid" + System.lineSeparator());
        System.out.println("Required letter is in" + "\u001B[1m BOLD " 
        + System.lineSeparator());
        System.out.print( (baseWord[6] + " ").toUpperCase() + "\u001B[0m");
        for(int i = 0; i < 6; i++){
            System.out.print((baseWord[i] + "").toUpperCase() + " ");
        }
        System.out.println();
        System.out.println();
        System.out.println("WORDS: " + helpData.numWords() 
        + ", POINTS: " + helpData.totalPoints() + ", PANGRAMS: " +
         helpData.numPangrams() + " (" + helpData.numPerfectPangrams()
          + " Perfect)" + System.lineSeparator());
        //This for loop prints out the matrix
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < maxWordSize - 1; col ++){
                if(row == 0 || col == 0 || row == 8 || col == maxWordSize - 2){
                    System.out.print("\u001B[1m" + String.format("%-" + 3 +"s", 
                    grid[row][col].toUpperCase()) + "\u001B[0m");
                    System.out.print(" ");
                }
                else{
                    System.out.print(String.format("%-" + 3 +"s", 
                    grid[row][col].toUpperCase()));
                    System.out.print(" ");
                }
            }
            System.out.println("");
        }

        //Print out the start of the two letter list section
        System.out.println(System.lineSeparator() + 
        "\u001B[1mTwo letter list: \u001B[0m" 
        + System.lineSeparator());
        
        //Prints out the list of two letter starts.
        for(int i = 0; i < 7; i++){
            for (int k = 0; k < 6; k++){
                if(!twoLetList[i][k].equals("-")){
                    System.out.print(String.format("%-" + 6 + "s", 
                    twoLetList[i][k].toUpperCase()));
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Creates a new puzzle with the given base word. Sends command to view to
     * display the new puzzle.
     * 
     * @param word The base word for the new puzzle
     */
    private void newPuzzle(String word, char requiredLetter) {
        Puzzle puzzle = Puzzle.getInstance();

        try {
            PuzzleBuilder builder = new PuzzleBuilder(dictionaryFile, rootsDictionaryFile);

            if (!builder.setRootAndRequiredLetter(word, requiredLetter)) {
                view.showErrorMessage(
                    "Invalid starting word. Please try again."
                );
                return;
            }

            puzzle = builder.build();
        } catch (FileNotFoundException e) {
            if (e.getMessage().contains(rootsDictionaryFile.getName())) {
                view.showErrorMessage(
                    "Could not find dictionary of root words. No puzzle created."
                );
            } else if (e.getMessage().contains(dictionaryFile.getName())) {
                view.showErrorMessage(
                    "Could not find dictionary of valid words. No puzzle created."
                );
            }
            return;
        } catch (IOException e) {
            view.showErrorMessage(
                "IO error while creating new puzzle. No puzzle created."
            );
            return;
        }

        view.showPuzzle(
            puzzle.getPrimaryLetter(),
            puzzle.getSecondaryLetters(),
            puzzle.getRank(),
            puzzle.getEarnedPoints()
        );
    }

    /**
     * Gets the current rank from the puzzle and sends it to view to be displayed.
     */
    private void ranks() {
        Puzzle puzzle = Puzzle.getInstance();

        if (puzzle == null) {
            view.showErrorMessage(
                "There is no puzzle to show ranks for. Please make or load a puzzle and try again."
            );
            return;
        }

        Rank curRank = puzzle.getRank();
        int earnedPoints = puzzle.getEarnedPoints();
        int totalPoints = puzzle.getTotalPoints();

        view.showRanks(curRank, earnedPoints, totalPoints);
    }

    private void save(List<String> arguments) {
        switch (arguments.size()) {
            case 0 -> save(SaveMode.ENCRYPTED);
            case 1 -> {
                switch (arguments.get(0)) {
                    case "encrypted" -> save(SaveMode.ENCRYPTED);
                    case "unencrypted" -> save(SaveMode.UNENCRYPTED);
                    default -> save(arguments.get(0), SaveMode.ENCRYPTED);
                }
            }
            case 2 -> {
                switch (arguments.get(1)) {
                    case "encrypted"
                        -> save(arguments.get(0), SaveMode.ENCRYPTED);
                    case "unencrypted" 
                        -> save(arguments.get(0), SaveMode.UNENCRYPTED);
                    default -> view.showErrorMessage(
                        "Invalid save mode. Please try again."
                    );
                }
            }
            default -> view.showErrorMessage(
                "Too many arguments for save. Please try again."
            );
        }
    }

    /**
     * Saves the puzzle to a json file at the given file path.
     * 
     * @param filePath the path to save the puzzle to
     */
    private void save(String filePath, SaveMode saveMode) {
        Puzzle puzzle = Puzzle.getInstance();

        if (puzzle == null) {
            view.showErrorMessage(
                "There is no puzzle to save. Please try again."
            );
            return;
        }

        File saveLocation = new File(filePath);

        try {
            puzzle.save(saveLocation, saveMode);
        } catch(IOException e) {
            view.showErrorMessage(
                "The file at " + saveLocation.getAbsolutePath() + " could not " +
                "be created, opened, or written. Please try again."
            );
        }
    }

    /**
     * Saves the puzzle to a json file at a default location.
     */
    private void save(SaveMode saveMode) {
        Puzzle puzzle = Puzzle.getInstance();

        if (puzzle == null) {
            view.showErrorMessage(
                "There is no puzzle to save. Please try again."
            );
            return;
        }

        StringBuilder filename = new StringBuilder();

        //This will create a title for the Json file consisting
        // of the non-required letters followed by the required letter.
        for (char c : puzzle.getSecondaryLetters()){
            filename.append(c);
        }

        filename.append(puzzle.getPrimaryLetter());

        filename.append(".json");

        save(filename.toString(), saveMode);
    }

    /**
     * Sends a command to view to display the puzzle.
     */
    private void show() {
        Puzzle puzzle = Puzzle.getInstance();

        if (puzzle == null) {
            view.showErrorMessage(
                "There is no puzzle to show. Please make or load a puzzle and try again."
            );
            return;
        }

        char primaryLetter = puzzle.getPrimaryLetter();
        char[] secondaryLetters = puzzle.getSecondaryLetters();
        Rank curRank = puzzle.getRank();
        int earnedPoints = puzzle.getEarnedPoints();

        view.showPuzzle(primaryLetter, secondaryLetters, curRank, earnedPoints);
    }

    /**
     * Gets the appropriate newline character for the current OS.
     * 
     * @return The newline character for the current OS.
     */
    private String getNewLineCharacter() {
        String newline = "\n";
        String osName = System.getProperty("os.name");
        // check the OS because Windows is stupid and uses two characters for a newline
        if (osName.startsWith("Windows")) {
            newline = "\r\n";
        }

        return newline;
    }
    
    private String autoCompleteCommand(String partialCommand, List<String> commands){
        String completedCommand = null;

        for (String command : commands) {
            if (command.startsWith(partialCommand)) {
                if (completedCommand == null){
                    completedCommand = command;
                }
            }
        }
        return completedCommand;
    }

    /**
     * Shuffles the letter in the puzzle, then sends a message to view to
     * redisplay the puzzle.
     */
    private void shuffle() {
        Puzzle puzzle = Puzzle.getInstance();

        if (puzzle == null) {
            view.showErrorMessage(
                "There is no puzzle to be shuffled. Please make or load a puzzle and try again."
            );
            return;
        }

        puzzle.shuffle();

        char primaryLetter = puzzle.getPrimaryLetter();
        char[] secondaryLetters = puzzle.getSecondaryLetters();
        Rank curRank = puzzle.getRank();
        int earnedPoints = puzzle.getEarnedPoints();

        view.showPuzzle(primaryLetter, secondaryLetters, curRank, earnedPoints);
    }

    /**
     * Saves the given high score with the given username
     * and then prints the saved high scores.
     * 
     * @param userName The username to save the high score with.
     */
    private void saveScore(String userName) {
        if (userName == null || userName.isEmpty()) {
            view.showErrorMessage("No username provided.");
            return;
        }

        Puzzle p = Puzzle.getInstance();
        int score = p.getEarnedPoints();

        if (!highScores.isHighScore(score)) {
            view.showErrorMessage("Your score is not high enough to be a high score.");
            return;
        }

        highScores.saveScore(userName, score);
        viewScores();
    }

    /**
     * Shows the current high scores.
     */
    private void viewScores() {
        TreeMap<String, Integer> scores = highScores.getScores();
        if (scores == null || scores.size() == 0) {
            view.showErrorMessage("No high scores saved currently.");
        }

        view.showHighScores(scores);
    }
}
