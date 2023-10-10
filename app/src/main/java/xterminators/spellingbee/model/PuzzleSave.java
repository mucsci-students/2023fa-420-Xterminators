package xterminators.spellingbee.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PuzzleSave {

    /** The required letter of the puzzle. */
    private char requiredLetter;
    /** The secondary letters of the puzzle. */
    private char[] baseWord;
    /** The list of all words currently found in the puzzle. */
    private List<String> foundWords;
    /** The total number of points that can be earned in the puzzle. */
    private int maxPoints;
    /** The number of points currently earned in the puzzle. */
    private int playerPoints;
    
    public PuzzleSave(char[] baseWord, List<String> foundWords, int playerPoints, char requiredLetter,
        int maxPoints) {
        this.baseWord = baseWord;
        this.foundWords = foundWords;
        this.playerPoints = playerPoints;
        this.requiredLetter = requiredLetter;
        this.maxPoints = maxPoints;
    }

    /**
     * Constructs a Puzzle object from the required letter, and the six other
     * acceptable letters. Fills validWords by parcing through dictionaryFile.
     * 
     * @param requiredLetter The required letter for the puzzle
     * @param baseWord the letters contained in the baseword
     * @throws IOException if there is an I/O error
     */
    public static PuzzleSave ToSave(char[] baseWord, List<String> foundWords, int playerPoints, char requiredLetter, int maxPoints) throws IOException {

        return new PuzzleSave(baseWord, foundWords, playerPoints, requiredLetter, maxPoints);
    }

    public char[] getPSBase(){
        return this.baseWord;
    }

    public List<String> getPSFoundWords(){
        return this.foundWords;
    }

    public int getPSPoints(){
        return this.playerPoints;
    }

    public char getPSPrimaryLetter(){
        return this.requiredLetter;
    }

    public int getPSMaxPoints(){
        return this.maxPoints;
    }



}