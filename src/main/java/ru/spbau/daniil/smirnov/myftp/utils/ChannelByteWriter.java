package ru.spbau.daniil.smirnov.myftp.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/**
 * Utility class used to write data to channel without locks
 */
public class ChannelByteWriter {
    @NotNull
    private final ByteBuffer buffer;

    /**
     * Constructs a writer with the given data
     * @param data data to be written
     */
    public ChannelByteWriter(@NotNull byte[] data) {
        this.buffer = ByteBuffer.wrap(data);
    }

    /**
     * Writes data to the channel.
     * @param channel channel to write to
     * @return number of bytes written; -1 if there were no more bytes to write
     * @throws IOException if an I/O error occurs
     */
    public int write(@NotNull ByteChannel channel) throws IOException {
        if (buffer.hasRemaining()) {
            return channel.write(buffer);
        } else {
            return -1;
        }
    }
}
