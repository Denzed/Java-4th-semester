package mygit.objects;

/**
 * Enum for {@link FileDifference} type of difference between a file in repository HEAD and in filesystem
 */
public enum FileDifferenceType {
    /**
     * File has been added
     */
    ADDITION,
    /**
     * File has been removed
     */
    REMOVAL,
    /**
     * File contents have been modified
     */
    MODIFICATION
}