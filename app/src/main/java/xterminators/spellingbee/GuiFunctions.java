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

public class GuiFunctions {

    private Puzzle puzzle;
    private GuiController guiController;

    public GuiFunctions(GuiController guic) {
        puzzle = null;
        guiController = guic;
    }

    /** The path to the dictionary file for the game. */
    public static final String DICTIONARY_PATH
        = Paths.get("src", "main", "resources", "dictionary_optimized.txt").toString();
    /** The path to the dictionary of valid starting words. */
    public static final String ROOT_DICTIONARY_PATH
        = Paths.get("src", "main", "resources", "dictionary_roots.txt").toString();

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
            FileReader dictionaryFile = new FileReader(DICTIONARY_PATH);
            FileReader rootWordsFile = new FileReader(ROOT_DICTIONARY_PATH);
            if (seedWord != null && !seedWord.equals("")) {
                puzzle = Puzzle.fromWord(seedWord, requiredLetter, 
                    rootWordsFile, dictionaryFile, false);
            } else {
                // no seedWord provided, assume random puzzle
                puzzle = Puzzle.randomPuzzle(rootWordsFile, dictionaryFile);
            }
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
     * Prints the list of found words.
     */
    private void showFoundWords() {
        if (puzzle == null) {
            return;
        }

        String newline = "\n";
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
                //TODO Replace with GUI System.out.println("File created: " + filename + ".json");
            } else {
                //TODO Replace with GUI System.out.println("A file by that name already exists." + getNewLineCharacter() + "Overwriting the file");

                //Open a writer to replace the information in the file.
                PrintWriter writer = new PrintWriter(savedFile);
                writer.print(savedJson);
                writer.close();
            }
        }
        catch (IOException e) {
            //TODO Replace with GUI System.out.println("An error occurred");
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
     * Prints all available ranks, along with the current rank.
     */
    private void showRanks() {
        //TODO Make this work with GUI 
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
    }
}
