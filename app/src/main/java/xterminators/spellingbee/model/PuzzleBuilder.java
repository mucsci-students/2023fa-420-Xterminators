package xterminators.spellingbee.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.random.RandomGenerator;

/**
 * Puzzle builder for creating Puzzles from a single standard interface.
 */
public class PuzzleBuilder {
    private File fullDictionary;
    private File rootsDictionary;

    private String rootWord;
    private char requiredLetter;

    /**
     * Creates a new PuzzleBuilder object.
     *
     * @param fullDictionary the location of the full dictionary file
     * @param rootsDictionary the location of the root words dictionary file
     * @throws FileNotFoundException if either dictionary file does not exist
     */
    public PuzzleBuilder(File fullDictionary, File rootsDictionary)
        throws FileNotFoundException
    {
        if (!fullDictionary.exists()) {
            throw new FileNotFoundException(
                "The full dictionary (" + fullDictionary.getAbsolutePath() + ") " +
                "does not exist."
            );
        }

        if (!rootsDictionary.exists()) {
            throw new FileNotFoundException(
                "The root dictionary (" + rootsDictionary.getAbsolutePath() + ") " +
                "does not exist."
            );
        }

        this.fullDictionary = fullDictionary;
        this.rootsDictionary = rootsDictionary;

        this.rootWord = null;
        this.requiredLetter = '\0';
    }

    /**
     * Sets the builder to build a puzzle with the given root word. Returns
     * whether or not the word is a valid root word. If the root word is not a
     * valid root word, no action is taken. This will also clear any set
     * required letter, if valid.
     *
     * @param root the root word to be used
     * @return if the root word is a legal root word
     */
    public boolean setRootWord(String root) {
        if (root == null || root.length() < Puzzle.NUMBER_UNIQUE_LETTERS) {
            return false;
        }

        try {
            FileReader rootsFileReader = new FileReader(rootsDictionary);
            BufferedReader rootsReader = new BufferedReader(rootsFileReader);

            boolean rootIsValid = rootsReader.lines()
                .anyMatch(root::equals);

            rootsReader.close();
            rootsFileReader.close();

            if (rootIsValid) {
                this.rootWord = root;
            }

            return rootIsValid;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sets the builder to build a puzzle with the given root word and required
     * letter. Returns whether or not the pair is a valid start for a puzzle. If
     * the pair is not a valid start for a puzzle, not action is taken.
     *
     * @param root the root word to be used
     * @param requiredLetter the required letter to be used
     * @return if the pair of root and requiredLetter is a valid start for a
     *         puzzle
     */
    public boolean setRootAndRequiredLetter(String root, char requiredLetter) {
        if (root == null || root.indexOf(requiredLetter) == -1) {
            return false;
        }

        if (!setRootWord(root)) {
            return false;
        }

        this.requiredLetter = requiredLetter;
        
        return true;
    }

    /**
     * Builds and returns a Puzzle object based on the current state of the
     * puzzle builder. If no root word is set, a random one will be used. If no
     * required letter is set a random one will be used. A random seed will be
     * used for any random choices.
     *
     * @return the built Puzzle object
     */
    public Puzzle build() {
        // TODO: Implement build
        return null;
    }

    /**
     * Builds and returns a Puzzle object based on the current state of the
     * puzzle builder. If no root word is set, a random one will be used. If no
     * required letter is set a random one will be used.
     * 
     * @param rng the random number generator to be used for making any random
     *            choices
     * @return the built Puzzle object
     */
    public Puzzle build(RandomGenerator rng) {
        // TODO: Implement build(seed)
        return null;
    }
}

