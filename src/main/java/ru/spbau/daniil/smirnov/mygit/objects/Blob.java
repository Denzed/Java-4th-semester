package ru.spbau.daniil.smirnov.mygit.objects;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Class which stores file contents without any additional information
 */
public class Blob implements Serializable {
    /**
     * String constant used to distinguish Blobs from other MyGit objects
     */
    public static final String TYPE = "blob";

    @NotNull
    private final byte[] contents;

    public Blob(@NotNull byte[] contents) {
        this.contents = contents;
    }

    /**
     * Gets file contents
     * @return contents of a file stored by the Blob
     */
    @NotNull
    public byte[] getContents() {
        return contents;
    }
}