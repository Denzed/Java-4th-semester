package ru.spbau.daniil.smirnov.myjunit;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
        List<Path> filesInPath;
        try {
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.class");
            filesInPath = Files.find(pathWithTests,
                                     Integer.MAX_VALUE,
                                     (path, attributes) ->
                                             attributes.isRegularFile() && pathMatcher.matches(path))
                                .collect(Collectors.toList());
        } catch (IOException e) {
            printStream.printf("An error occurred while reading file tree:\n%s\n", e.getMessage());
            return;
        }
        URL fileURLs[] = filesInPath
                .stream()
                .map(path -> {
                        try {
                           return path.toUri().toURL();
                        } catch (MalformedURLException e) {
                            printStream.printf(
                                    "Forming URL for path %s failed with message:\n%s\n%s\n",
                                    path,
                                    e.getMessage(),
                                    "So it will be ignored");
                            return null;
                        }
                   })
                .filter(url -> url != null)
                .toArray(URL[]::new);
        ClassLoader classLoader = new URLClassLoader(fileURLs);
        filesInPath
                .stream()
                .map(Path::toFile)
                .forEach(file -> loadClassAndRunTests(classLoader, file));
    }

    private void loadClassAndRunTests(@NotNull ClassLoader classLoader, @NotNull File file) {
        @NotNull
        Class<?> clazz;
        try {
            clazz = classLoader.loadClass(file.getName());
        } catch (ClassNotFoundException e) {
            printStream.printf(
                    "Loading class from file %s failed with message:\n%s\n%s\n",
                    file,
                    e.getMessage(),
                    "So it will be ignored");
            return;
        }
        new ClassTestRunner<>(clazz, printStream).runTests();
    }
}
