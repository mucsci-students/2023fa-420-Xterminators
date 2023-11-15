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

import com.google.gson.annotations.Expose;

public final class EncryptedPuzzleSave extends PuzzleSave {
    @Expose(serialize = false, deserialize = false)
    private final byte[] key;

    @Expose(serialize = false, deserialize = false)
    private final byte[] iv;

    private List<byte[]> secretWordList;

    public static EncryptedPuzzleSave fromKeyIV(
        char[] baseWord,
        char requiredLetter,
        List<String> foundWords,
        int playerPoints,
        List<String> validWords,
        int maxPoints,
        byte[] key,
        byte[] iv)
    {
        try {
            return new EncryptedPuzzleSave(
                baseWord,
                requiredLetter,
                foundWords,
                playerPoints,
                validWords,
                maxPoints,
                key,
                iv
            );
        } catch (Exception e) {
            return null;
        }
    }

    public static EncryptedPuzzleSave fromDefaults(
        char[] baseWord,
        char requiredLetter,
        List<String> foundWords,
        int playerPoints,
        List<String> validWords,
        int maxPoints)
    {
        return fromKeyIV(
            baseWord,
            requiredLetter,
            foundWords,
            playerPoints,
            validWords,
            maxPoints,
            "Xterminators\0\0\0\0".getBytes(),
            "InitializaVector".getBytes()
        );
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

    private EncryptedPuzzleSave(
        char[] baseWord,
        char requiredLetter,
        List<String> foundWords,
        int playerPoints,
        List<String> validWords,
        int maxPoints,
        byte[] key,
        byte[] iv
    ) throws NoSuchAlgorithmException, NoSuchPaddingException,
        InvalidKeyException, InvalidAlgorithmParameterException,
        IllegalBlockSizeException, BadPaddingException
    {
        super(baseWord, requiredLetter, foundWords, playerPoints, maxPoints);
        this.key = key;
        this.iv = iv;
        this.secretWordList = new ArrayList<>();
        encryptWords(validWords);
    }
}
