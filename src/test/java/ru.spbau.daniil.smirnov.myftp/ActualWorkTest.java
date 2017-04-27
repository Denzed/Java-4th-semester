package ru.spbau.daniil.smirnov.myftp;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.daniil.smirnov.myftp.client.Client;
import ru.spbau.daniil.smirnov.myftp.server.Server;
import ru.spbau.daniil.smirnov.myftp.server.actions.ListDirectoryAction;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class ActualWorkTest {
    private static final int TEST_PORT = 179;

    @Test
    public void testListAndGet() throws Exception {
        Server server = new Server(TEST_PORT);
        server.start();
        sleep(500);

        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        File file1 = folder.newFile("file1");
        File file2 = folder.newFile("file2");
        File dir = folder.newFolder("dir");
        byte[] data = new byte[10000];
        new Random().nextBytes(data);
        Files.write(file1.toPath(), data);

        Client client = new Client(TEST_PORT);

        List<ListDirectoryAction.ListActionResultEntry> list =
                client.list(folder.getRoot().getAbsolutePath());
        assertTrue(list != null);
        assertEquals(3, list.size());
        assertTrue(list.contains(new ListDirectoryAction.ListActionResultEntry(file1.getName(), false)));
        assertTrue(list.contains(new ListDirectoryAction.ListActionResultEntry(file2.getName(), false)));
        assertTrue(list.contains(new ListDirectoryAction.ListActionResultEntry(dir.getName(), true)));

        byte[] dataReceived = client.get(file1.toPath().toAbsolutePath().toString());
        assertNotEquals(0, dataReceived.length);
        assertArrayEquals(data, dataReceived);

        server.stop();
    }
}
