package ru.spbau.daniil.smirnov.mygit.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitIllegalArgumentException;

/**
 * Test set for {@link MyGitSHA1Hasher} class
 */
public class MyGitSHA1HasherTest {
    private static final Object TEST_OBJECT = "Sample text";
    private static final MyGitHasher HASHER = new MyGitSHA1Hasher();

    @Test
    public void SHA1PartsTest() throws Exception {
        final String hash = HASHER.getHashFromObject(TEST_OBJECT);
        final MyGitHasher.HashParts parts = HASHER.splitHash(hash);
        Assert.assertEquals(hash.substring(0, MyGitSHA1Hasher.SHA1HashParts.DIRECTORY_HASH_LENGTH),
                            parts.getDirectoryHash());
        Assert.assertEquals(hash.substring(MyGitSHA1Hasher.SHA1HashParts.DIRECTORY_HASH_LENGTH),
                            parts.getFilenameHash());
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void SHA1PartsCrashTest() throws Exception {
        final String hash = HASHER.getHashFromObject(TEST_OBJECT);
        HASHER.splitHash(hash.substring(1));
    }

    @Test
    public void SHA1HasherTest() throws Exception {
        final String firstHash = HASHER.getHashFromObject(TEST_OBJECT);
        final String secondHash = HASHER.getHashFromObject(TEST_OBJECT);
        Assert.assertEquals(firstHash, secondHash);
    }
}
