package ru.spbau.smirnov.daniil.tictactoe.net.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;

/**
 * Utility class used to read chunks of data from channel without locks
 */
public class ChannelByteReader {
    private static final int BUFFER_SIZE = 256;

    @NotNull
    private final ByteBuffer buffer;

    @NotNull
    private byte[] data;

    private int position;

    /**
     * Constructs a reader with default buffer size = {@value BUFFER_SIZE}.
     */
    public ChannelByteReader() {
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.data = new byte[BUFFER_SIZE];
        this.position = 0;
    }

    /**
     * Reads a chunk of data from {@link ByteChannel} of size not more than {@value BUFFER_SIZE} assuming that the
     * channel is in non-blocking mode
     * @param channel channel to read from
     * @return number of bytes read; {@code -1} if EOF is reached
     * @throws IOException if an I/O error occurs
     */
    public int read(@NotNull ByteChannel channel) throws IOException {
        int bytesRead = channel.read(buffer);
        if (bytesRead == -1) {
            return -1;
        }
        while (position + bytesRead > data.length) {
            byte[] newData = new byte[data.length * 2];
            System.arraycopy(data, 0, newData, 0, position);
            data = newData;
        }
        buffer.flip();
        buffer.get(data, position, bytesRead);
        position += bytesRead;
        buffer.clear();
        return bytesRead;
    }

    /**
     * Should be called after EOF was reached. Returns concatenated byte chunks that was read.
     *
     * @return concatenated byte array
     */
    @NotNull
    public byte[] getData() {
        return Arrays.copyOf(data, position);
    }
}