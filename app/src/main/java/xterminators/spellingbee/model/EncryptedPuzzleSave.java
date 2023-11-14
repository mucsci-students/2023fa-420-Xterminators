package xterminators.spellingbee.model;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedPuzzleSave extends PuzzleSave {
    private final static byte[] key = "Xterminators\0\0\0\0".getBytes();
    private final static byte[] iv = "InitializaVector".getBytes();

    private List<byte[]> secretWordList;

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
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public EncryptedPuzzleSave(
        char[] baseWord,
        char requiredLetter,
        List<String> foundWords,
        int playerPoints,
        List<String> validWords,
        int maxPoints
    ) throws InvalidKeyException, NoSuchAlgorithmException,
        NoSuchPaddingException, InvalidAlgorithmParameterException,
        IllegalBlockSizeException, BadPaddingException
    {
        super(baseWord, requiredLetter, foundWords, playerPoints, maxPoints);
        
        this.secretWordList = new ArrayList<>();
        encryptWords(validWords);
    }

    @Override
    public List<String> validWords() 
        throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException
    {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        List<String> validWords = new ArrayList<>();

        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        for (byte[] encryptedWord : secretWordList) {
            byte[] decryptedWord = cipher.doFinal(encryptedWord);
            validWords.add(new String(decryptedWord));
        }

        return validWords;
    }

    /**
     * Encrypts the given list of words. Uses nonce encryption mode with AES.
     * 
     * @param words the list of words to encrypt
     * @return the encrypted list of words
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private void encryptWords(List<String> words)
        throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException
    {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        for (String word : words) {
            byte[] encryptedWord = cipher.doFinal(word.getBytes());
            secretWordList.add(encryptedWord);
        }
    }
}
