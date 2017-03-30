import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

/**
 * Class for generating file and directory hashes using a single thread
 */
public class SingleThreadedMD5Hasher {
    private static final String HASHING_ALGORITHM = "MD5";
    private static final int BUFFER_SIZE = 4096;

    /**
     * Computes MD5 hash of a given object
     *
     * @param path path to file to hash
     * @return its hash
     * @throws IOException if an exception occurs in the hasher
     */
    public static @NotNull byte[] getHashFromPath(@NotNull Path path) throws IOException {
        File file = path.toFile();
        if (!file.exists()) {
            throw new NoSuchFileException(path.toString());
        }
        return file.isFile()
                ? getHashFromFile(file)
                : getHashFromDirectory(file);
    }

    @NotNull
    private static byte[] getHashFromDirectory(@NotNull File directory) throws IOException {
        Vector<InputStream> inputStreams = new Vector<>();
        inputStreams.add(new ByteArrayInputStream(directory.getName().getBytes()));
        File[] files = directory.listFiles();
        Arrays.sort(files, Comparator.comparing(File::getName));
        for (File file : files) {
            inputStreams.add(new ByteArrayInputStream(getHashFromFile(file)));
        }
        MessageDigest messageDigest = getMessageDigest();
        try (DigestInputStream digestInputStream = new DigestInputStream(
                new SequenceInputStream(inputStreams.elements()),
                messageDigest)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (digestInputStream.read(buffer) != -1) ;
        }
        return messageDigest.digest();
    }

    @NotNull
    private static byte[] getHashFromFile(@NotNull File file) throws IOException {
        MessageDigest messageDigest = getMessageDigest();
        try (DigestInputStream digestInputStream = new DigestInputStream(
                new FileInputStream(file),
                messageDigest)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((digestInputStream.read(buffer) != -1)) ;
        }
        return messageDigest.digest();
    }

    @NotNull
    private static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance(HASHING_ALGORITHM);
        } catch (NoSuchAlgorithmException ignored) {
            throw new IllegalStateException(HASHING_ALGORITHM + " hashing algorithm is not available");
        }
    }
}