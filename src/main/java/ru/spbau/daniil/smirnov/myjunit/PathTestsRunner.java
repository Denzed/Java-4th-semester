package ru.spbau.daniil.smirnov.myjunit;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Runs tests found in .class files found in the given path in accordance with annotations from
 * {@link ru.spbau.daniil.smirnov.myjunit.annotations} package
 */
public class PathTestsRunner {
    @NotNull
    private final Path pathWithTests;

    @NotNull
    private final PrintStream printStream;

    /**
     * Constructs the test runner
     * @param pathWithTests path which contains .class files with tests defined by annotations from
     * {@link ru.spbau.daniil.smirnov.myjunit.annotations} package
     * @param printStream {@link PrintStream} to write output into
     */
    public PathTestsRunner(@NotNull Path pathWithTests, @NotNull PrintStream printStream) {
        this.pathWithTests = pathWithTests;
        this.printStream = printStream;
    }

    /**
     * Runs the tests
     */
    public void runTests() {
        @NotNull
        List<Path> filesInPath;
        try {
            filesInPath = findClassFilesInPath(pathWithTests);
        } catch (IOException e) {
            printStream.printf("An error occurred while reading file tree:\n%s\n", e.getMessage());
            return;
        }
        filesInPath
                .stream()
                .map(path -> path.relativize(pathWithTests))
                .map(Path::toFile)
                .forEach(this::loadClassAndRunTests);
    }

    @NotNull
    static private List<Path> findClassFilesInPath(@NotNull Path pathWithTests) throws IOException {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("regex:.*\\.class$");
        return Files.find(pathWithTests,
                Integer.MAX_VALUE,
                (path, attributes) ->
                        attributes.isRegularFile() && pathMatcher.matches(path))
                .collect(Collectors.toList());
    }

    private void loadClassAndRunTests(@NotNull File file) {
        @NotNull
        Class<?> clazz;
        try {
            clazz = Class.forName(file.toString());
        } catch (ClassNotFoundException e) {
            printStream.printf(
                    "Loading class %s failed with message:\n%s\n%s\n",
                    file,
                    e.getMessage(),
                    "So it will be ignored");
            return;
        }
        new ClassTestRunner<>(clazz).runTests(printStream);
    }
}
