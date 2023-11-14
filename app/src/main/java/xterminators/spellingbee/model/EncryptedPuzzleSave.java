package xterminators.spellingbee.model;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedPuzzleSave extends PuzzleSave {
    private List<byte[]> encryptedValidWords;

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
    public EncryptedPuzzleSave(
        char[] baseWord,
        char requiredLetter,
        List<String> foundWords,
        int playerPoints,
        List<String> validWords,
        int maxPoints)
    {
        super(baseWord, requiredLetter, foundWords, playerPoints, maxPoints);
        
        this.encryptedValidWords = new ArrayList<>();
        encryptWords(validWords);
    }

    @Override
    public List<String> validWords() {
        byte[] key = "Xterminators\0\0\0\0".getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        byte[] iv = "InitializaVector".getBytes();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        List<String> validWords = new ArrayList<>();

        try {
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            for (byte[] encryptedWord : encryptedValidWords) {
                byte[] decryptedWord = cipher.doFinal(encryptedWord);
                validWords.add(new String(decryptedWord));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return validWords;
    }

    /**
     * Encrypts the given list of words. Uses nonce encryption mode with AES.
     * 
     * @param words the list of words to encrypt
     * @return the encrypted list of words
     */
    private void encryptWords(List<String> words) {
        byte[] key = "Xterminators\0\0\0\0".getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        byte[] iv = "InitializaVector".getBytes();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        try {
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            for (String word : words) {
                byte[] encryptedWord = cipher.doFinal(word.getBytes());
                encryptedValidWords.add(encryptedWord);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
