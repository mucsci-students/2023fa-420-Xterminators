package xterminators.spellingbee.model;

import java.util.List;

import com.google.gson.annotations.Expose;

public class EncryptedPuzzleSave extends PuzzleSave {
    private List<byte[]> encryptedValidWords;

    @Expose(serialize = false, deserialize = false)
    private String password;

    /**
     * Creates a new EncryptedPuzzleSave.
     * 
     * @param baseWord an array containing all the letters of the puzzle
     * @param requiredLetter the required letter of the puzzle
     * @param foundWords the player's found words
     * @param playerPoints the player's earned points
     * @param validWords the list of valid words
     * @param maxPoints the maximum points of the puzzle
     * @param password the password to encrypt the valid words with
     */
    public EncryptedPuzzleSave(char[] baseWord, char requiredLetter, List<String> foundWords, int playerPoints, List<String> validWords, int maxPoints, String password) {
        super(baseWord, requiredLetter, foundWords, playerPoints, maxPoints);

        this.password = password;
        
        this.encryptedValidWords = encryptWords(validWords);
    }

    @Override
    public List<String> validWords() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Decryption not implemented");
    }

    /**
     * Encrypts the given list of words. Uses nonce encryption mode with AES.
     * 
     * @param words the list of words to encrypt
     * @return the encrypted list of words
     */
    private List<byte[]> encryptWords(List<String> words) {
        // TODO: Implement encryption
        throw new UnsupportedOperationException("Encryption not implemented");
    }
}
