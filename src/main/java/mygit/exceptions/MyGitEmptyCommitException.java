package mygit.exceptions;

/**
 * Exception which occurs when user tries to commit with no staged changes
 */
public class MyGitEmptyCommitException extends MyGitException {
    public MyGitEmptyCommitException() {
        super("No changes to commit");
    }
}
