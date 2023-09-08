import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Puzzle {
    /** The minimum length for a word to be considered valid and earn points. */
    private final static int MINIMUM_WORD_LENGTH = 4;

    /** The number of bonus points recived for finding a pangram. */
    private final static int PANGRAM_BONUS = 7;

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
        // If the word is already found, return -1
        if (foundWords.stream().anyMatch(s -> s.equalsIgnoreCase(word))) {
            return -1;
        }

        int points = wordValue(word);

        // If points (wordValue) != 0, then the word is valid
        if (points != 0) {
            foundWords.add(word);
            earnedPoints += points;
        }
        
        return points;
    }

    /**
     * Calculates the point value of a word.
     * 
     * @param word The word to calculate the value of
     * @return The point value of the word if it is a valid guess,
     *         0 if the guess is invalid
     */
    private int wordValue(String word) {
        // If validWords does not contain word ignoring case, return 0
        if (validWords.stream().noneMatch(s -> s.equalsIgnoreCase(word))) {
            return 0;
        }

        int wordValue;
        if (word.length() == MINIMUM_WORD_LENGTH) {
            wordValue = 1;
        } else {
            wordValue = word.length();
        }

        if (isPangram(word)) {
            wordValue += PANGRAM_BONUS;
        }

        return wordValue;
    }

    /**
     * Checks if the word is a pangram (contains all seven puzzle letters)
     * 
     * @param word The word to check for pangram
     * @return true if the word is a pangram, false otherwise
     */
    private boolean isPangram(String word) {
        for (char c : letters) {
            if (word.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }
}
