package xterminators.spellingbee.cli;

import java.util.Arrays;
import java.util.List;

import xterminators.spellingbee.model.Rank;
import xterminators.spellingbee.ui.View;

/**
 * The view of the CLI mode of the Spelling Bee game. This class takes
 * instructions and data from the controller and displays output accordingly.
 */
public class CLIView extends View {
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    /**
     * Displays the given list of found words for the puzzle.
     * 
     * @param foundWords The list of found words
     */
    public void showFoundWords(List<String> foundWords) {
        if (foundWords.isEmpty()) {
            System.out.println("You have not found any words yet.");
        } else if (foundWords.size() == 1) {
            System.out.println("You have found 1 word:");
        } else {
            System.out.println("You have found " + foundWords.size() + " words:");
        }

        foundWords.forEach(System.out::println);
    }

    /**
     * Displays the result of guessing the given word, given the points earned.
     * 
     * @param word The word guesses
     * @param points The points earned from the word
     */
    public void showGuess(String word, int points) {
        if (points == -1) {
            System.out.println(
                "You already found the word \"" + word + "\". Try again."
            );
        } else if (points == 0) {
            System.out.println(
                "The word \"" + word + "\" is not a word in the puzzle. Try again."
            );
        } else {
            System.out.println(
                "You found \"" + word + "\". You earned " + points +" points."
            );
        }
    }

    /**
     * Displays the general help for all commands.
     */
    public void showHelp() {
        for (Command c : Command.values()) {
            System.out.println(c.keyword + ": " + c.shortHelp);
        }
    }

    /**
     * Displays the full help for a given command.
     * 
     * @param command The command to display help for
     */
    public void showHelp(Command command) {
        System.out.println(command.longHelp);
    }

    /**
     * Displays the given status message to the user.
     * 
     * @param message The status message to be displayed
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays the given error message to the user.
     * 
     * @param message The error message to be displayed
     */
    public void showErrorMessage(String message) {
        System.out.println(ANSI_BOLD + ANSI_RED + message + ANSI_RESET);
    }

    /**
     * Displays the puzzle to the user along with the rank and current points of
     * the puzzle.
     * 
     * @param primaryLetter The required letter of the puzzle
     * @param secondaryLetters The secondary letters of the puzzle
     * @param rank The current rank of the puzzle
     * @param points The current number of earned points
     */
    public void showPuzzle(char primaryLetter, char[] secondaryLetters,
                           Rank rank, int points)
    {
        StringBuilder puzzleHex = new StringBuilder();
        puzzleHex.append("    +---+")
            .append(System.lineSeparator());
        puzzleHex.append("+---+ %c +---+".formatted(secondaryLetters[0]))
            .append(System.lineSeparator());
        puzzleHex.append("| %c +---+ %c |".formatted(
                secondaryLetters[5], secondaryLetters[1]
            )).append(System.lineSeparator());
        puzzleHex.append("+---+ %c +---+".formatted(primaryLetter))
            .append(System.lineSeparator());
        puzzleHex.append("| %c +---+ %c |".formatted(
                secondaryLetters[4], secondaryLetters[2]
            )).append(System.lineSeparator());
        puzzleHex.append("+---+ %c +---+".formatted(secondaryLetters[3]))
            .append(System.lineSeparator());
        puzzleHex.append("    +---+")
            .append(System.lineSeparator());
        
        System.out.println(puzzleHex);
        System.out.println("Current Rank  : " + rank.getRankName());
        System.out.println("Current Points: " + points);
    }

    /**
     * Displays all ranks to the user with the number of points needed to earn
     * each rank. Also highlights the current ranks of the puzzle.
     * 
     * @param rank The current rank of the puzzle
     * @param earnedPoints The current number of earned points
     * @param totalPoints The number of total possible points in the puzzle
     */
    public void showRanks(Rank rank, int earnedPoints, int totalPoints) {
        if (earnedPoints == 1) {
            System.out.println(
                "Current Rank: " + rank.getRankName() + " - " +
                earnedPoints + " point" + System.lineSeparator()
            );
        } else {
            System.out.println(
                "Current Rank: " + rank.getRankName() + " - " +
                earnedPoints + " points" + System.lineSeparator()
            );
        }

        int maxNameLength = Arrays.stream(Rank.values())
                                  .map(Rank::getRankName)
                                  .mapToInt(String::length)
                                  .max().orElseThrow();

        int maxPointsLength = Arrays.stream(Rank.values())
                                    .map(r -> r.getRequiredPoints(totalPoints))
                                    .map(String::valueOf)
                                    .mapToInt(String::length)
                                    .max().orElseThrow();

        for (Rank r : Rank.values()) {
            int reqPoints = r.getRequiredPoints(totalPoints);
            String reqPointsStr = String.valueOf(reqPoints);
            String asterisk = r.equals(rank) ? "*" : " ";
        
            System.out.println(
                asterisk + padLeft(r.getRankName(), maxNameLength) + " - " +
                padLeft(reqPointsStr, maxPointsLength) + " point" +
                (reqPoints == 1 ? "" : "s") + " minimum"
            );
        }               
    }

    /**
     * Pads the given string on the right with spaces and returns the result.
     * If the given string is null, it will be returned unchanged.
     * 
     * @param str The string to pad.
     * @param totalLength The total length that the resulting string should be.
     * @return The padded string.
     */
    private String padLeft(String str, int totalLength) {
        if (str == null) {
            return str;
        }
        return String.format("%1$-" + totalLength + "s", str);
    }
}