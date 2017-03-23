package mygit.objects;

/**
 * Enum for {@link FileDifference} staging status of a file
 */
enum FileDifferenceStageStatus {
    /**
     * Next commit will replace file copy in HEAD with its current contents
     */
    TO_BE_COMMITTED,
    /**
     * Next commit will ignore changes in the current file
     */
    NOT_STAGED_FOR_COMMIT,
    /**
     * HEAD does not have current file in it
     */
    UNTRACKED
}
