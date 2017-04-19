package ru.spbau.daniil.smirnov.mygit.objects;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class which stores information about commit: hash of the root {@link Tree} object, message, author, date of creation
 * and parent commits' hashes
 */
public class Commit implements Serializable, Comparable<Commit> {
    /**
     * String constant used to distinguish Commits from other MyGit objects
     */
    public static final String TYPE = "commit";

    @NotNull
    private final String rootTreeHash;

    @NotNull
    private final String commitMessage;

    @NotNull
    private final String commitAuthor;

    @NotNull
    private final Date creationDate;

    @NotNull
    private final List<String> parentCommitsHashes;

    private Commit(@NotNull String rootTreeHash,
                   @NotNull String commitMessage,
                   @NotNull String commitAuthor,
                   @NotNull Date creationDate,
                   @NotNull List<String> parentCommitsHashes) {
        this.rootTreeHash = rootTreeHash;
        this.commitMessage = commitMessage;
        this.commitAuthor = commitAuthor;
        this.creationDate = creationDate;
        this.parentCommitsHashes = parentCommitsHashes;
    }

    /**
     * Constructs Commit with the given root tree hash, default message ("initial commit"), current user as author,
     * current date as creation date and empty parent list
     * @param rootTreeHash root tree hash
     */
    public Commit(@NotNull String rootTreeHash) {
        this(rootTreeHash, "initial commit", new ArrayList<>());
    }

    /**
     * Constructs Commit with the given root tree hash, given message, current user as author,
     * current date as creation date and given parent list
     * @param rootTreeHash root tree hash
     * @param message commit message
     * @param parentCommitsHashes parent commits' hashes
     */
    public Commit(@NotNull String rootTreeHash, @NotNull String message, @NotNull List<String> parentCommitsHashes) {
        this(rootTreeHash, message, getUsername(), new Date(), parentCommitsHashes);
    }

    /**
     * Compares two Commits. Order of comparison is: creationDate, commitAuthor, commitMessage, rootTreeHash, parentCommitsHashes
     * @param other Commit to compare with
     * @return negative integer if current commit is less than the other, zero if equal, positive if greater
     */
    @Override
    public int compareTo(@NotNull Commit other) {
        int result = creationDate.compareTo(other.creationDate);
        if (result != 0) {
            return result;
        }
        result = commitAuthor.compareTo(other.commitAuthor);
        if (result != 0) {
            return result;
        }
        result = commitMessage.compareTo(other.commitMessage);
        if (result != 0) {
            return result;
        }
        result = rootTreeHash.compareTo(other.rootTreeHash);
        if (result != 0) {
            return result;
        }
        result = Integer.valueOf(parentCommitsHashes.size()).compareTo(other.parentCommitsHashes.size());
        if (result != 0) {
            return result;
        }
        for (int i = 0; i < parentCommitsHashes.size(); i++) {
            result = parentCommitsHashes.get(i).compareTo(other.parentCommitsHashes.get(i));
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    /**
     * Gets the root tree hash of the commit
     * @return root tree hash
     */
    @NotNull
    public String getRootTreeHash() {
        return rootTreeHash;
    }

    /**
     * Gets the commit message
     * @return commit message
     */
    @NotNull
    public String getCommitMessage() {
        return commitMessage;
    }

    /**
     * Gets the commit author
     * @return commit author
     */
    @NotNull
    public String getCommitAuthor() {
        return commitAuthor;
    }

    /**
     * Gets the commit creation date
     * @return creation date
     */
    @NotNull
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Gets the {@link List} of parent commits' hashes
     * @return parent commits' hashes
     */
    @NotNull
    public List<String> getParentCommitsHashes() {
        return parentCommitsHashes;
    }


    /**
     * Generated method used to check equality between Commit and other objects
     * @param object object to check equality with
     * @return {@code true} if the given object is a {@link Commit} with the same field values and {@code false} otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Commit commit = (Commit) object;

        return compareTo(commit) == 0;
    }

    /**
     * Generated method used to hash Commit objects
     * @return computed hash of the Commit
     */
    @Override
    public int hashCode() {
        int result = getRootTreeHash().hashCode();
        result = 31 * result + getCommitMessage().hashCode();
        result = 31 * result + getCommitAuthor().hashCode();
        result = 31 * result + getCreationDate().hashCode();
        result = 31 * result + getParentCommitsHashes().hashCode();
        return result;
    }

    @NotNull
    private static String getUsername() {
        return System.getProperty("user.name");
    }
}