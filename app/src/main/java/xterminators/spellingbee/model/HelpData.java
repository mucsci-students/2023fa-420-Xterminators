package xterminators.spellingbee.model;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

/**
 * A record containing all the data needed for displaying hints for a puzzle.
 * 
 * @param numWords The number of words in the puzzle
 * @param totalPoints The total number of points that can be earned in the
 *                    puzzle.
 * @param numPangrams The number of pangrams in the puzzle
 * @param numPerfectPangrams The number of perfect pangrams (pangram with length
 *                           7) in the puzzle.
 * @param startingLetterGrid A map that has keys that are a pair of a starting
 *                           letter and a word length, and values of the number
 *                           of words that have that starting letter and length.
 * @param startingLetterPairs A map mapping a string of two letter to the number
 *                            of words that start with those two letters (in
 *                            order).
 */
public record HelpData(
    int numWords,
    int totalPoints,
    int numPangrams,
    int numPerfectPangrams,
    Map<Pair<Character, Integer>, Integer> startingLetterGrid,
    Map<String, Integer> startingLetterPairs
) {}
