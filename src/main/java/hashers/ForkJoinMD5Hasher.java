package hashers;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Class for generating file and directory hashes with help of a ForkJoinPool
 */
public class ForkJoinMD5Hasher {
    /**
     * Computes MD5 hash of a given object
     *
     * @param path path to file to hash
     * @return its hash
     * @throws IOException if an exception occurs when working with the filesystem
     */
    public static @NotNull byte[] getHashFromPath(@NotNull Path path) throws IOException {
        File file = path.toFile();
        if (!file.exists()) {
            throw new NoSuchFileException(file.toPath().toString());
        }
        return MD5HashTask.makeTask(file).compute();
    }
}