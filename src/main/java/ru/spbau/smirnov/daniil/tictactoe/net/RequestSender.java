package ru.spbau.smirnov.daniil.tictactoe.net;

import org.jetbrains.annotations.NotNull;
import ru.spbau.smirnov.daniil.tictactoe.net.actions.MakeTurnAction;
import ru.spbau.smirnov.daniil.tictactoe.net.actions.RegisterAction;
import ru.spbau.smirnov.daniil.tictactoe.net.utils.ChannelByteReader;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Sends requests to server
 */
public class RequestSender {
    private static long TIMEOUT = 2000;


    private final int port;

    /**
     * Constructs a RequestSender to interact with the Server on a given port
     * @param port port of the Server
     */
    public RequestSender(int port) {
        this.port = port;
    }

    private static void writeBytesToChannel(@NotNull byte[] bytes, @NotNull SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        channel.shutdownOutput();
    }

    @NotNull
    private static byte[] readBytesFromChannel(@NotNull SocketChannel channel) throws IOException {
        ChannelByteReader reader = new ChannelByteReader();
        int bytesRead = reader.read(channel);
        while (bytesRead != -1) {
            bytesRead = reader.read(channel);
        }
        return reader.getData();
    }

    public boolean sendRegisterRequest() {
        SocketChannel channel;
        byte[] response;
        try (
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            channel = openChannel();
            byte[] bytes;
            outputStream.writeInt(RegisterAction.ACTION_CODE);
            outputStream.flush();
            bytes = byteStream.toByteArray();
            writeBytesToChannel(bytes, channel);
            response = readBytesFromChannel(channel);
        } catch (IOException e) {
            return false;
        }
        boolean result;
        try (
                ByteArrayInputStream byteStream = new ByteArrayInputStream(response);
                DataInputStream inputStream = new DataInputStream(byteStream)) {
            int registered = inputStream.readInt();
            result = (registered == 1);
            channel.close();
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    public void sendTurnRequest(int row, int column) {
        SocketChannel channel;
        byte[] bytes;
        try (
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            channel = openChannel();
            outputStream.writeInt(MakeTurnAction.ACTION_CODE);
            outputStream.writeInt(row);
            outputStream.writeInt(column);
            outputStream.flush();
            bytes = byteStream.toByteArray();
            writeBytesToChannel(bytes, channel);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private SocketChannel openChannel() throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        try (Selector selector = Selector.open()) {
            channel.register(selector, SelectionKey.OP_CONNECT);
            channel.connect(new InetSocketAddress(port));
            int selected = selector.select(TIMEOUT);
            if (selected == 0 || !channel.finishConnect()) {
                throw new SocketTimeoutException("Could not connect to spbau.daniil.smirnov.myftp.server in " + TIMEOUT + "ms");
            }
            return channel;
        }
    }
}
