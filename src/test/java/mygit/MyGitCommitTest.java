package mygit;

import mygit.exceptions.MyGitEmptyCommitException;
import mygit.exceptions.MyGitIllegalArgumentException;
import org.junit.Test;
import testing.MyGitInitialisedTest;

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
