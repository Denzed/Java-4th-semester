package mygit.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception which occurs when user passes a wrong argument to the library
 */
public class MyGitIllegalArgumentException extends MyGitException {
    /**
     * Constructs an exception with the given message
     * @param message non-null message with additional information
     */
    public MyGitIllegalArgumentException(@NotNull String message) {
        super(message);
    }
}
