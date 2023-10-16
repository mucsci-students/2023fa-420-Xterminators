package xterminators.spellingbee.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import com.google.gson.Gson;

import xterminators.spellingbee.model.Puzzle;
import xterminators.spellingbee.model.Rank;
import xterminators.spellingbee.model.PuzzleSave;

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
        this.view = view;
        this.dictionaryFile = dictionaryFile;
        this.rootsDictionaryFile = rootsDictionaryFile;
    }

    /**
     * Enters the command execution loop for the Spelling Bee game. The
     * controller will read in commands from the user, process them, and sends
     * output to the view to be displayed.
     */
    public void run() {
        System.out.println("Welcome to the Spelling Bee!");
        System.out.printf(
            "Type \"%s\" to create a new puzzle, or \"%s\" to see all commands.\n",
            Command.NEW.getCommand(),
            Command.HELP.getCommand()
        );

        Scanner scanner = new Scanner(System.in);

        boolean exitFlag = false;

        while (scanner.hasNextLine() && !exitFlag) {
            String input = scanner.nextLine();

            String[] tokens = input.split(" ");

            Optional<Command> optCommand = Command.fromString(tokens[0]);

            if (optCommand.isEmpty()) {
                view.showErrorMessage(
                    "The command entered is invalid. Please consult \'" +
                    Command.HELP.getCommand() + "\' for valid commands."
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
                    exitFlag = true;
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
                    if (arguments.isEmpty()) {
                        save();
                    } else if (arguments.size() > 1) {
                        view.showErrorMessage(
                            "Too many arguments for save. Please try again."
                        );
                    } else {
                        save(arguments.get(0));
                    }
                }
                case SHOW -> {
                    show();
                }
                case SHUFFLE -> {
                    shuffle();
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
        
        Gson load = new Gson();
        String jsonPuzzle = "";

        try{
            //Create a file object to read the contents of loadFile
            File myObj = new File (filePath);
            Scanner fileReader = new Scanner (myObj);
            
            //Construct a string by reading the file line by line
            while (fileReader.hasNextLine()){
                jsonPuzzle = jsonPuzzle + fileReader.nextLine();
                
            }
            fileReader.close();
            //Construct a new puzzle based on the loaded file
            PuzzleSave loading = load.fromJson (jsonPuzzle, PuzzleSave.class);

            //Initialize the values that will be used by PuzzleSave
            char[] BaseWord = loading.getPSBase();
            char RequiredLetter = BaseWord[6];

            char[] newSecondaryLetters = new char[6];
            for(int i = 0; i < newSecondaryLetters.length; i ++){
                newSecondaryLetters[i] = BaseWord[i];
            }

            FileReader dictionary = new FileReader(dictionaryFile);

            ArrayList<String> newFoundWords = new ArrayList<String>();

            for(String word : loading.getPSFoundWords() ) {
                newFoundWords.add(word);
            }

            String work = "";
            for ( char c : BaseWord) {
                work = work + c;
            }

            Puzzle LoadedPuzzle = new Puzzle(RequiredLetter, newSecondaryLetters, dictionary, loading.getPSPoints(), loading.getPSMaxPoints(), newFoundWords);

            puzzle = LoadedPuzzle;
            
            //this.puzzle = LoadedPuzzle;

            show();
        }
        catch (FileNotFoundException e) {
            System.out.println("The file cannot be found.");
        }
        catch (IOException e) {
            System.out.println("There was an I/O error");
        }
    }

    /**
     * Creates a new random puzzle with a base word form the dictionary of base
     * words. Sends command to view to display the new puzzle.
     */
    private void newPuzzle() {
        try {
            FileReader rootWordsReader = new FileReader(rootsDictionaryFile);
            FileReader dictionaryReader = new FileReader(dictionaryFile);

            puzzle = Puzzle.randomPuzzle(rootWordsReader, dictionaryReader);
            
            rootWordsReader.close();
            dictionaryReader.close();
        } catch (FileNotFoundException e) {
            if (e.getMessage().contains(rootsDictionaryFile.getName())) {
                view.showErrorMessage(
                    "Could not find dictionary of root words. No puzzle created."
                );
            } else if (e.getMessage().contains(dictionaryFile.getName())) {
                view.showErrorMessage(
                    "Could not find dictionary of valid words. No puzzle created."
                );
            } else {
                view.showErrorMessage(
                    "Unknown FileNotFoundException thrown. No puzzle created.\n" +
                    e.getLocalizedMessage()
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
     * Creates a new puzzle with the given base word. Sends command to view to
     * display the new puzzle.
     * 
     * @param word The base word for the new puzzle
     */
    private void newPuzzle(String word, char requiredLetter) {
        try {
            FileReader rootWordsReader = new FileReader(rootsDictionaryFile);
            FileReader dictionaryReader = new FileReader(dictionaryFile);

            puzzle = Puzzle.fromWord(
                word,
                requiredLetter,
                rootWordsReader,
                dictionaryReader,
                false
            );
        } catch (FileNotFoundException e) {
            if (e.getMessage().contains(rootsDictionaryFile.getName())) {
                view.showErrorMessage(
                    "Could not find dictionary of root words. No puzzle created."
                );
            } else if (e.getMessage().contains(dictionaryFile.getName())) {
                view.showErrorMessage(
                    "Could not find dictionary of valid words. No puzzle created."
                );
            } else {
                view.showErrorMessage(
                    "Unknown FileNotFoundException thrown. No puzzle created.\n" +
                    e.getLocalizedMessage()
                );
            }

            return;
        } catch (IllegalArgumentException e) {
            view.showErrorMessage(
                "Invalid starting word. Please try again."
            );
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

    /**
     * Saves the puzzle to a json file at the given file path.
     * 
     * @param filePath the path to save the puzzle to
     */
    private void save(String filePath) {
        //Create an object of the Gson class
        Gson saved = new Gson();

        char[] nonRequiredLetters = puzzle.getSecondaryLetters();

        char[] baseWord = new char[7];
        baseWord[6] = puzzle.getPrimaryLetter();
        for(int i = 0; i < baseWord.length - 1; i++){
            baseWord[i] = nonRequiredLetters[i];
        }

        try{

            // Take the necessary attributes and create a puzzleSave object,
            PuzzleSave savedPuzzle = PuzzleSave.ToSave(baseWord, puzzle.getFoundWords(),
            puzzle.getEarnedPoints(), puzzle.getPrimaryLetter(), puzzle.getTotalPoints());

            //Converts the current puzzle object to Json
            String savedJson = saved.toJson (savedPuzzle);

            //Create the file and populate it with the saved Json
        
            String pathName = filePath + ".json";
            File savedFile = new File(pathName);
            //Returns true if a new file is created.
                if(savedFile.createNewFile()){

                    //Create a file writer to populate the created File
                    FileWriter writing = new FileWriter(savedFile);
                    //Insert input
                    writing.write (savedJson);
                    //Close the writer
                    writing.close ();
                    //Notify the user
                    System.out.println("File created: " + pathName);
                }
            
        } catch (IOException e) {
            System.out.println("An error occurred");
            }
        
    }

    /**
     * Saves the puzzle to a json file at a default location.
     */
    private void save() {
        //Create an object of the Gson class
        Gson saved = new Gson();

        char[] nonRequiredLetters = puzzle.getSecondaryLetters();

        char[] baseWord = new char[7];
        baseWord[6] = puzzle.getPrimaryLetter();
        for(int i = 0; i < baseWord.length - 1; i++){
            baseWord[i] = nonRequiredLetters[i];
        }

        String filename = "";

        //This will create a title for the Json file consisting
        // of the non-required letters followed by the required letter.
        for (char c : baseWord){
            filename = filename + c;
        }

        

        //Create the file and populate it with the saved Json
        try{
            // Take the necessary attributes and create a puzzleSave object,
            PuzzleSave savedPuzzle = PuzzleSave.ToSave(baseWord, puzzle.getFoundWords(), puzzle.getEarnedPoints(), puzzle.getPrimaryLetter(), puzzle.getTotalPoints());

            //Converts the current puzzle object to Json
            String savedJson = saved.toJson (savedPuzzle);

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
     * Sends a command to view to display the puzzle.
     */
    private void show() {
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

    /**
     * Shuffles the letter in the puzzle, then sends a message to view to
     * redisplay the puzzle.
     */
    private void shuffle() {
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
}
