package xterminators.spellingbee.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonSyntaxException;

import xterminators.spellingbee.model.HelpData;
import xterminators.spellingbee.model.Puzzle;
import xterminators.spellingbee.model.PuzzleBuilder;
import xterminators.spellingbee.model.Rank;
import xterminators.spellingbee.model.SaveMode;
import xterminators.spellingbee.model.HighScores;
import xterminators.spellingbee.ui.Controller;

public class GuiController extends Controller {
    /** The view that the user interacts with. */
    private GuiView guiView;
    /** The file pointing to the full dictionary of usable words. */
    private File dictionaryFile;
    /** The file pointing to the dictionary of valid root words. */
    private File rootsDictionaryFile;

    private HelpData helpData;

    private HighScores highScores;

    public GuiController(GuiView guic, File dictionaryFile, File rootsDictionaryFile) {
        this.guiView = guic;
        this.dictionaryFile = dictionaryFile;
        this.rootsDictionaryFile = rootsDictionaryFile;
        this.highScores = new HighScores();
    }

    @Override
    public void run() {
        guiView.InitUI();
    }

    /**
     * Sets the view that the controller interacts with.
     * 
     * @param view the new view
     */
    public void setView(GuiView view) {
        this.guiView = view;
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
                builder.build();
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

            builder.build();
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
        Puzzle puzzle = Puzzle.getInstance();
        puzzle.shuffle();
    }

    public String savePuzzle() throws IOException {
        Puzzle puzzle = Puzzle.getInstance();
        StringBuilder filename = new StringBuilder();

        //This will create a title for the Json file consisting
        // of the non-required letters followed by the required letter.
        for (char c : puzzle.getSecondaryLetters()) {
            filename.append(c);
        }
        filename.append(puzzle.getPrimaryLetter());
        filename.append(".json");

        return savePuzzle(filename.toString(), SaveMode.ENCRYPTED);
    }

    /**
     * Saves the puzzle to a JSON format.
     * @throws IOException - if an I/O error occurs.
     */
    public String savePuzzle(String saveFilepath, SaveMode saveMode) throws IOException {
        Puzzle puzzle = Puzzle.getInstance();

        if (puzzle == null) {
            return "There is no puzzle in progress. Please try again.";
        }

        File saveLocation = new File(saveFilepath);
        puzzle.save(saveLocation, saveMode);

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
        Puzzle puzzle = Puzzle.getInstance();

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

    /**
     * A method to display the hints in the GUI.
     * 
     * @return String - The message that will be displayed in the GUI
     */
    public String hint(){
        Puzzle puzzle = Puzzle.getInstance();
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
     * Gets the high scores from the HighScores class.
     */
    public TreeMap<String, Integer> getHighScores() {
        return highScores.getScores();
    }

    /**
     * Saves a high score using the given user name
     * and the current score in the puzzle.
     * 
     * @param userName The user's name that they gave.
     */
    public boolean saveHighScore(String userName) {
        Puzzle puzzle = Puzzle.getInstance();
        return highScores.saveScore(userName, puzzle.getEarnedPoints());
    }

    /**
     * Checks if the puzzle's score is worthy of being a high score.
     * If the score given is greater than or equal to the lowest 
     * high score, this returns true.
     */
    public boolean isHighScore() {
        Puzzle puzzle = Puzzle.getInstance();
        return highScores.isHighScore(puzzle.getEarnedPoints());
    }

}
