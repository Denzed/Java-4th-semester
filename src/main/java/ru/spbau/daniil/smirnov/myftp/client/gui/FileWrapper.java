package ru.spbau.daniil.smirnov.myftp.client.gui;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Wrapper for {@link File} class to redefine its {@code toString} method
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

    FileWrapper(@NotNull File file) {
        super(file.toString());
    }

    @Override
    @NotNull
    public String toString() {
        return isRootNode ? super.toString() : getName();
    }
}
