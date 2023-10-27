package xterminators.spellingbee.gui;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;

import xterminators.spellingbee.model.Puzzle;
import xterminators.spellingbee.model.PuzzleSave;
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
        puzzle = null;
        guiView = guic;
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
            FileReader dictionaryFileReader = new FileReader(dictionaryFile);
            FileReader rootWordsFileReader = new FileReader(rootsDictionaryFile);
            if (seedWord != null && !seedWord.equals("")) {
                puzzle = Puzzle.fromWord(seedWord, requiredLetter, 
                    rootWordsFileReader, dictionaryFileReader, false);
            } else {
                // no seedWord provided, assume random puzzle
                puzzle = Puzzle.randomPuzzle(rootWordsFileReader, dictionaryFileReader);
            }
        } catch (IllegalArgumentException | IOException e) {
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
    public String savePuzzle() throws IOException{
        Gson saved = new Gson();

        char[] nonRequiredLetters = puzzle.getSecondaryLetters();

        char[] baseWord = new char[7];
        System.arraycopy(nonRequiredLetters, 0, baseWord, 0, nonRequiredLetters.length);

        baseWord[6] = puzzle.getPrimaryLetter();

        String filename = "";

        //This will create a title for the Json file consisting
        // of the non-required letters followed by the required letter.
        for (char c : baseWord){
            filename += c;
        }

        // Take the necessary attributes and create a puzzleSave object
        PuzzleSave testSave = PuzzleSave.ToSave(baseWord, puzzle.getFoundWords(),
        puzzle.getEarnedPoints(), puzzle.getPrimaryLetter(), puzzle.getTotalPoints());

        //Converts the current puzzle object to Json
        String savedJson = saved.toJson (testSave);
        String result = "";

        try {
            File savedFile = new File(filename + ".json");

            //Returns true if a new file is created.
            if(savedFile.createNewFile()) {

                //Create a file writer to populate the created File
                FileWriter writing = new FileWriter(savedFile);

                writing.write(savedJson);
                writing.close();

                result = "File created: " + filename + ".json";

            } else {
                result = "A file by that name already exists. Overwriting the file";

                PrintWriter writer = new PrintWriter(savedFile);
                writer.print(savedJson);
                writer.close();
            }
        }
        catch (IOException e) {
            result = "File could not be saved, an Input/Output error occurred";
        }        
        return result;
    }

    /**
     * Loads a saved puzzle from a JSON format.
     * @param loadFile - the file to be loaded
     */
    public String loadPuzzle(String loadFile) {
        
        Gson load = new Gson();
        String jsonPuzzle = "";

        String result = "";

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
            PuzzleSave loading = load.fromJson (jsonPuzzle, PuzzleSave.class);

            //Initialize the values that will be used by PuzzleSave
            char[] BaseWord = loading.getPSBase();
            char RequiredLetter = BaseWord[6];

            char[] newSecondaryLetters = new char[6];
            for(int i = 0; i < newSecondaryLetters.length; i ++){
                newSecondaryLetters[i] = BaseWord[i];
            }

            FileReader dictionaryFileReader = new FileReader(dictionaryFile);

            ArrayList<String> newFoundWords = new ArrayList<>();

            for(String word : loading.getPSFoundWords() ) {
                newFoundWords.add(word);
            }

            String work = "";
            for ( char c : BaseWord) {
                work = work + c;
            }

            Puzzle LoadedPuzzle = new Puzzle(RequiredLetter, newSecondaryLetters,
            dictionaryFileReader, loading.getPSPoints(), loading.getPSMaxPoints(), newFoundWords);

            puzzle = LoadedPuzzle;
            result = "Succesfully loaded " + loadFile + "!";
        }
        catch (FileNotFoundException e) {
            result = "The file cannot be found";
        }
        catch (IOException e) {
            result = "There was an input or output error";
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
