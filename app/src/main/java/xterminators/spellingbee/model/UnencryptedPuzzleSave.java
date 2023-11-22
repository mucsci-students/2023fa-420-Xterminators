package xterminators.spellingbee.model;

import java.util.List;

/**
 * A PuzzleSave class for storing a puzzle with an unencrypted word list.
 */
public final class UnencryptedPuzzleSave extends PuzzleSave {
    private List<String> wordList;

    /**
     * Constructs a new UnencryptedPuzzleSave.
     * 
     * @param baseWord an array containing all the letters of the puzzle
     * @param requiredLetter the required letter of the puzzle
     * @param foundWords the player's found words
     * @param playerPoints the player's earned points
     * @param validWords the list of valid words
     * @param maxPoints the maximum points of the puzzle
     */
    public UnencryptedPuzzleSave(
        char[] baseWord,
        char requiredLetter,
        List<String> foundWords,
        int playerPoints,
        List<String> validWords,
        int maxPoints
    ) {
        super(baseWord, requiredLetter, foundWords, playerPoints, maxPoints);
        this.wordList = validWords;
    }

    @Override
    public List<String> validWords() {
        return wordList;
    }
}
