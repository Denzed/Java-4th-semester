package ru.spbau.daniil.smirnov.myftp.server.actions;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListDirectoryActionTest {
    @Test
    public void list() throws Exception {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        File file1 = folder.newFile("file1");
        File file2 = folder.newFile("file2");
        File dir = folder.newFolder("dir");
        ListDirectoryAction command = new ListDirectoryAction(folder.getRoot().toPath().toAbsolutePath());
        List<ListDirectoryAction.ListActionResultEntry> list =
                ListDirectoryAction.fromBytes(command.perform());
        assertEquals(3, list.size());
        assertTrue(list.contains(new ListDirectoryAction.ListActionResultEntry(file1.getName(), false)));
        assertTrue(list.contains(new ListDirectoryAction.ListActionResultEntry(file2.getName(), false)));
        assertTrue(list.contains(new ListDirectoryAction.ListActionResultEntry(dir.getName(), true)));
    }

    @Test
    public void listNonexistentPathTest() throws Exception {
        assertEquals(0, ListDirectoryAction.fromBytes(
                new ListDirectoryAction(Paths.get("nonexistent", "directory").toAbsolutePath()).perform()).size());
    }

    @Test
    public void listFileTest() throws Exception {
        TemporaryFolder folder = new TemporaryFolder();
        folder.create();
        Path file = folder.newFile("file").toPath();
        assertEquals(0, ListDirectoryAction.fromBytes(
                new ListDirectoryAction(file.toAbsolutePath()).perform()).size());
    }
}