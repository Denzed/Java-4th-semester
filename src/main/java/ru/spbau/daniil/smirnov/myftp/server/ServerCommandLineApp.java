package ru.spbau.daniil.smirnov.myftp.server;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

/**
 * Command line access to spbau.daniil.smirnov.myftp.server
 */
public class ServerCommandLineApp {
    /**
     * Port to bind spbau.daniil.smirnov.myftp.server on
     */
    public static final int PORT = 40000;

    /**
     * Runs a spbau.daniil.smirnov.myftp.server command line app.
     * @param args command line arguments
     */
    public static void main(@NotNull String[] args) {
        ServerCommandLineArgumentsHandler handler = new ServerCommandLineArgumentsHandler(
                () -> new Server(PORT),
                System.out);
        handler.showHelp();
        try (Scanner scanner = new Scanner(System.in)) {
            handler.handleArgumentsFrom(scanner);
        }
    }
}
