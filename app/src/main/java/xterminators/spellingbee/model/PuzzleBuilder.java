package xterminators.spellingbee.model;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Puzzle builder for creating Puzzles from a single standard interface.
 */
public class PuzzleBuilder {
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
        // TODO: Implement constructor
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
        // TODO: Implement setRootWord
        return false;
    }

    /**
     * Sets the builder to build a puzzle with the given required letter.
     * Returns whether or not the letter is a valid required letter based on the
     * root word. If the letter is not a valid required letter, no action is
     * taken. This function assumes a root word is set.
     *
     * @param requiredLetter the required letter to be used
     * @return if the required letter is valid for the root word
     */
    public boolean setRequiredLetter(char requiredLetter) {
        // TODO: Implement setRequiredLetter
        return false;
    }

    /**
     * Sets the builder to build a puzzle with the given root word and required
     * letter. Returns whether or not the pair is a valid start for a puzzle. If
     * the pair is not a valid start for a puzzle, not action is taken. If the
     * pair is a valid start for a puzzle, this is equivalent to setRoot(root) &&
     * setRequiredLetter(requiredLetter).
     *
     * @param root the root word to be used
     * @param requiredLetter the required letter to be used
     * @return if the pair of root and requiredLetter is a valid start for a
     *         puzzle
     */
    public boolean setRootAndRequiredLetter(String root, char requiredLetter) {
        // TODO: Implement setRootAndRequiredLetter
        return false;
    }

    /**
     * Clears the current state of the puzzle builder, including any set root
     * word or required letter.
     */
    public void clear() {
        // TODO: Implement clear
    }

    /**
     * Clears the current set root word, if one exists. This also clears any set
     * required letter.
     */
    public void clearRootWord() {
        // TODO: Implement clearRootWord
    }

    /**
     * Clears the current set required letter, if one exists.
     */
    public void clearRequiredLetter() {
        // TODO: Implement clearRequiredLetter
    }

    /**
     * Builds and returns a Puzzle object based on the current state of the
     * puzzle builder.
     *
     * @return the built Puzzle object
     */
    public Puzzle build() {
        // TODO: Implement build
        return null;
    }
}

