package ru.spbau.daniil.smirnov.myftp.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myftp.exceptions.MyFTPException;
import ru.spbau.daniil.smirnov.myftp.exceptions.MyFTPIllegalArgumentException;
import ru.spbau.daniil.smirnov.myftp.exceptions.MyFTPUnsuccessfulOperationException;
import ru.spbau.daniil.smirnov.myftp.server.actions.ListDirectoryAction;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * Handles command line arguments and invokes corresponding methods
 */
class CommandLineArgumentsHandler {
    private static final String HELP = "help";
    private static final String GET = "get";
    private static final String LIST = "list";

    @NotNull
    private final ClientFactory clientFactory;

    @NotNull
    private final PrintStream printStream;

    CommandLineArgumentsHandler(@NotNull ClientFactory clientFactory, @NotNull PrintStream printStream) {
        this.clientFactory = clientFactory;
        this.printStream = printStream;
    }

    /**
     * Parses command line arguments and executes them if parsed successfully.
     * @param arguments command line arguments
     */
    void handle(@NotNull String[] arguments) {
        if (arguments.length == 0 || arguments[0].equals(HELP)) {
            showHelp();
            return;
        }

        try {
            switch (arguments[0]) {
                case GET:
                    handleGetCommand(arguments);
                    break;
                case LIST:
                    handleListCommand(arguments);
                    break;
                default:
                    showHelp();
            }
        } catch (Exception e) {
            printStream.println("Unsuccessful operation:");
            e.printStackTrace(printStream);
        }
    }

    private void handleGetCommand(@NotNull String[] arguments) throws IOException, MyFTPException {
        if (arguments.length > 1) {
            byte[] response = clientFactory.createClient().get(arguments[1]);
            printStream.write(response);
        } else {
            throw new MyFTPIllegalArgumentException("No file path specified");
        }
    }

    private void handleListCommand(@NotNull String[] arguments) throws IOException, MyFTPException {
        if (arguments.length > 1) {
            final List<ListDirectoryAction.ListActionResultEntry> response =
                    clientFactory.createClient().list(arguments[1]);
            if (response == null) {
                throw new MyFTPUnsuccessfulOperationException("Response is empty");
            }
            printStream.println("Directory " + arguments[1] + " contents:");
            for (ListDirectoryAction.ListActionResultEntry entry : response) {
                printStream.println((entry.isDirectory() ? "d" : "f") + " " + entry.getName());
            }
        } else {
            throw new MyFTPIllegalArgumentException("No directory path specified");
        }
    }

    private void showHelp() {
        printStream.println(
                "usage: <command> [<args>]\n" +
                        "\n" +
                        "show this help:\n" +
                        "  " + HELP + "\n" +
                        "\n" +
                        "get file contents:\n" +
                        "  " + GET + " <file>\n" +
                        "\n" +
                        "list directory contents:\n" +
                        "  " + LIST + "<directory>\n");
    }
}
