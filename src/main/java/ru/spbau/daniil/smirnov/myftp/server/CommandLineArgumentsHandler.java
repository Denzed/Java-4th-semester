package ru.spbau.daniil.smirnov.myftp.server;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Iterator;

/**
 * Class which handles server commands which are coming as strings from {@link Iterator}
 * and prints the result to {@link PrintStream}.
 */
class CommandLineArgumentsHandler {
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String EXIT = "exit";

    @NotNull
    private final ServerFactory serverFactory;

    @NotNull
    private final PrintStream printStream;

    CommandLineArgumentsHandler(@NotNull ServerFactory serverFactory, @NotNull PrintStream printStream) {
        this.serverFactory = serverFactory;
        this.printStream = printStream;
    }

    void handleArgumentsFrom(@NotNull Iterator<String> iterator) {
        Server server = null;
        while (true) {
            String token = iterator.next();
            switch (token) {
                case START:
                    if (server == null) {
                        server = serverFactory.createServer();
                        server.start();
                        printStream.println("Started a server on port " + server.getPort());
                    } else {
                        printStream.println("The server has already started");
                    }
                    break;
                case STOP:
                    if (server == null) {
                        printStream.println("No server is running");
                    } else {
                        server.stop();
                        server = null;
                        printStream.println("Server will stop shortly");
                    }
                    break;
                case EXIT:
                    if (server != null) {
                        server.stop();
                    }
                    return;
                default:
                    printStream.println("Unknown command -- ignoring");
                    break;
            }
        }
    }

    void showHelp() {
        printStream.println(
                "Enter:\n" +
                START + "to start the server\n" +
                STOP + "to stop the server\n" +
                EXIT + "to exit\n");
    }
}