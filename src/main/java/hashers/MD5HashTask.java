package hashers;

import hashers.forkjointasks.MD5HashDirectoryTask;
import hashers.forkjointasks.MD5HashSingleFileTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.RecursiveTask;

/**
 * Base class for ForkJoin MD5 hashing tasks
 */
abstract public class MD5HashTask extends RecursiveTask<byte[]> {
    private static final String HASHING_ALGORITHM = "MD5";
    protected static final int BUFFER_SIZE = 4096;

    /**
     * Method which invokes the hash computation
     *
     * @return computed hash
     */
    @NotNull
    public abstract byte[] compute();

    @NotNull
    protected static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance(HASHING_ALGORITHM);
        } catch (NoSuchAlgorithmException ignored) {
            throw new IllegalStateException(HASHING_ALGORITHM + " hashing algorithm is not available");
        }
    }

    /**
     * Generates a hashing task depending on file type
     *
     * @param file file to hash
     * @return the generated task
     */
    static public MD5HashTask makeTask(File file) {
        return file.isFile()
                ? new MD5HashSingleFileTask(file)
                : new MD5HashDirectoryTask(file);
    }
}
