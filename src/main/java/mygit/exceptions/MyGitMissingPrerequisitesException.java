package mygit.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception which occurs when user tries to execute command which cannot be done in a current state of a repository
 */
public class MyGitMissingPrerequisitesException extends MyGitException {
    /**
     * Constructs an exception with the given message
     * @param message non-null message with additional information
     */
    public MyGitMissingPrerequisitesException(@NotNull String message) {
        super(message);
    }
}
