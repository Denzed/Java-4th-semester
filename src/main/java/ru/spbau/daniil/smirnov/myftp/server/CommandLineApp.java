package ru.spbau.daniil.smirnov.myftp.server;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

/**
 * Command line access to server
 */
public class CommandLineApp {
    /**
     * Port to bind server on
     */
    public static final int PORT = 40000;

    /**
     * Runs a server command line app.
     * @param args command line arguments
     */
    public static void main(@NotNull String[] args) {
        CommandLineArgumentsHandler handler = new CommandLineArgumentsHandler(
                () -> new Server(PORT),
                System.out);
        handler.showHelp();
        try (Scanner scanner = new Scanner(System.in)) {
            handler.handleArgumentsFrom(scanner);
        }
    }
}
