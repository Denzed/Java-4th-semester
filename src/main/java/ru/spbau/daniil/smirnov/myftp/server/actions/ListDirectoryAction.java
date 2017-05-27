package ru.spbau.daniil.smirnov.myftp.server.actions;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myftp.exceptions.MyFTPIllegalArgumentException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link Action} which lists directory contents
 */
public class ListDirectoryAction extends Action {
    public static final int ACTION_CODE = 1;

    @NotNull
    private final Path pathToDirectory;

    /**
     * Constructs the action instance
     *
     * @param pathToDirectory path to the directory contents of which it will try to list
     */
    public ListDirectoryAction(@NotNull Path pathToDirectory) {
        this.pathToDirectory = pathToDirectory;
    }

    /**
     * Converts the return byte sequence from {@link #perform()} to human-readable form
     *
     * @param response response from server to convert
     * @return List of entries {@code (String name, boolean isDirectory)} which represents directory contents
     * @throws IOException if an I/O exception occurs
     */
    public static List<ListActionResultEntry> fromBytes(byte[] response) throws IOException {
        try (
                ByteArrayInputStream byteStream = new ByteArrayInputStream(response);
                DataInputStream inputStream = new DataInputStream(byteStream)) {
            int entryCount = inputStream.readInt();
            List<ListActionResultEntry> entries = new ArrayList<>(entryCount);
            while (entryCount > 0) {
                String name = inputStream.readUTF();
                boolean isDirectory = inputStream.readBoolean();
                entries.add(new ListActionResultEntry(name, isDirectory));
                entryCount--;
            }
            return entries;
        }
    }

    /**
     * Performs the action and returns the result
     *
     * @return Byte array representing the response in form: <size: Int> (<name: String> <is_dir: Boolean>)*
     * @throws IOException if an I/O exception occurs
     * @throws MyFTPIllegalArgumentException if the given argument is invalid
     */
    @NotNull
    @Override
    public byte[] perform() throws IOException, MyFTPIllegalArgumentException {
        return toByte(listDirectory());
    }

    private List<File> listDirectory() throws MyFTPIllegalArgumentException, IOException {
        if (!pathToDirectory.isAbsolute()) {
            throw new MyFTPIllegalArgumentException(
                    pathToDirectory.toString() + " - path is not absolute");
        }
        File file = pathToDirectory.toFile();
        if (file.exists() && file.isDirectory()) {
            return Files.list(pathToDirectory)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private byte[] toByte(List<File> files) throws IOException {
        try (
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            outputStream.writeInt(files.size());
            for (File file : files) {
                outputStream.writeUTF(file.getName());
                outputStream.writeBoolean(file.isDirectory());
            }
            outputStream.flush();
            return byteStream.toByteArray();
        }
    }

    /**
     * A directory contents entry: a String representing filename and a boolean which tells whether it is a directory
     */
    public static class ListActionResultEntry {
        @NotNull
        private final String name;

        private final boolean isDirectory;

        public ListActionResultEntry(@NotNull String name, boolean isDirectory) {
            this.name = name;
            this.isDirectory = isDirectory;
        }

        /**
         * Gets file name
         * @return file name
         */
        @NotNull
        public String getName() {
            return name;
        }

        /**
         * Tells whether the entry is a directory
         * @return {@code true} if so, {@code false} otherwise
         */
        public boolean isDirectory() {
            return isDirectory;
        }

        /**
         * Tells whether the entry is a file (opposite to {@link #isDirectory()} method)
         * @return {@code true} if so, {@code false} otherwise
         */
        public boolean isFile() {
            return !isDirectory;
        }

        /**
         * A generated method to compare two entries' equality
         * @param o object to compare with
         * @return {@code true} if equals, {@code false} if not
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListActionResultEntry entry = (ListActionResultEntry) o;

            return isDirectory() == entry.isDirectory() && getName().equals(entry.getName());

        }
    }
}
