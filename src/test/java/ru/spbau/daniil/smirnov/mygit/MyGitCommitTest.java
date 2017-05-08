package ru.spbau.daniil.smirnov.mygit;

import org.junit.Assert;
import org.junit.Test;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitEmptyCommitException;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitIllegalArgumentException;
import ru.spbau.daniil.smirnov.testing.MyGitInitialisedTest;

import java.nio.file.Path;

/**
 * Test set for MyGit commit command
 */
public class MyGitCommitTest extends MyGitInitialisedTest {
    @Test
    public void CommitTest() throws Exception {
        for (String filename : FILES) {
            temporaryFolder.newFile(filename);
        }
        for (Path path : FILE_PATHS) {
            String[] paths = {path.toString()};
            actionHandler.add(paths);
        }
        actionHandler.commit("added some files");
        for (Path path : FILE_PATHS) {
            Assert.assertNotNull(actionHandler.findElementInHeadTree(path));
        }
    }

    @Test(expected = MyGitEmptyCommitException.class)
    public void EmptyCommitTest() throws Exception {
        actionHandler.commit("added some files (no)");
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void EmptyCommitMessageTest() throws Exception {
        for (String filename : FILES) {
            temporaryFolder.newFile(filename);
        }
        for (Path path : FILE_PATHS) {
            String[] paths = {path.toString()};
            actionHandler.add(paths);
        }
        actionHandler.commit("");
    }
}
