package ru.spbau.daniil.smirnov.server.actions;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.exceptions.MyFTPIllegalArgumentException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@link Action} which gets the file at the given path
 */
public class GetFileAction extends Action<byte[]> {
    @NotNull
    private final Path pathToFile;

    /**
     * Constructs the action instance
     *
     * @param pathToFile path to the file it will try to get
     */
    public GetFileAction(@NotNull Path pathToFile) {
        this.pathToFile = pathToFile;
    }

    /**
     * Performs the file get action and returns the result
     *
     * @return byte array of file contents which is empty if the file is not empty
     * @throws IOException                   if an I/O exception occurs while working with the filesystem
     * @throws MyFTPIllegalArgumentException if the given argument is invalid
     */
    @NotNull
    @Override
    public byte[] perform()
            throws IOException, MyFTPIllegalArgumentException {
        if (!pathToFile.isAbsolute()) {
            throw new MyFTPIllegalArgumentException(
                    pathToFile.toString() + " - path is not absolute");
        }
        File file = pathToFile.toFile();
        if (file.exists() || file.isFile()) {
            return Files.readAllBytes(pathToFile);
        }
        return new byte[]{};
    }
}
