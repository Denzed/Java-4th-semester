package mygit.objects;

/**
 * Class which represents an update state of a file, namely:
 * modified, deleted, unstaged, staged
 */
public enum FileStatus {
    MODIFIED("modified"),
    DELETED("deleted"),
    UNSTAGED("unstaged"),
    STAGED("staged");

    private final String state;

    FileStatus(String state) {
        this.state = state;
    }

    /**
     * Gets the String representation of file status
     *
     * @return String corresponding to current file status
     */
    public String getState() {
        return state;
    }
}
