package ru.spbau.daniil.smirnov.myftp.server.actions;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myftp.exceptions.MyFTPException;
import ru.spbau.daniil.smirnov.myftp.exceptions.MyFTPIllegalArgumentException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@link Action} which gets the file at the given path
 */
public class GetFileAction extends Action {
    public static final int ACTION_CODE = 2;

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
     * Performs the file get action and returns the result in the form: <size: Int> <content: Bytes>
     *
     * @return byte array of file contents which is empty if the file is not empty
     * @throws IOException                   if an I/O exception occurs
     * @throws MyFTPIllegalArgumentException if the given argument is invalid
     */
    @NotNull
    @Override
    public byte[] perform()
            throws IOException, MyFTPIllegalArgumentException {
        return toByte(getFileContents());
    }

    @NotNull
    private byte[] getFileContents() throws MyFTPIllegalArgumentException, IOException {
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

    private byte[] toByte(@NotNull byte[] fileContents) throws IOException {
        try (
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            outputStream.writeInt(fileContents.length);
            outputStream.write(fileContents);
            outputStream.flush();
            return byteStream.toByteArray();
        }
    }

    /**
     * Converts the return byte sequence from {@link #perform()} to file contents
     *
     * @param response response from server to convert
     * @return actual file contents
     * @throws IOException if an I/O exception occurs
     * @throws MyFTPException if the response is corrupt
     */
    public static byte[] fromBytes(byte[] response) throws IOException, MyFTPException {
        try (
                ByteArrayInputStream byteStream = new ByteArrayInputStream(response);
                DataInputStream inputStream = new DataInputStream(byteStream)) {
            int fileSize = inputStream.readInt();
            byte[] fileContents = new byte[fileSize];
            if (inputStream.read(fileContents) < fileSize) {
                throw new MyFTPException("Corrupt response: "
                        + "file size read from response is greater than the actual file contents size");
            }
            return fileContents;
        }
    }
}
