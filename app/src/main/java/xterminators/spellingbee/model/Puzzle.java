package xterminators.spellingbee.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Puzzle {
    /** The minimum length for a word to be considered valid and earn points. */
    private final static int MINIMUM_WORD_LENGTH = 4;
    /** The number of letters for the puzzle. */
    private final static int NUMBER_UNIQUE_LETTERS = 7;
    /** The number of bonus points recived for finding a pangram. */
    private final static int PANGRAM_BONUS = 7;

    /** The primary (required) letter of the puzzle. */
    private char primaryLetter;
    /** The secondary letters of the puzzle. */
    private char[] secondaryLetters;
    /** The list of all valid words for the puzzle. */
    private List<String> validWords;
    /** The list of all words currently found in the puzzle. */
    private List<String> foundWords;
    /** The total number of points that can be earned in the puzzle. */
    private int totalPoints;
    /** The number of points currently earned in the puzzle. */
    private int earnedPoints;

    /** The HelpData object storing all the help data for the puzzle. */
    private HelpData helpData;

    /**
     * A record for containing all the data in a JSON save of a puzzle.
     */
    private record PuzzleData(
        char[] baseWord,
        List<String> foundWords,
        int playerPoints,
        char requiredLetter,
        int maxPoints
    ) {}

    /**
     * Loads a saved puzzle json into a Puzzle object.
     * 
     * @param savedPuzzle the file where a puzzle is saved
     * @param dictionaryFile the dictionary file to be used to generate
     *                       validWords
     * @return a new Puzzle constructed from the save file's contents
     * @throws FileNotFoundException if the save file doesn't exist or
     *                               if the dictionary file doesn't exist
     * @throws IOException
     * @throws JsonSyntaxException if json is not a valid representation for a
     *                             saved puzzle
     * @throws IllegalArgumentException if the json file contains an impossible
     *                                  puzzle
     */
    public static Puzzle loadPuzzle(File savedPuzzle, File dictionaryFile)
        throws FileNotFoundException, IOException, JsonSyntaxException,
               IllegalArgumentException
    {
        Scanner reader = new Scanner(savedPuzzle);

        StringBuilder json = new StringBuilder();

        while(reader.hasNextLine()) {
            json.append(reader.nextLine());
        }

        reader.close();

        Gson gson = new Gson();

        PuzzleData saveData = gson.fromJson(json.toString(), PuzzleData.class);

        return new Puzzle(saveData, dictionaryFile);
    }

    /**
     * Constructs a Puzzle object from the given puzzle data.
     * 
     * @param puzzleData the puzzle data to construct into a Puzzle
     * @param dictionaryFile the dictionary of words to parse
     * @throws FileNotFoundException if the dictionary file is not found
     * @throws IOException
     * @throws IllegalArgumentException if the puzzle data represents an invalid
     *                                  puzzle
     */
    private Puzzle(PuzzleData puzzleData, File dictionaryFile)
        throws FileNotFoundException, IOException, IllegalArgumentException
    {
        this.primaryLetter = puzzleData.requiredLetter();

        List<Character> otherLetters = new ArrayList<>();
        for (char c : puzzleData.baseWord()) {
            if (c != primaryLetter && !otherLetters.contains(c)) {
                otherLetters.add(Character.valueOf(c));
            }
        }

        if (otherLetters.size() < 6) {
            throw new IllegalArgumentException(
                "Not enough unique letters in base word"
            );
        } else if (otherLetters.size() > 6) {
            throw new IllegalArgumentException(
                "Too many unique letters in base word"
            );
        }
        // Past here otherLetters.size() == 6

        this.secondaryLetters = new char[6];

        for (int i = 0; i < 6; i++) {
            this.secondaryLetters[i] = otherLetters.get(i);
        }

        FileReader dictionaryFileReader = new FileReader(dictionaryFile);
        BufferedReader dictBuffReader = new BufferedReader(dictionaryFileReader);

        this.validWords = dictBuffReader.lines()
            .parallel()
            .filter(this::isValid)
            .toList();

        dictBuffReader.close();
        dictionaryFileReader.close();

        this.totalPoints = validWords.parallelStream()
            .mapToInt(this::wordValue)
            .sum();
        
        this.foundWords = new ArrayList<>(puzzleData.foundWords());

        this.earnedPoints = this.foundWords.parallelStream()
            .mapToInt(this::wordValue)
            .sum();
    }

    /**
     * Constructs a Puzzle object from the required letter, and the six other
     * acceptable letters. Fills validWords by parcing through dictionaryFile.
     * 
     * @param primaryLetter The required letter for the puzzle
     * @param secondaryLetters The six other acceptable letters for the puzzle
     * @param dictionaryFile The dictionary file to be used to generate validWords.
     * @throws IOException if an I/O error occurs.
     */
    public Puzzle(char primaryLetter, char[] secondaryLetters,
                  FileReader dictionaryFile) throws IOException {
        this.primaryLetter = primaryLetter;
        this.secondaryLetters = Arrays.copyOf(secondaryLetters,
                                              secondaryLetters.length);
        this.validWords = new ArrayList<String>();
        this.foundWords = new ArrayList<String>();
        this.totalPoints = 0;
        this.earnedPoints = 0;

        BufferedReader bufferedReader = new BufferedReader(dictionaryFile);

        char[] sortedLetters = Arrays.copyOf(secondaryLetters, secondaryLetters.length);
        Arrays.sort(sortedLetters, 0, sortedLetters.length);

        dictLoop:
        for (String word = bufferedReader.readLine(); word != null; 
             word = bufferedReader.readLine()) {
            if (word.indexOf(primaryLetter) == -1) {
                continue;
            }

            for (char c : word.toLowerCase().toCharArray()) {
                if (c != primaryLetter
                        && Arrays.binarySearch(sortedLetters, c) < 0) {
                    continue dictLoop;
                }
            }

            // To get to this point, all letters of the puzzle are in the word
            this.validWords.add(word);
            this.totalPoints += this.wordValue(word);
        }

        bufferedReader.close();

        this.helpData = calculateHelpData();
    }

    /**
     * Constructs a puzzle object that takes the attributes from a PuzzleSave 
     * object, so saved files can be loaded.
     *  
     * @param primaryLetter the required letter for the puzzle
     * @param secondaryLetters the non-required letters for the puzzle
     * @param dictionaryFile the dictionary file to be used to generate valid words
     * @param earnedPoints the number of points that were earned when the puzzle was saved
     * @param totalPoints the maximum number of points possible in the puzzle
     * @param foundWords the list of words that had been found at time of save
     * @throws IOException if an I/O error occurs
     */
    public Puzzle(char primaryLetter, char[] secondaryLetters,
                  FileReader dictionaryFile, int earnedPoints, int totalPoints, ArrayList<String> foundWords) throws IOException {

        this.validWords = new ArrayList<String>();
        this.foundWords = foundWords;
        this.primaryLetter = primaryLetter;
        this.secondaryLetters = secondaryLetters;
        this.earnedPoints = earnedPoints;
        this.totalPoints = totalPoints;

        BufferedReader bufferedReader = new BufferedReader(dictionaryFile);

        char[] sortedLetters = Arrays.copyOf(secondaryLetters, secondaryLetters.length);
        Arrays.sort(sortedLetters, 0, sortedLetters.length);

        dictLoop:
        // reconstruct the valid words list
        for (String word = bufferedReader.readLine(); word != null; 
             word = bufferedReader.readLine()) {
            if (word.indexOf(primaryLetter) == -1) {
                continue;
            }

            for (char c : word.toLowerCase().toCharArray()) {
                if (c != primaryLetter
                        && Arrays.binarySearch(sortedLetters, c) < 0) {
                    continue dictLoop;
                }
            }

            this.validWords.add(word);
        }

        bufferedReader.close();

        this.helpData = calculateHelpData();
    }

     /**
      * Create a puzzle from a given starting word with given required letter.
      * Fills validWords by parsing through dictionaryFile.
      *
      * @param word The starting word for the puzzle
      * @param primaryLetter The required letter of the word for the puzzle
      * @param dictionaryFile The dictionary file to be used to generate validWords
      * @return A puzzle based on the starting word and required letter
      * @throws IllegalArgumentException if the word is not usable as a starting
      *                                  word given the required letter
      * @throws IOException if an I/O error occurs
      */
    public static Puzzle fromWord(String word, char primaryLetter, 
                                  FileReader rootWordsFile, 
                                  FileReader dictionaryFile,
                                  boolean isKnownRootWord) 
        throws IllegalArgumentException, IOException {

        if (word.length() < NUMBER_UNIQUE_LETTERS) {
            throw new IllegalArgumentException(
                "Invalid argument: \"" + word + "\" is too short to be a" +
                "starting word."
            );
        }
        
        if (word.indexOf(primaryLetter) == -1) {
            throw new IllegalArgumentException(
                "Invalid argument: \"" + word + "\" does not contain required" +
                "letter \'" + primaryLetter + "\'."
            );
        }

        ArrayList<Character> chars = new ArrayList<>();
        for (char c : word.toCharArray()) {
            if (!chars.contains(c)) {
                chars.add(c);
            }
        }

        if (chars.size() < NUMBER_UNIQUE_LETTERS) {
            throw new IllegalArgumentException(
                "Invalid argument: \"" + word + "\" contains too few unique " +
                "characters to be a starting word."
            );
        }

        // This only runs if the root word is a user input.
        if (!isKnownRootWord) {
            // Check if it's in the root word dictionary last because this is
            // the most expensive check. If one of the cheap word checks throws
            // an exception, we don't have to bother doing this.
            boolean isRootWord = false;
            BufferedReader bufferedReader = new BufferedReader(rootWordsFile);
            for (String rootWord = bufferedReader.readLine(); rootWord != null; 
                rootWord = bufferedReader.readLine()) {
                if (!rootWord.equals("") && word.equalsIgnoreCase(rootWord)) {
                    isRootWord = true;
                    break;
                }
            }

            if (!isRootWord) {
                throw new IllegalArgumentException(
                    "Invalid argument: \"" + word + "\" is not in the dictionary.");
            }
        }

        chars.remove(Character.valueOf(primaryLetter));
        char[] secondaryLetters = new char[NUMBER_UNIQUE_LETTERS - 1];
        for (int i = 0; i < NUMBER_UNIQUE_LETTERS - 1; ++i) {
            secondaryLetters[i] = chars.get(i);
        }

        return new Puzzle(primaryLetter, secondaryLetters, dictionaryFile);
    }

    /**
     * Creates a puzzle from a random word in the root word dictionary.
     * 
     * @param rootWordsFile The dictionary to pick a random word from
     * @param dictionaryFile The dictionary to use to fill the validWords list
     * @return A random puzzle
     * @throws IOException if an I/O error occurs
     */
    public static Puzzle randomPuzzle(FileReader rootWordsFile,
                                      FileReader dictionaryFile)
        throws IOException {

        ArrayList<String> candidateWords = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(rootWordsFile);
        for (String word = bufferedReader.readLine(); word != null; 
             word = bufferedReader.readLine()) {
            candidateWords.add(word);
        }
        
        Random random = new Random();
        while (true) {
            int wordIndex = random.nextInt(candidateWords.size());
            String word = candidateWords.get(wordIndex);
            int charIndex = random.nextInt(word.length());
            char primaryLetter = word.charAt(charIndex);
            try {
                return fromWord(word, primaryLetter, rootWordsFile, dictionaryFile, true);
            } catch (IllegalArgumentException e) {
                candidateWords.remove(wordIndex);
            }
        }
    }

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
     * Gets the total number of possible points in the puzzle.
     * 
     * @return the total number of possible points in the puzzle
     */
    public int getTotalPoints() {
        return totalPoints;
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
     * Gets the current rank achived for the puzzle.
     * 
     * @return the current rank achived for the puzzle
     */
    public Rank getRank() {
        Rank currentRank = null;
        for (Rank rank : Rank.values()) {
            if (earnedPoints >= rank.getRequiredPoints(totalPoints)) {
                currentRank = rank;
            }
        }
        return currentRank;
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
        if (!isValid(word)) {
            return 0;
        }

        // If the word is already found, return -1
        if (foundWords.stream().anyMatch(s -> s.equalsIgnoreCase(word))) {
            return -1;
        }

        int points = wordValue(word);

        foundWords.add(word);
        Collections.sort(foundWords);
        earnedPoints += points;
        
        return points;
    }

    /**
     * Gets the help data for the puzzle.
     * 
     * @return The help data for the puzzle
     */
    public HelpData getHelpData() {
        return helpData;
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
     * Determines if a word is a valid word for this puzzle.
     * 
     * @param word the word to be checked for validity
     * @return whether or not the word is a valid word for this puzzle
     */
    private boolean isValid(String word) {
        if (word.length() < MINIMUM_WORD_LENGTH
            || word.indexOf(primaryLetter) == -1)
        {
            return false;
        }

        // Checks each letter for validity
        wordLetterLoop:
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            for (char secondary : secondaryLetters) {
                if (c == secondary || c == primaryLetter) {
                    // If the letter is in secondaryLetters (or is the primary
                    // letter), check the next letter.
                    continue wordLetterLoop;
                }
            }

            // If none of the secondary letters match c (c is not in 
            // secondaryLetters), return false, as the word is invalid.
            return false;
        }

        // If every letter of the word is valid, return true
        return true;
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

    private HelpData calculateHelpData() {
        int numWords = validWords.size();

        long numPangrams = validWords.parallelStream()
            .filter(this::isPangram)
            .count();
        
        long numPerfectPangrams = validWords.parallelStream()
            .filter(this::isPangram)
            .filter(s -> s.length() == 7)
            .count();
        
        Map<Pair<Character, Integer>, Long> grid = validWords.parallelStream()
            // maps each word to a pair (char, int) representing
            // (first letter, word length)
            .map(s -> new ImmutablePair<>(s.charAt(0), s.length()))
            .collect(Collectors.groupingBy(
                // Groups (collapses elements into groups) by the identity
                // function. Each distinct pair is its own group.
                Function.identity(),
                // Maps each group to the number of elements in it.
                // Here, the number of words with that starting letter and
                // length.
                Collectors.counting()
            ));
        
        Map<String, Long> letterLists = validWords.parallelStream()
            // Maps each word down to just its first two letters
            .map(s -> s.substring(0, 2))
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ));

        return new HelpData(
            numWords,
            totalPoints,
            numPangrams,
            numPerfectPangrams,
            grid,
            letterLists
        );
    }
}
