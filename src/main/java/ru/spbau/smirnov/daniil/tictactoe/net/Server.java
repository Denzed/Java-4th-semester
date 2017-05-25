package ru.spbau.smirnov.daniil.tictactoe.net;

import org.jetbrains.annotations.NotNull;
import ru.spbau.smirnov.daniil.tictactoe.GameHandler;
import ru.spbau.smirnov.daniil.tictactoe.net.actions.MakeTurnAction;
import ru.spbau.smirnov.daniil.tictactoe.net.actions.RegisterAction;
import ru.spbau.smirnov.daniil.tictactoe.net.utils.ChannelByteReader;
import ru.spbau.smirnov.daniil.tictactoe.net.utils.ChannelByteWriter;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Stands for server which interacts with clients using {@link ServerSocketChannel}
 */
public class Server {
    private final int port;
    private final GameHandler gameHandler;

    @NotNull
    private final Queue<ClientRequest> requestQueue;

    private volatile boolean isStopped;

    /**
     * Constructs a server bound to the given port
     * @param port port to which server should be bound
     */
    public Server(int port, GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        this.port = port;
        this.isStopped = false;
        this.requestQueue = new LinkedList<>();
    }

    /**
     * Starts a server in a separate thread
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
     * Stops the server on the next iteration of its working cycle
     */
    public void stop() {
        isStopped = true;
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
                    case RegisterAction.ACTION_CODE:
                        response = ByteBuffer.allocate(Integer.BYTES).putInt(gameHandler.register()).array();
                        break;
                    case MakeTurnAction.ACTION_CODE:
                        int row = inputStream.readInt();
                        int column = inputStream.readInt();
                        if (gameHandler.canMakeTurn(row, column, gameHandler.getOtherPlayer())) {
                            gameHandler.makeTurn(row, column);
                        }
                    default:
                        throw new IOException("Unknown action code requested");
                }

            } catch (IOException e) {
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
