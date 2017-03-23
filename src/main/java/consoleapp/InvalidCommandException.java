package consoleapp;

import org.jetbrains.annotations.NotNull;

/**
 * Exception which occurs when user passes a wrong command or executes it with invalid arguments
 */
public class InvalidCommandException extends Exception {
    /**
     * Constructs an exception with the given message
     * @param message non-null message with additional information
     */
    public InvalidCommandException(@NotNull String message) {
        super(message);
    }
}
