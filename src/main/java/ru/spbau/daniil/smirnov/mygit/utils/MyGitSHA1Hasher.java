package ru.spbau.daniil.smirnov.mygit.utils;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitIllegalArgumentException;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * {@link MyGitHasher} implementation which uses SHA1 as hashing algorithm
 */
public class MyGitSHA1Hasher implements MyGitHasher {
    private static final String HASHING_ALGORITHM = "SHA-1";
    private static final int HASH_LENGTH = 40; // SHA-1 computes hashes of length 160 bits => 40 hexadecimal digits

    /**
     * Computes SHA-1 hash of a given object
     * @param object object to hash
     * @return its hash
     * @throws IOException if an exception occurs in the hasher
     */
    @Override
    public @NotNull String getHashFromObject(@NotNull Object object)
            throws IOException {
        return bytesToHex(getByteHashFromObject(object));
    }

    /**
     * Splits the hash computed by {@link #getHashFromObject(Object)} into two parts:
     * directory hash will be directoryHash {@value ru.spbau.daniil.smirnov.mygit.utils.MyGitSHA1Hasher.SHA1HashParts#DIRECTORY_HASH_LENGTH}
     * letters and the latter will serve as a filename hash
     * @param hash hash to split computed by {@link #getHashFromObject(Object)}
     * @return splitted hash
     * @throws MyGitIllegalArgumentException if the given hash length is not {@value ru.spbau.daniil.smirnov.mygit.utils.MyGitSHA1Hasher#HASH_LENGTH} letters
     */
    @Override
    public @NotNull MyGitHasher.HashParts splitHash(@NotNull String hash)
            throws MyGitIllegalArgumentException {
        return new SHA1HashParts(hash);
    }

    class SHA1HashParts implements MyGitHasher.HashParts {
        static final int DIRECTORY_HASH_LENGTH = 3;

        @NotNull
        final private String directoryHash;

        @NotNull
        final private String filenameHash;

        /**
         * Constructs hash parts from the given string
         * @param hash SHA-1 hash
         * @throws MyGitIllegalArgumentException if the given hash length is not {@value ru.spbau.daniil.smirnov.mygit.utils.MyGitSHA1Hasher#HASH_LENGTH} letters
         */
        private SHA1HashParts(@NotNull String hash) throws MyGitIllegalArgumentException {
            if (hash.length() != HASH_LENGTH) {
                throw new MyGitIllegalArgumentException("hash length is not " + HASH_LENGTH);
            }
            directoryHash = hash.substring(0, DIRECTORY_HASH_LENGTH);
            filenameHash = hash.substring(DIRECTORY_HASH_LENGTH);
        }

        /**
         * Gets hash of the directory name
         * @return hash of the directory name
         */
        @Override
        public @NotNull String getDirectoryHash() {
            return directoryHash;
        }

        /**
         * Gets hash of the filename
         * @return hash of the filename
         */
        @Override
        public @NotNull String getFilenameHash() {
            return filenameHash;
        }
    }

    private static byte[] getByteHashFromObject(@NotNull Object object)
            throws IOException {
        final MessageDigest messageDigest = getMessageDigest();

        final OutputStream dummyOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        };

        try (
                DigestOutputStream digestOutputStream = new DigestOutputStream(dummyOutputStream, messageDigest);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(digestOutputStream)) {
            objectOutputStream.writeObject(object);
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

    @NotNull
    private static String bytesToHex(@NotNull byte[] byteHashFromObject) {
        return DatatypeConverter.printHexBinary(byteHashFromObject);
    }
}
