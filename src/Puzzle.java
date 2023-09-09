import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Puzzle {
    /** The minimum length for a word to be considered valid and earn points. */
    private final static int MINIMUM_WORD_LENGTH = 4;
    /** The number of bonus points recived for finding a pangram. */
    private final static int PANGRAM_BONUS = 7;

    /** The primary (required) letter of the puzzle. */
    private char primaryLetter;
    /** The secondary letters of the puzzle. */
    private char[] secondaryLetters;
    /** The list of all valid words for the puzzle. */
    private ArrayList<String> validWords;
    /** The list of all words currently found in the puzzle. */
    private ArrayList<String> foundWords;
    /** The total number of points that can be earned in the puzzle. */
    private int totalPoints;
    /** The number of points currently earned in the puzzle. */
    private int earnedPoints;
    /** The number of points necessary for each rank. */
    private int[] rankPoints;

    /**
     * Gets the primary (required) letter of the puzzle.
     * 
     * @return The primary letter of the puzzle
     */
    public char getPrimaryLetter() {
        return primaryLetter;
    }

    /**
     * Gets the secondary letters of the puzzle.
     * 
     * @return A copy of the array of secondary letters
     */
    public char[] getSecondaryLetters() {
        // returns a copy so that it won't modify the puzzle if modified
        return Arrays.copyOf(secondaryLetters, secondaryLetters.length);
    }

    /**
     * Gets the list of found words.
     * 
     * @return An unmodifiable list of the currently found words in the puzzle
     */
    public List<String> getFoundWords() {
        // returns a read-only view of foundWords
        return Collections.unmodifiableList(this.foundWords);
    }

    /**
     * Gets the number of points that have currently been earned on the puzzle.
     * 
     * @return The number of earned points
     */
    public int getEarnedPoints() {
        return this.earnedPoints;
    }

    /**
     * Gets the array defining how many points are needed to reach each rank in
     * the puzzle.
     * 
     * @return The array of rank point minimums
     */
    public int[] getRankPoints() {
        return Arrays.copyOf(rankPoints, rankPoints.length);
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
     * Shuffles the secondary letters for the next display
     * 
     * @implNote Used the Fisher–Yates shuffle algorithim to shuffle secondaryLetters.
     * See https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
     */
    public void shuffle() {
        int index;
        Random random = new Random();
        for (int i = secondaryLetters.length - 1; i > 0; --i) {
            index = random.nextInt(i + 1);
            char temp = secondaryLetters[index];
            secondaryLetters[index] = secondaryLetters[i];
            secondaryLetters[i] = temp;
        }
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
        for (char c : secondaryLetters) {
            if (word.indexOf(c) == -1) {
                return false;
            }
        }

        if (word.indexOf(primaryLetter) == -1) {
            return false;
        }
        
        return true;
    }
}
