package mygit.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception which occurs when user incorrectly uses MyGit
 */
public class MyGitIllegalStateException extends MyGitException {
    /**
     * Constructs an exception with the given message
     * @param message non-null message with additional information
     */
    public MyGitIllegalStateException(@NotNull String message) {
        super(message);
    }
}

