package xterminators.spellingbee.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class EncryptedPuzzleSave extends PuzzleSave {
    private transient byte[] key;
    private transient byte[] iv;

    private String secretWordList;

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
        throws Exception
    {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
            Base64.getDecoder().decode(secretWordList)
        );

        if (key == null) {
            key = "Xterminators\0\0\0\0".getBytes();
            iv = "InitializaVector".getBytes();
        }

        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] decryptedWordsBytes = cipher.doFinal(inputStream.readAllBytes());

        ByteArrayInputStream decryptedInputStream = new ByteArrayInputStream(
            decryptedWordsBytes
        );

        List<String> decryptedWords = null;
        try (ObjectInputStream objectInputStream
                = new ObjectInputStream(decryptedInputStream))
        {
            decryptedWords = (List<String>) objectInputStream.readObject();
        }

        return decryptedWords;
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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOStream
                = new ObjectOutputStream(outputStream))
        {
            objectOStream.writeObject(words);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        byte[] encryptedWords = cipher.doFinal(outputStream.toByteArray());

        Base64.Encoder encoder = Base64.getEncoder();
        secretWordList = encoder.encodeToString(encryptedWords);
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
        encryptWords(validWords);
    }
}
