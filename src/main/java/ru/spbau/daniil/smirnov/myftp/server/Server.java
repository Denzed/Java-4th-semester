package ru.spbau.daniil.smirnov.myftp.server;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myftp.exceptions.MyFTPException;
import ru.spbau.daniil.smirnov.myftp.server.actions.GetFileAction;
import ru.spbau.daniil.smirnov.myftp.server.actions.ListDirectoryAction;
import ru.spbau.daniil.smirnov.myftp.utils.ChannelByteReader;
import ru.spbau.daniil.smirnov.myftp.utils.ChannelByteWriter;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Stands for MyFTP spbau.daniil.smirnov.myftp.server which interacts with clients using {@link ServerSocketChannel}
 */
public class Server {
    private final int port;

    @NotNull
    private final Queue<ClientRequest> requestQueue;

    private volatile boolean isStopped;

    /**
     * Constructs a spbau.daniil.smirnov.myftp.server bound to the given port
     * @param port port to which spbau.daniil.smirnov.myftp.server should be bound
     */
    public Server(int port) {
        this.port = port;
        this.isStopped = false;
        this.requestQueue = new LinkedList<>();
    }

    /**
     * Starts a spbau.daniil.smirnov.myftp.server in a separate thread
     */
    public void start() {
        final Runnable serverCycleTask = () -> {
            try (
                    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                    Selector selector = Selector.open()) {
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(new InetSocketAddress(port));
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                while (!isStopped) {
                    int readyChannelCount = selector.selectNow();
                    if (readyChannelCount != 0) {
                        processReadyChannels(selector);
                        processQueue(selector);
                    }
                }
            } catch (IOException e) {
                System.out.println("An exception occurred. Server will be stoppped. Stack trace:");
                e.printStackTrace();
            }
        };
        new Thread(serverCycleTask).start();
    }

    /**
     * Stops the spbau.daniil.smirnov.myftp.server on the next iteration of its working cycle
     */
    public void stop() {
        isStopped = true;
    }

    /**
     * Gets the port bound to the spbau.daniil.smirnov.myftp.server
     * @return bound port
     */
    int getPort() {
        return port;
    }

    private void processReadyChannels(@NotNull Selector selector) {
        final Set<SelectionKey> keySet = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = keySet.iterator();
        while (keyIterator.hasNext()) {
            final SelectionKey key = keyIterator.next();
            if (key.isAcceptable()) {
                acceptConnection(key, selector);
            } else if (key.isReadable()) {
                final ChannelByteReader reader = (ChannelByteReader) key.attachment();
                try {
                    int bytesRead = reader.read((ByteChannel) key.channel());
                    if (bytesRead == -1) {
                        byte[] data = reader.getData();
                        key.interestOps(0);
                        requestQueue.add(new ClientRequest(key, data));
                    }
                } catch (IOException e) {
                    closeChannelForKey(key);
                }
            } else if (key.isWritable()) {
                final ChannelByteWriter writer = (ChannelByteWriter) key.attachment();
                try {
                    int bytesWritten = writer.write((ByteChannel) key.channel());
                    if (bytesWritten == -1) {
                        closeChannelForKey(key);
                    }
                } catch (IOException e) {
                    closeChannelForKey(key);
                }
            }
            keyIterator.remove();
        }
    }

    private void processQueue(@NotNull Selector selector) {
        while (!requestQueue.isEmpty()) {
            ClientRequest request = requestQueue.poll();
            byte[] response;
            try (
                    ByteArrayInputStream byteStream = new ByteArrayInputStream(request.getContent());
                    DataInputStream inputStream = new DataInputStream(byteStream)) {
                int actionCode = inputStream.readInt();
                switch (actionCode) {
                    case ListDirectoryAction.ACTION_CODE:
                        response = new ListDirectoryAction(Paths.get(inputStream.readUTF())).perform();
                        break;
                    case GetFileAction.ACTION_CODE:
                        response = new GetFileAction(Paths.get(inputStream.readUTF())).perform();
                        break;
                    default:
                        throw new MyFTPException("Unknown action code requested");
                }
            } catch (IOException | MyFTPException e) {
                response = new byte[]{};
            }

            try {
                SelectableChannel channel = request.getKey().channel();
                channel.register(selector, SelectionKey.OP_WRITE, new ChannelByteWriter(response));
            } catch (ClosedChannelException e) {
                closeChannelForKey(request.getKey());
            }
        }
    }

    private void closeChannelForKey(@NotNull SelectionKey key) {
        try {
            key.cancel();
            key.channel().close();
        } catch (IOException e) {
            System.out.println("An exception occurred while closing channel:");
            e.printStackTrace();
        }
    }

    private void acceptConnection(@NotNull SelectionKey key, @NotNull Selector selector) {
        Channel channel = key.channel();
        if (!(channel instanceof ServerSocketChannel)) {
            return;
        }
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel client;
        try {
            client = serverSocketChannel.accept();
            client.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            client.register(selector, SelectionKey.OP_READ, new ChannelByteReader());
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }
}
