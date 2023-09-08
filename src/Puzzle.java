import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Puzzle {
    private String seedWord;
    private char[] letters;
    private ArrayList<String> validWords;
    private ArrayList<String> foundWords;
    private int totalPoints;
    private int earnedPoints;
    private int[] rankPoints;

    public char[] getLetters() {
        // returns a copy so that letters can not be modified externaly
        return Arrays.copyOf(this.letters, this.letters.length);
    }

    public List<String> getFoundWords() {
        // returns a read-only view of foundWords
        return Collections.unmodifiableList(this.foundWords);
    }

    public int getTotalPoints() {
        return this.totalPoints;
    }

    public int getEarnedPoints() {
        return this.earnedPoints;
    }
    
    /**
     * Processes a guess of word for the puzzle. If the word is a valid guess,
     * then the word will be added to foundWords, the point value of the word
     * will be added to earnedPoints. The value of the word will be returned.
     * 
     * @param word The word the user guessed
     * @return -1 if the word was already found,
     *          0 if the word is not in validWords,
     *          the number of points earned if the word is a valid guess
     */
    public int guess(String word) {
        // TODO Implement checking logic
    }

    /**
     * Calculates the point value of a word.
     * 
     * @param word The word to calculate the value of
     * @return The point value of the word if it is a valid guess,
     *         0 if the guess is invalid
     */
    private int wordValue(String word) {
        // TODO Implement logic and fomulas
    }

    /**
     * Checks if the word is a pangram (contains all seven puzzle letters)
     * 
     * @param word The word to check for pangram
     * @return if the word is a pangram
     */
    private boolean isPangram(String word) {
        // TODO Implement pangram check
    }
}
