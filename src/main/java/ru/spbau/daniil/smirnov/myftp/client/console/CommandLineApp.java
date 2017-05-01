package ru.spbau.daniil.smirnov.myftp.client.console;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myftp.client.Client;
import ru.spbau.daniil.smirnov.myftp.server.ServerCommandLineApp;

/**
 * Command line access to MyFTP spbau.daniil.smirnov.myftp.client
 */
public class CommandLineApp {
    /**
     * Runs the spbau.daniil.smirnov.myftp.client command line app
     * @param arguments command line arguments
     */
    public static void main(@NotNull String[] arguments) {
        final CommandLineArgumentsHandler handler = new CommandLineArgumentsHandler(
                () -> new Client(ServerCommandLineApp.PORT),
                System.out);
        handler.handle(arguments);
    }
}
