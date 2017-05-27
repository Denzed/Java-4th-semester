package ru.spbau.daniil.smirnov.myftp.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.daniil.smirnov.myftp.exceptions.MyFTPException;
import ru.spbau.daniil.smirnov.myftp.server.Server;
import ru.spbau.daniil.smirnov.myftp.server.actions.GetFileAction;
import ru.spbau.daniil.smirnov.myftp.server.actions.ListDirectoryAction;
import ru.spbau.daniil.smirnov.myftp.utils.ChannelByteReader;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * Client to interact with an instance of {@link Server}
 */
public class Client {
    private static final long TIMEOUT = 2000;

    private final int port;
    private final String serverAddress;

    /**
     * Constructs a client to interact with the server on a given port
     * @param serverAddress address of the server to connect to
     * @param port port of the server
     */
    public Client(@NotNull String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public Client(int port) {
        this("localhost", port);
    }

    /**
     * Sends a request to the server to list the directory at the given path
     * @param path path to directory
     * @return files in the directory or {@code null} in case of failure
     * @throws IOException if an I/O error occurs
     */
    @Nullable
    public List<ListDirectoryAction.ListActionResultEntry> list(@NotNull String path)
            throws IOException {
        byte[] response = sendRequestWithCodeAndArg(ListDirectoryAction.ACTION_CODE, path);
        return response.length == 0 ? null : ListDirectoryAction.fromBytes(response);
    }

    /**
     * Sends a request to the server to get file contents at the given path
     * @param path path to file
     * @return file contents if success and an empty {@code byte[]} otherwise
     * @throws IOException if an I/O error occurs
     */
    @NotNull
    public byte[] get(@NotNull String path) throws IOException, MyFTPException {
        return GetFileAction.fromBytes(sendRequestWithCodeAndArg(GetFileAction.ACTION_CODE, path));
    }

    @NotNull
    private byte[] sendRequestWithCodeAndArg(int code, @NotNull String arg) throws IOException {
        SocketChannel channel = openChannel();
        byte[] bytes;
        try (
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            outputStream.writeInt(code);
            outputStream.writeUTF(arg);
            outputStream.flush();
            bytes = byteStream.toByteArray();
        }
        writeBytesToChannel(bytes, channel);
        byte[] response = readBytesFromChannel(channel);
        channel.close();
        return response;
    }

    @NotNull
    private SocketChannel openChannel() throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        try (Selector selector = Selector.open()) {
            channel.register(selector, SelectionKey.OP_CONNECT);
            @NotNull
            final InetAddress inetAddress = (serverAddress.equals("localhost")
                ? InetAddress.getLocalHost()
                : InetAddress.getByName(serverAddress));
            channel.connect(new InetSocketAddress(inetAddress, port));
            int selected = selector.select(TIMEOUT);
            if (selected == 0 || !channel.finishConnect()) {
                throw new SocketTimeoutException("Could not connect to server in " + TIMEOUT + "ms");
            }
            return channel;
        }
    }

    private void writeBytesToChannel(@NotNull byte[] bytes, @NotNull SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        channel.shutdownOutput();
    }

    @NotNull
    private byte[] readBytesFromChannel(@NotNull SocketChannel channel) throws IOException {
        ChannelByteReader reader = new ChannelByteReader();
        int bytesRead = reader.read(channel);
        while (bytesRead != -1) {
            bytesRead = reader.read(channel);
        }
        return reader.getData();
    }
}
