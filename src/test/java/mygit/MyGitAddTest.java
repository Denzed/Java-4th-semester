package mygit;

import mygit.exceptions.MyGitIllegalArgumentException;
import org.junit.Test;
import testing.MyGitInitialisedTest;

import java.nio.file.Path;

/**
 * Test set for MyGit add command
 */
public class MyGitAddTest extends MyGitInitialisedTest {
    @Test
    public void AddTest() throws Exception {
        for (String filename : FILES) {
            temporaryFolder.newFile(filename);
        }
        for (Path path : FILE_PATHS) {
            String[] paths = {path.toString()};
            actionHandler.add(paths);
        }
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void AddMissingFileTest() throws Exception {
        String[] paths = {FILE_PATHS.get(0).toString()};
        actionHandler.add(paths);
    }
}
