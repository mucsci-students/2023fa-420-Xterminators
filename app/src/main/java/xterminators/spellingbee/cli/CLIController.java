package xterminators.spellingbee.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import com.google.gson.JsonSyntaxException;

import xterminators.spellingbee.model.Puzzle;
import xterminators.spellingbee.model.PuzzleBuilder;
import xterminators.spellingbee.model.Rank;

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

        inputLoop:
        while (scanner.hasNextLine()) {
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

    /**
     * Saves the puzzle to a json file at the given file path.
     * 
     * @param filePath the path to save the puzzle to
     */
    private void save(String filePath) {
        Puzzle puzzle = Puzzle.getInstance();

        if (puzzle == null) {
            view.showErrorMessage(
                "There is no puzzle to save. Please try again."
            );
            return;
        }

        File saveLocation = new File(filePath);

        try {
            puzzle.save(saveLocation);
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
    private void save() {
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

        save(filename.toString());
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
}
