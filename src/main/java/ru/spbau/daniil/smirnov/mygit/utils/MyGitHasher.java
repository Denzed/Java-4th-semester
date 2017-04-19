package ru.spbau.daniil.smirnov.mygit.utils;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitIllegalArgumentException;

import java.io.IOException;

/**
 * Interface for hashing MyGit objects
 */
public interface MyGitHasher {
    @NotNull
    String getHashFromObject(@NotNull Object object) throws IOException;

    @NotNull
    HashParts splitHash(@NotNull String hash) throws MyGitIllegalArgumentException;

    /**
     * Interface used to split file hash into hashes of its directory name and filename
     */
    interface HashParts {
        /**
         * Gets hash of the directory name
         * @return hash of the directory name
         */
        @NotNull
        String getDirectoryHash();

        /**
         * Gets hash of the filename
         * @return hash of the filename
         */
        @NotNull
        String getFilenameHash();
    }
}
