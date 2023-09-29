package xterminators.spellingbee.utils;

import java.util.Arrays;

import org.mockito.ArgumentMatcher;

/**
 * Custom Mockito argument matcher for matching char arrays disregarding element
 * order.
 */
public class CharArrayOrderlessMatcher implements ArgumentMatcher<char[]> {
    char[] expectedArr;

    /**
     * Constructs a new CharArrayOrderlessMatcher with the expected char array.
     *
     * @param expectedArr The expected char array to match against
     */
    public CharArrayOrderlessMatcher(char[] expectedArr) {
        this.expectedArr = expectedArr;
        Arrays.sort(this.expectedArr);
    }

    /**
     * Matches the given char array against the expected char array disregarding
     * element order.
     *
     * @param argument The char array to check for match
     * @return true if the argument contains the same characters as the expected
     *         array (disregarding order),
     *         false otherwise
     */
    @Override
    public boolean matches(char[] argument) {
        if(argument == null || argument.length != expectedArr.length) {
            return false;
        }

        for (char c : argument) {
            if (Arrays.binarySearch(this.expectedArr, c) < 0) {
                return false;
            }
        }

        return true;
    }
}