package ru.spbau.daniil.smirnov.myftp.client;

import org.jetbrains.annotations.NotNull;

/**
 * Command line access to MyFTP client
 */
public class CommandLineApp {
    /**
     * Runs the client command line app
     * @param arguments command line arguments
     */
    public static void main(@NotNull String[] arguments) {
        final CommandLineArgumentsHandler handler = new CommandLineArgumentsHandler(System.out);
        handler.handle(arguments);
    }
}
