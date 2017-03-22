package mygit.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception which occurs when user tries to execute command which cannot be done in a current state of a repository
 */
public class MyGitMissingPrerequisitiesException extends MyGitException {
    /**
     * Constructs an exception with the given message
     * @param message non-null message with additional information
     */
    public MyGitMissingPrerequisitiesException(@NotNull String message) {
        super(message);
    }
}
