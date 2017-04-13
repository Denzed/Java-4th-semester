package mygit;

import org.junit.Assert;
import org.junit.Test;
import testing.MyGitInitialisedTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * Test set for MyGit rm command
 */
public class MyGitRmTest extends MyGitInitialisedTest {
    @Test
    public void ActualRemovalTest() throws Exception {
        final Path directory = temporaryFolder.newFolder().toPath();
        final Path fileInDirectory = Paths.get(directory.toString(), "sample");
        Files.createFile(fileInDirectory);
        actionHandler.rm(Collections.singletonList(directory));
        Assert.assertFalse(Files.exists(directory));
        Assert.assertFalse(Files.exists(fileInDirectory));
        final Path singleFile = temporaryFolder.newFile().toPath();
        actionHandler.rm(Collections.singletonList(singleFile));
        Assert.assertFalse(Files.exists(singleFile));
    }

    @Test
    public void TreeRemovalTest() throws Exception {
        final Path directory = temporaryFolder.newFolder().toPath();
        final Path fileInDirectory = Paths.get(directory.toString(), "sample");
        Files.createFile(fileInDirectory);
        actionHandler.add(new String[]{directory.toString()});
        actionHandler.commit("first");
        Assert.assertNotNull(actionHandler.findElementInHeadTree(fileInDirectory));
        actionHandler.rm(Collections.singletonList(fileInDirectory));
        actionHandler.commit("second");
        Assert.assertNull(actionHandler.findElementInHeadTree(fileInDirectory));
    }
}
