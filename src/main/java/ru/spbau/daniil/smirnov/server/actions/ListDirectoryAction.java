package ru.spbau.daniil.smirnov.server.actions;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.exceptions.MyFTPIllegalArgumentException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link Action} which lists directory contents
 */
public class ListDirectoryAction extends Action<List<ListDirectoryAction.ListActionResultEntry>> {
    @NotNull
    private final Path pathToDirectory;

    public class ListActionResultEntry {
        @NotNull
        private final String name;

        private final boolean isDirectory;

        public ListActionResultEntry(@NotNull String name, boolean isDirectory) {
            this.name = name;
            this.isDirectory = isDirectory;
        }
    }

    /**
     * Constructs the action instance
     *
     * @param pathToDirectory path to the directory contents of which it will try to list
     */
    public ListDirectoryAction(@NotNull Path pathToDirectory) {
        this.pathToDirectory = pathToDirectory;
    }

    /**
     * Performs the action and returns the result
     *
     * @return List of entries (filename, isDirectory) which is empty if the directory is not present
     * @throws IOException                   if an I/O exception occurs while working with the filesystem
     * @throws MyFTPIllegalArgumentException if the given argument is invalid
     */
    @NotNull
    @Override
    public List<ListActionResultEntry> perform() throws IOException, MyFTPIllegalArgumentException {
        if (!pathToDirectory.isAbsolute()) {
            throw new MyFTPIllegalArgumentException(
                    pathToDirectory.toString() + " - path is not absolute");
        }
        File file = pathToDirectory.toFile();
        if (file.exists() || file.isDirectory()) {
            return Files.list(pathToDirectory)
                    .map(path -> new ListActionResultEntry(
                            path.getFileName().toString(),
                            path.toFile().isDirectory()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
