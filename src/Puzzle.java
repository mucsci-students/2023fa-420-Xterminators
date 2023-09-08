import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Puzzle {
    private String seedWord;
    private char[] letters;
    private ArrayList<String> validWords;
    private ArrayList<String> foundWords;
    private int totalPoints;
    private int earnedPoints;
    private int[] rankPoints;

    public char[] getLetters() {
        // returns a copy so that letters can not be modified externaly
        return Arrays.copyOf(this.letters, this.letters.length);
    }

    public List<String> getFoundWords() {
        // returns a read-only view of foundWords
        return Collections.unmodifiableList(this.foundWords);
    }

    public int getTotalPoints() {
        return this.totalPoints;
    }

    public int getEarnedPoints() {
        return this.earnedPoints;
    }
}
