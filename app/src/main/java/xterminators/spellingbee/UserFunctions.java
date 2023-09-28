package xterminators.spellingbee;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;

public class UserFunctions {

    private Puzzle puzzle;

    public UserFunctions() {
        puzzle = null;
    }

    /** The path to the dictionary file for the game. */
    public static final String DICTIONARY_PATH
        = Paths.get("src", "main", "resources", "dictionary_optimized.txt").toString();
    /** The path to the dictionary of valid starting words. */
    public static final String ROOT_DICTIONARY_PATH
        = Paths.get("src", "main", "resources", "dictionary_roots.txt").toString();

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
        // Do input.trim() in case user put a space at the end like a noob
        if (input.trim().contains(" ")) {
            String[] inputs = input.split(" ");
            command = inputs[0];
            parameters = Arrays.copyOfRange(inputs, 1, inputs.length);
        } else {
            command = input.trim();
        }

        switch (command.toLowerCase()) {
            case EXIT_COMMAND:
                //TODO call save puzzle function (is that in Puzzle or UserFunctions?)
                return false;
            case HELP_COMMAND:
                printCommands();
                break;
            case NEW_COMMAND:
                System.out.println(System.getProperty("user.dir"));
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
                        "Too many arguments for " + NEW_COMMAND + ". The " +
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
                if (parameters.length != 1){
                    System.out.println("In order to load, a file must be specified.");
                } else {
                    loadPuzzle(parameters[0]);
                }
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
                    }
                    break;
                }
            default:
                guessWord(command);
                break;
        }

        return true;
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

    /**
     * Prints all commands in alphabetical order.
     */
    private void printCommands() {
        String newline = getNewLineCharacter();

        String help = "Commands " + newline +
        "Exit the game       : " + EXIT_COMMAND + newline + 
        "See found words     : " + FOUND_COMMAND + newline + 
        "Guess a word        : " + GUESS_COMMAND + newline +
        "Print this list     : " + HELP_COMMAND + newline +
        "Load saved puzzle   : " + LOAD_COMMAND + newline + 
        "Create new puzzle   : " + NEW_COMMAND + newline + 
        "Print current rank  : " + RANK_COMMAND + newline +
        "Save current puzzle : " + SAVE_COMMAND + newline + 
        "Reprint the puzzle  : " + SHOW_COMMAND + newline +
        "Shuffle letters     : " + SHUFFLE_COMMAND + newline;

        System.out.println(help); 
    }

    /**
     * Creates a new puzzle using the given word and required letter,
     * and then prints it. If no seed word is given, a random puzzle
     * will be generated instead.
     * 
     * @param seedWord The pangram to use for the letters in the puzzle.
     *  If seedWord is empty, a random puzzle will be generated instead.
     * @param requiredLetter The letter that should be required.
     */
    private void createNewPuzzle(String seedWord, char requiredLetter) {
        System.out.println("Generating new puzzle ...");

        String newLine = getNewLineCharacter();
        try {
            FileReader dictionaryFile = new FileReader(DICTIONARY_PATH);
            FileReader rootWordsFile = new FileReader(ROOT_DICTIONARY_PATH);
            if (seedWord != null && !seedWord.equals("")) {
                puzzle = Puzzle.fromWord(seedWord, requiredLetter, 
                    rootWordsFile, dictionaryFile, false);
            } else {
                // no seedWord provided, assume random puzzle
                puzzle = Puzzle.randomPuzzle(rootWordsFile, dictionaryFile);
            }
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

    /**
     * Creates a new puzzle using a random word from the dictionary.
     * The random word will have exactly 7 unique letters but may be 
     * longer than 7 characters.
     */
    private void createNewPuzzle() {
        // Send empty word to create a random puzzle
        // If seedWord is empty requiredLetter is irrelevant
        createNewPuzzle("", 'a');
    }

    /**
     * Shuffles the letters in the display.
     */
    private void shuffleLetters() {
        puzzle.shuffle();
        printPuzzle();
    }

    /**
     * Prints the list of found words.
     */
    private void showFoundWords() {
        if (puzzle == null) {
            return;
        }

        String newline = getNewLineCharacter();
        StringBuilder output = new StringBuilder();
        for (String word : puzzle.getFoundWords()) {
            output.append(word + newline);
        }
        System.out.println("Found Words:");
        System.out.println(output.toString());
    }



    /**
     * Saves the puzzle to a JSON format.
     */
    private void savePuzzle() {
        //Create an object of the Gson class
        Gson saved = new Gson();

        String filename = "";

        //This will mcreate a title for the Json file consisting
        // of the primary letter followed by the secondary letters.
        filename = filename + puzzle.getPrimaryLetter();
        for (char c : puzzle.getSecondaryLetters()){
            filename = filename + c;
        }

        //Converts the current puzzle object to Json
        String savedJson = saved.toJson (puzzle);

        //Create the file and populate it with the saved Json
        try{
            File savedFile = new File(filename + ".json");

            //Returns true if a new file is created.
            if(savedFile.createNewFile ()){

                //Create a file writer to populate the created File
                FileWriter writing = new FileWriter(savedFile);
                //Insert input
                writing.write (savedJson);
                //Close the writer
                writing.close ();
                //Notify the user
                System.out.println("File created: " + filename + ".json");
            } else {
                System.out.println("A file by that name already exists." + getNewLineCharacter() + "Overwriting the file");

                //Open a writer to replace the information in the file.
                PrintWriter writer = new PrintWriter(savedFile);
                writer.print(savedJson);
                writer.close();
            }
        }
        catch (IOException e) {
            System.out.println("An error occurred");
        }
        
    }

    /**
     * Loads a saved puzzle from a JSON format.
     */

    private void loadPuzzle(String loadFile) {
        
        Gson load = new Gson();
        String jsonPuzzle = "";

        try{
            //Create a file object to read the contents of loadFile
            File myObj = new File (loadFile);
            Scanner fileReader = new Scanner (myObj);
            
            //Construct a string by reading the file line by line
            while (fileReader.hasNextLine()){
                jsonPuzzle = jsonPuzzle + fileReader.nextLine();
                
            }
            fileReader.close();
            //Construct a new puzzle based on the loaded file
            this.puzzle = load.fromJson (jsonPuzzle, Puzzle.class);
            printPuzzle();
        } catch (FileNotFoundException e) {
            System.out.println("The puzzle file could not be found.");
        }
    }

    /**
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
            String plural = "s";
            if (wasValid == 1) {
                plural = "";
            }
            System.out.println("Good job! Your word was worth " + wasValid + 
            " point" + plural + ".");
        }

        Rank curRank = puzzle.getRank();
        if (!curRank.equals(prevRank)) {
            System.out.println(
                "You reached a new rank! Your rank is now " +
                curRank.getRankName() + "."
            );
        }
    }

    /**
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
        String vertLine = " \u2502 ";
        String leftT = " \u251C";
        String rightT = "\u2524 ";

        String mainLine = horizontalLine + horizontalLine + horizontalLine;

        // This is still super messy, I'm not quite sure how to fix it
        // It works though
        String display = 
        //Line 1
        "     \u250C" + mainLine + "\u2510     " + newline +
        //Line 2
        " \u250C" + mainLine + rightT + letters[0] + leftT + mainLine +
        "\u2510 " + newline + 
        //Line 3
        vertLine + letters[5] + leftT + mainLine + rightT + letters[1] + 
        vertLine + newline +
        //Line 4
        leftT + mainLine + rightT + puzzle.getPrimaryLetter() + leftT + 
        mainLine + rightT + newline +
        //Line 5
        vertLine + letters[4] + leftT + mainLine + rightT + letters[2] + 
        vertLine + newline +
        //Line 6
        " \u2514" + mainLine + rightT + letters[3] + leftT + mainLine + 
        "\u2518 " + newline + 
        //Line 7
        "     \u2514" + mainLine + "\u2518     ";

        System.out.println(display);
        System.out.println(
            newline + "Current Rank  : " +
            puzzle.getRank().getRankName()
        );
        System.out.println("Current Points: " + 
            puzzle.getEarnedPoints() + newline);
        System.out.println("Type \"" + GUESS_COMMAND + "\" and a word to guess the word.");
        System.out.println("Type \"" + HELP_COMMAND + "\" to see all commands.");
    }

    /**
     * Prints all available ranks, along with the current rank.
     */
    private void showRanks() {
        if (puzzle == null) {
            return;
        }

        int totalPoints = puzzle.getTotalPoints();
        int earnedPoints = puzzle.getEarnedPoints();
        Rank curRank = puzzle.getRank();

        if (earnedPoints == 1) {
            System.out.println(
                "Current Rank: " + curRank.getRankName() + " - " +
                earnedPoints + " point\n"
            );
        } else {
            System.out.println(
                "Current Rank: " + curRank.getRankName() + " - " +
                earnedPoints + " points\n"
            );
        }

        int namePadWidth = 0;
        int pointPadWidth = 0;
        for (Rank rank : Rank.values()) {
            int reqPoints = rank.getRequiredPoints(totalPoints);
            if (String.valueOf(reqPoints).length() > pointPadWidth) {
                pointPadWidth = String.valueOf(reqPoints).length();
            }
            if (rank.getRankName().length() > namePadWidth) {
                namePadWidth = rank.getRankName().length();
            }
            
        }

        for (Rank rank : Rank.values()) {
            int reqPoints = rank.getRequiredPoints(totalPoints);
            String reqPointsStr = String.valueOf(reqPoints);

            if (reqPoints == 1) {
                if (rank.equals(curRank)) {
                    System.out.println(
                        "*" + padLeft(rank.getRankName(), namePadWidth) + " - " +
                        padLeft(reqPointsStr, pointPadWidth) + " point minimum"
                    );
                } else {
                    System.out.println(
                        " " + padLeft(rank.getRankName(), namePadWidth) + " - " +
                        padLeft(reqPointsStr, pointPadWidth) + " point minimum"
                    );
                }
            } else {
                if (rank.equals(curRank)) {
                    System.out.println(
                        "*" + padLeft(rank.getRankName(), namePadWidth) + " - " +
                        padLeft(reqPointsStr, pointPadWidth) + " points minimum"
                    );
                } else {
                    System.out.println(
                        " " + padLeft(rank.getRankName(), namePadWidth) + " - " +
                        padLeft(reqPointsStr, pointPadWidth) + " points minimum"
                    );
                }
            }
        }
    }

    /**
     * Pads the given string on the right with spaces and returns the result.
     * If the given string is null, it will be returned unchanged.
     * @param str The string to pad.
     * @param totalLength The total length that the resulting string should be.
     * @return The padded string.
     */
    private String padLeft(String str, int totalLength) {
        if (str == null) {
            return str;
        }
        return String.format("%1$-" + totalLength + "s", str);
    }
}
