package ru.spbau.daniil.smirnov.myjunit.consoleapp;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myjunit.PathTestsRunner;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles command line arguments and invokes corresponding methods
 */
class CommandLineArgumentsHandler {
    private static final String HELP = "help";

    @NotNull
    private final PrintStream printStream;

    CommandLineArgumentsHandler(@NotNull PrintStream printStream) {
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
        handleRunTests(arguments[0]);

    }

    private void handleRunTests(@NotNull String pathString) {
        Path path = Paths.get(pathString);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir"), pathString);
        }
        new PathTestsRunner(path, printStream).runTests();
    }

    private void showHelp() {
        printStream.println(
                "usage:\n" +
                "path containing .class files with tests\n" +
                "   to run tests\n" +
                HELP + "\n" +
                "   to show this help\n");
    }
}
