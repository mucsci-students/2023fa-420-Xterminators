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
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;

import xterminators.spellingbee.model.HelpData;
import xterminators.spellingbee.model.Puzzle;
import xterminators.spellingbee.model.PuzzleSave;
import xterminators.spellingbee.model.Rank;

public class GuiController {

    private Puzzle puzzle;
    private GuiView guiView;
    private HelpData helpData;

    public GuiController(GuiView guic) {
        puzzle = null;
        guiView = guic;
    }

    // Idealy these two files would just be instance variable and be passed to
    // a constructor, but they will be hardcoded untill proper constructors are
    // made.
    /** The file pointing to the full dictionary of usable words. */
    public static final File DICTIONARY_FILE = new File(
        Paths.get("src", "main", "resources", "dictionaries", "dictionary_optimized.txt").toString()
    );
    /** The file pointing to the dictionary of valid root words. */
    public static final File ROOT_DICTIONARY_FILE = new File(
        Paths.get("src", "main", "resources", "dictionaries", "dictionary_roots.txt").toString()
    );

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
            FileReader dictionaryFile = new FileReader(DICTIONARY_FILE);
            FileReader rootWordsFile = new FileReader(ROOT_DICTIONARY_FILE);
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

        String newline = System.lineSeparator();
        StringBuilder output = new StringBuilder();
        for (String word : puzzle.getFoundWords()) {
            output.append(word + newline);
        }
        System.out.println("Found Words:");
        System.out.println(output.toString());
    }

    /**
     * Saves the puzzle to a JSON format.
     * @throws IOException - if an I/O error occurs.
     */
    public String savePuzzle() throws IOException{
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
            filename += c;
        }

        // Take the necessary attributes and create a puzzleSave object,
        PuzzleSave testSave = PuzzleSave.ToSave(baseWord, puzzle.getFoundWords(),
        puzzle.getEarnedPoints(), puzzle.getPrimaryLetter(), puzzle.getTotalPoints());

        //Converts the current puzzle object to Json
        String savedJson = saved.toJson (testSave);

        //The message that will be displayed as a result of this change.
        String result = "";

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
                result = "File created: " + filename + ".json";

            } else {
                result = "A file by that name already exists. Overwriting the file";
                //Open a writer to replace the information in the file.
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

            FileReader dictionaryFile = new FileReader(DICTIONARY_FILE);

            ArrayList<String> newFoundWords = new ArrayList<String>();

            for(String word : loading.getPSFoundWords() ) {
                newFoundWords.add(word);
            }

            String work = "";
            for ( char c : BaseWord) {
                work = work + c;
            }

            Puzzle LoadedPuzzle = new Puzzle(RequiredLetter, newSecondaryLetters,
            dictionaryFile, loading.getPSPoints(), loading.getPSMaxPoints(), newFoundWords);

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
        String result = "";

        if (puzzle == null) {
            result = "No puzzle is loaded.";
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

    /**
     * A method to display the hints in the GUI.
     * 
     * @return String - The message that will be displayed in the GUI
     */
    public String hint(){
        String result = "";
        helpData = puzzle.getHelpData();

        //Initialize a baseword array for later printing the matrix
        char[] baseWord = new char[7];
        baseWord[6] = puzzle.getPrimaryLetter();
        char [] nonRequiredLetters = puzzle.getSecondaryLetters();
        for(int i = 0; i < baseWord.length - 1; i++){
            baseWord[i] = nonRequiredLetters[i];
        }
        Map<Pair<Character, Integer>, Long> trying = helpData.startingLetterGrid();
        
        // Find the maxwordsize to create the matrix
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
        // Set the summation rows titles, and a blank row for [0][0].
        grid[0][0] = " ";
        grid[8][0] = "\u03A3";
        grid[0][maxWordSize - 2] = "\u03A3";

        //Put the puzzle letters into the matrix
        for(int i = 1; i < 8; i++){
            grid[i][0] = "" + baseWord[i - 1];
        }

        //Puts the word lengths into the matrix
        for(int i = 1; i < maxWordSize - 2; i ++){
            grid[0][i] = (i + 3) + "";
        }

        //Initialize a variable for later manual padding.
        int maxValLen = 0;

        //This for loop maps the number of words to their size and letters
        //in the grid.
        for(Map.Entry<Pair<Character, Integer>, Long> work : trying.entrySet()){
            for(int k = 1; k < 8; k++){
                if((work.getKey().getLeft()+ "").equals(grid[k][0])){
                    grid[k][work.getKey().getRight() - 3] = (work.getValue() + "");

                    // Check to find the maximum value length.
                    if((work.getValue() + "").length() > maxValLen){
                        maxValLen = (work.getValue() + "").length();
                    }
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
        result += "Spelling Bee Grid" + 
        System.lineSeparator() + System.lineSeparator() +
    
        "Required letter is first" +
        System.lineSeparator() + System.lineSeparator() +

        (baseWord[6] + " ").toUpperCase();

        for(int i = 0; i < 6; i++){
            result += (baseWord[i] + "").toUpperCase() + " ";
        }
        result += System.lineSeparator() + System.lineSeparator() + 

        "WORDS: " + helpData.numWords() + ", POINTS: "
         + helpData.totalPoints() + ", PANGRAMS: " + helpData.numPangrams()
          + " (" + helpData.numPerfectPangrams() + " Perfect)" + 
          System.lineSeparator() + System.lineSeparator();

        //This for loop prints out the matrix
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < maxWordSize - 1; col ++){
                result +=  grid[row][col].toUpperCase() + "  ";
                if(grid[row][col].length() < maxValLen){
                    for(int i = grid[row][col].length(); i < maxValLen + 1; i++){
                        result += " ";
                    }
                }
            }
            result += System.lineSeparator();
        }

        //Print out the start of the two letter list section
        result += System.lineSeparator() + "Two letter list: " +
        System.lineSeparator() + System.lineSeparator();
        
        //Prints out the list of two letter starts.
        for(int i = 0; i < 7; i++){
            for (int k = 0; k < 6; k++){
                if(!twoLetList[i][k].equals("-")){
                    result += String.format("%-" + 6 + "s", 
                    twoLetList[i][k].toUpperCase());
                }
            }
            result += System.lineSeparator();
        }
        return result;
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
                earnedPoints + " point" + System.lineSeparator()
            );
        } else {
            System.out.println(
                "Current Rank: " + curRank.getRankName() + " - " +
                earnedPoints + " points" + System.lineSeparator()
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
