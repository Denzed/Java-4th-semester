package hashers.forkjointasks;

import hashers.MD5HashTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Class for a file hashing task that runs within a ForkJoinPool
 */
public class MD5HashSingleFileTask extends MD5HashTask {
    private final File file;

    /**
     * Constructs a task for hashing a single file
     *
     * @param file file to hash
     */
    public MD5HashSingleFileTask(@NotNull File file) {
        this.file = file;
    }

    /**
     * Method which invokes the computation of hash of a single file
     *
     * @return computed hash
     */
    @NotNull
    public byte[] compute() {
        MessageDigest messageDigest = getMessageDigest();
        try {
            try (DigestInputStream digestInputStream = new DigestInputStream(
                    new FileInputStream(file),
                    messageDigest)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((digestInputStream.read(buffer) != -1)) ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageDigest.digest();
    }
}
