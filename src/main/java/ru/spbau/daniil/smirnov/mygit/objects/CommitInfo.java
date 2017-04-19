package ru.spbau.daniil.smirnov.mygit.objects;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * User-accessible information about commit
 */
public class CommitInfo {
    @NotNull
    private String revisionHash;

    @NotNull
    private String message;

    @NotNull
    private String author;

    @NotNull
    private Date creationDate;

    /**
     * Constructs CommitInfo with given revision hash, message, author and creaion date
     * @param revisionHash hash of revision
     * @param message commit message
     * @param author commit author
     * @param creationDate date of creation
     */
    public CommitInfo(@NotNull String revisionHash,
                      @NotNull String message,
                      @NotNull String author,
                      @NotNull Date creationDate) {
        this.revisionHash = revisionHash;
        this.message = message;
        this.author = author;
        this.creationDate = creationDate;
    }

    /**
     * Gets hash of commit's revision
     * @return hash of revision
     */
    @NotNull
    public String getRevisionHash() {
        return revisionHash;
    }

    /**
     * Gets commit message
     * @return commit message
     */
    @NotNull
    public String getMessage() {
        return message;
    }

    /**
     * Gets commit author
     * @return commit author
     */
    @NotNull
    public String getAuthor() {
        return author;
    }

    /**
     * Gets commit creation date
     * @return commit creation date
     */
    @NotNull
    public Date getCreationDate() {
        return creationDate;
    }
}
