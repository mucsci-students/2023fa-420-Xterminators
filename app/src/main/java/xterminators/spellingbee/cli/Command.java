package xterminators.spellingbee.cli;

import java.util.Optional;

/**
 * Enum representing commands for the Spelling Bee CLI. Each command has a 
 * keyword, a short help description, and a long help description.
 */
public enum Command {
    /** The command to exit the CLI. */
    EXIT(
        "exit",
        "Exits the game.",
        """
        Exits the game. This command will not save the puzzle before exiting. To
        save see the "save" command."""
    ),
    /** The command to display all found words. */
    FOUND_WORDS(
        "found",
        "Shows the currently found words of the puzzle.",
        "Shows the currently found words of the puzzle."
    ),
    /** The command to guess a word. */
    GUESS(
        "guess",
        "Guesses words for the puzzle.",
        """
        Guesses words for the puzzle. Requires at least one word to guess. Can
        be used with as many words as wanted."""
    ),
    /** The command to display command help. */
    HELP(
        "help",
        "Shows help for commands.",
        """
        Shows help for commands. Add a command name after it for more detailed
        help with that command."""
    ),
    /** The command to load a puzzle from a file. */
    LOAD(
        "load",
        "Loads a puzzle from a file.",
        """
        Loads a puzzle from a file. Requires one argument containing the path to
        to the file to load from."""
    ),
    /** The command to create a new puzle. */
    NEW(
        "new",
        "Creates a new puzzle.",
        """
        Creates a new puzzle at random or from a given word and letter. If a
        a random word is wanted, do not add any parameters. If a puzle is wanted
        from a word, the first argument should be a pangram, and the second
        argument should be the required letter for the puzzle."""
    ),
    /** The command to display ranks and current rank. */
    RANK(
        "rank",
        "Shows the rank for the puzzle.",
        """
        Shows all of the ranks for the puzzle, along with the necessary points
        to achieve each rank. The current rank of the puzzle is given with a
        star."""
    ),
    /** The command to save the puzzle. */
    SAVE(
        "save",
        "Saves the puzzle to a file.",
        """
        Saves the puzzle to a file. If the first argument is either "encrypted"
        or "unencrypted", the puzzle will be saved in the corresponding format
        in a default location. If the first argument is a path, the puzzle will
        be saved to that path. If no second argument is given, the puzzle will
        be encrypted. If the second argument is "encrypted" or "unencrypted",
        the puzzle will be saved in the corresponding format. If no arguments
        are given, the puzzle will be saved in the default location in an
        encrypted format."""
    ),
    /** The command to display the puzzle. */
    SHOW(
        "show",
        "Displays the puzzle.",
        """
        Displays the puzzle. Shows the current puzzle with the required letter
        in the center of the hexagon. Also shows the current rank and points of
        the puzzle. To reorder the letter in the hexagon, see the "shuffle"
        command."""
    ),
    /** The command to shuffle the puzzle display. */
    SHUFFLE(
        "shuffle",
        "Shuffles the letters in the puzzle.",
        """
        Shuffles the order of the non-required letters in the display of the puzzle."""
    ),
    HINT(
        "hint",
        "Displays hints for the current puzzle.",
        """
        Takes the puzzle and compiles a grid to show the relationship between words
        and their length.
                """
    );

    /** The keyword for the command. */
    public final String keyword;
    /** A short description of the command. */
    public final String shortHelp;
    /** The full description of the command and how to use it. */
    public final String longHelp;

    /**
     * Initilizes a Command object.
     * 
     * @param command The keyword for the command
     * @param shortHelp The short help for the command
     * @param longHelp The long help for the command
     */
    Command(String keyword, String shortHelp, String longHelp) {
        this.keyword = keyword;
        this.shortHelp = shortHelp;
        this.longHelp = longHelp;
    }

    public static Optional<Command> fromString(String string) {
        for (Command command : values()) {
            if (command.keyword.equals(string)) {
                return Optional.of(command);
            }
        }

        return Optional.empty();
    }
}
