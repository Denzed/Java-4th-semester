package ru.spbau.daniil.smirnov.myftp.server.actions;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class GetFileActionTest {
    @Test
    public void getTest() throws Exception {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        Path path = folder.newFile("file").toPath();
        byte[] data = new byte[10000];
        new Random().nextBytes(data);
        Files.write(path, data);

        byte[] response = new GetFileAction(path.toAbsolutePath()).perform();
        assertArrayEquals(ByteBuffer.allocate(Integer.BYTES + data.length)
                                    .putInt(data.length)
                                    .put(data)
                                    .array(),
                          response);
        assertArrayEquals(data, GetFileAction.fromBytes(response));
    }

    @Test
    public void getNonexistentPathTest() throws Exception {
        byte[] response = new GetFileAction(Paths.get("nonexistent", "directory").toAbsolutePath()).perform();
        assertArrayEquals(ByteBuffer.allocate(Integer.BYTES)
                        .putInt(0)
                        .array(),
                response);
        assertArrayEquals(new byte[]{}, GetFileAction.fromBytes(response));
    }

    @Test
    public void getDirectoryTest() throws Exception {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        byte[] response = new GetFileAction(folder.newFolder().toPath().toAbsolutePath()).perform();
        assertArrayEquals(ByteBuffer.allocate(Integer.BYTES)
                        .putInt(0)
                        .array(),
                response);
        assertArrayEquals(new byte[]{}, GetFileAction.fromBytes(response));
    }
}
