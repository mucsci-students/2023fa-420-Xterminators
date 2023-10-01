package xterminators.spellingbee.cli;

import java.util.Optional;

/**
 * Enum representing commands for the Spelling Bee CLI. Each command has a 
 * keyword, a short help description, and a long help description.
 */
public enum Command {
    /** The command to exit the CLI. */
    EXIT("exit", "", ""),
    /** The command to display all found words. */
    FOUND_WORDS("found", "", ""),
    /** The command to guess a word. */
    GUESS("guess", "", ""),
    /** The command to display command help. */
    HELP("help", "", ""),
    /** The command to load a puzzle from a file. */
    LOAD("load", "", ""),
    /** The command to create a new puzle. */
    NEW("new", "", ""),
    /** The command to display ranks and current rank. */
    RANK("rank", "", ""),
    /** The command to save the puzzle. */
    SAVE("save", "", ""),
    /** The command to display the puzzle. */
    SHOW("show", "", ""),
    /** The command to shuffle the puzzle display. */
    SHUFFLE("shuffle", "", "");

    /** The keyword for the command. */
    private String command;
    /** A short description of the command. */
    private String shortHelp;
    /** The full description of the command and how to use it. */
    private String longHelp;

    /**
     * Initilizes a Command object.
     * 
     * @param command The keyword for the command
     * @param shortHelp The short help for the command
     * @param longHelp The long help for the command
     */
    Command(String command, String shortHelp, String longHelp) {
        this.command = command;
        this.shortHelp = shortHelp;
        this.longHelp = longHelp;
    }

    /**
     * Gets keyword to invoke the command.
     * 
     * @return The keyword to invoke the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Gets the short help for the command. Gives only basic information on how
     * to use the command.
     * 
     * @return The short help of the command
     */
    public String getShortHelp() {
        return shortHelp;
    }

    /**
     * Gets the long help for the command. Gives full description of the command 
     * and how to use it.
     * 
     * @return The long help of the command
     */
    public String getLongHelp() {
        return longHelp;
    }

    public static Optional<Command> fromString(String string) {
        for (Command command : values()) {
            if (command.getCommand().equals(string)) {
                return Optional.of(command);
            }
        }

        return Optional.empty();
    }
}
