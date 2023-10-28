package xterminators.spellingbee.gui;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;

import com.google.gson.JsonSyntaxException;

import xterminators.spellingbee.model.Puzzle;
import xterminators.spellingbee.model.PuzzleBuilder;
import xterminators.spellingbee.model.Rank;

public class GuiController {

    /** The puzzle object. */
    private Puzzle puzzle;
    /** The view that the user interacts with. */
    private GuiView guiView;
    /** The file pointing to the full dictionary of usable words. */
    private File dictionaryFile;
    /** The file pointing to the dictionary of valid root words. */
    private File rootsDictionaryFile;

    public GuiController(GuiView guic, File dictionaryFile, File rootsDictionaryFile) {
        this.puzzle = null;
        this.guiView = guic;
        this.dictionaryFile = dictionaryFile;
        this.rootsDictionaryFile = rootsDictionaryFile;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    /**
     * Creates a new puzzle using the given word and required letter,
     * and then prints it. If no seed word is given, a random puzzle
     * will be generated instead.
     * 
     * @param seedWord The pangram to use for the letters in the puzzle.
     *  If seedWord is empty, a random puzzle will be generated instead.
     * @param requiredLetter The letter that should be required.
     * @throws IllegalArgumentException if the starting word was not valid.
     * @throws FileNotFoundException if the dictionary file could not be found.
     * @throws IOException if there was a problem reading the dictionary file.
     */
    public void createNewPuzzle(String seedWord, char requiredLetter) 
        throws IllegalArgumentException, FileNotFoundException, IOException  {

        try {
            PuzzleBuilder builder = new PuzzleBuilder(
                dictionaryFile,
                rootsDictionaryFile
            );

            // If seedWord is empty, a random puzzle will be generated instead.
            if (seedWord.equals("")) {
                puzzle = builder.build();
                return;
            }

            if (seedWord.length() < Puzzle.NUMBER_UNIQUE_LETTERS) {
                throw new IllegalArgumentException(
                    "The seed word must be at least " + Puzzle.NUMBER_UNIQUE_LETTERS +
                    " characters long."
                );
            }

            if (seedWord.indexOf(seedWord) == -1) {
                throw new IllegalArgumentException(
                    "The seed word must contain the required letter."
                );
            }

            if (!builder.setRootAndRequiredLetter(seedWord, requiredLetter)) {
                throw new IllegalArgumentException(
                    "The seed word must be a valid root word and contain the required letter."
                );
            }

            puzzle = builder.build();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Creates a new puzzle using a random word from the dictionary.
     * The random word will have exactly 7 unique letters but may be 
     * longer than 7 characters.
     * @throws IllegalArgumentException if the starting word was not valid.
     * @throws FileNotFoundException if the dictionary file could not be found.
     * @throws IOException if there was a problem reading the dictionary file.
     */
    public void createNewPuzzle() 
        throws IllegalArgumentException, FileNotFoundException, IOException{
        // Send empty word to create a random puzzle
        // If seedWord is empty requiredLetter is irrelevant
        createNewPuzzle("", 'a');
    }

    /**
     * Shuffles the letters in the display.
     */
    public void shuffleLetters() {
        puzzle.shuffle();
    }

    /**
     * Saves the puzzle to a JSON format.
     * @throws IOException - if an I/O error occurs.
     */
    public String savePuzzle() throws IOException {
        if (puzzle == null) {
            return "There is no puzzle in progress. Please try again.";
        }

        StringBuilder filename = new StringBuilder();

        //This will create a title for the Json file consisting
        // of the non-required letters followed by the required letter.
        for (char c : puzzle.getSecondaryLetters()){
            filename.append(c);
        }

        filename.append(puzzle.getPrimaryLetter());

        filename.append(".json");

        File saveLocation = new File(filename.toString());

        puzzle.save(saveLocation);

        return "File created: " + saveLocation.getAbsolutePath();
    }

    /**
     * Loads a saved puzzle from a JSON format.
     * @param loadFile - the file to be loaded
     */
    public String loadPuzzle(String loadFile) {
        String result = "";

        try{
            //Create a file object to read the contents of loadFile
            File savedFile = new File (loadFile);
            
            Puzzle LoadedPuzzle = Puzzle.loadPuzzle(savedFile, dictionaryFile);

            puzzle = LoadedPuzzle;
            result = "Succesfully loaded " + loadFile + "!";
        }
        catch (FileNotFoundException e) {
            result = "The file cannot be found";
        }
        catch (IOException e) {
            result = "There was an input or output error";
        } catch (JsonSyntaxException e) {
            result = "The file was not properly formatted as a puzzle.";
        }
        return result;
    }

    /**
     * Takes a guess, checks if it's a valid word,
     * and prints the appropriate output.
     * 
     * @param word The word that's being guessed.
     */
    public String guessWord(String word) {
        if (word == null) {
            word = "";
        }

        String result = "";

        if (puzzle == null) {
            result = "No puzzle is loaded.";
            return result;
        }

        Rank prevRank = puzzle.getRank();

        int wasValid = puzzle.guess(word);

        if (wasValid < 0) {
            result = "You already found this word.";
        } else if (wasValid == 0) {
            result = "\"" + word + "\" was not a valid word. Try again!";
        } else {
            String plural = "s";
            if (wasValid == 1) {
                plural = "";
            }
            result = "Good job! Your word was worth " + wasValid + 
            " point" + plural + ".";
        }

        Rank curRank = puzzle.getRank();
        if (!curRank.equals(prevRank)) {
            result +=
                "You reached a new rank! Your rank is now " +
                curRank.getRankName() + ".";
        }
        return result;
    }
}
