package xterminators.spellingbee.model;

import java.util.List;

/**
 * A PuzzleSave represents the state of a puzzle that can be saved and loaded.
 */
public abstract class PuzzleSave {
    private char[] baseWord;
    private char requiredLetter;
    private List<String> foundWords;
    private int playerPoints;
    private int maxPoints;
    private final String author = "Xterminators";

    public PuzzleSave(char[] baseWord, char requiredLetter, List<String> foundWords, int playerPoints, int maxPoints) {
        this.baseWord = baseWord;
        this.requiredLetter = requiredLetter;
        this.foundWords = foundWords;
        this.playerPoints = playerPoints;
        this.maxPoints = maxPoints;
    }

    /**
     * Returns the base word of the save.
     *
     * @return the base word of the save
     */
    public char[] baseWord() {
        return baseWord;
    }

    /**
     * Returns the required letter of the save.
     *
     * @return the required letter of the save
     */
    public char requiredLetter() {
        return requiredLetter;
    }

    /**
     * Returns the list of found words of the save.
     *
     * @return the list of found words of the save
     */
    public List<String> foundWords() {
        return foundWords;
    }

    /**
     * Returns the player's earned points of the save.
     *
     * @return the player's earned points of the save
     */
    public int playerPoints() {
        return playerPoints;
    }

    /**
     * Returns the maximum points of the save.
     *
     * @return the maximum points of the save
     */
    public int maxPoints() {
        return maxPoints;
    }

    /**
     * Returns the list of valid words of the save.
     *
     * @return the list of valid words of the save
     */
    public abstract List<String> validWords() throws Exception;
}
