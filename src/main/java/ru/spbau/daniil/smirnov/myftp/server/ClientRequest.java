package ru.spbau.daniil.smirnov.myftp.server;

import org.jetbrains.annotations.NotNull;

import java.nio.channels.SelectionKey;

/**
 * Contains requests from clients
 */
class ClientRequest {
    @NotNull
    private final SelectionKey key;

    @NotNull
    private final byte[] content;

    ClientRequest(@NotNull SelectionKey key, @NotNull byte[] content) {
        this.key = key;
        this.content = content;
    }

    @NotNull
    SelectionKey getKey() {
        return key;
    }

    @NotNull
    byte[] getContent() {
        return content;
    }
}
