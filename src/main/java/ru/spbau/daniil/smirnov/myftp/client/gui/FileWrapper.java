package ru.spbau.daniil.smirnov.myftp.client.gui;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Wrapper for {@link File} class with custom {@link File#toString} method
 */
class FileWrapper extends File {
    private boolean isRootNode;

    FileWrapper(@NotNull String string, boolean isRootNode) {
        super(string);
        this.isRootNode = isRootNode;
    }

    FileWrapper(@NotNull String s) {
        this(s, false);
    }

    /**
     * Returns the full path to file if it is the root directory and just the file name otherwise
     * @return the resulting string
     */
    @Override
    @NotNull
    public String toString() {
        return isRootNode ? super.toString() : getName();
    }
}
