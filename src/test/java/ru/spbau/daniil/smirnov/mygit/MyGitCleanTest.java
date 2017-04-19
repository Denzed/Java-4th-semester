package ru.spbau.daniil.smirnov.mygit;

import org.junit.Assert;
import org.junit.Test;
import ru.spbau.daniil.smirnov.mygit.objects.FileStatus;
import ru.spbau.daniil.smirnov.testing.MyGitInitialisedTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Test for MyGit clean command
 */
public class MyGitCleanTest extends MyGitInitialisedTest {
    @Test
    public void WorkTest() throws Exception {
        final Path stagedFile = Paths.get(folderPath.toString(), "in1");
        Files.createFile(stagedFile);
        actionHandler.add(new String[]{stagedFile.toString()});
        final Path unstagedFile = Paths.get(folderPath.toString(), "in2");
        Files.createFile(unstagedFile);
        Map<Path,FileStatus> differences = actionHandler.status();
        Assert.assertEquals(FileStatus.STAGED, differences.get(stagedFile));
        Assert.assertEquals(FileStatus.UNSTAGED, differences.get(unstagedFile));

        actionHandler.clean();

        differences = actionHandler.status();
        Assert.assertEquals(1, differences.size());
        for (Path path : differences.keySet()) {
            Assert.assertEquals(FileStatus.STAGED, differences.get(path));
            Assert.assertEquals("in1", path.getFileName().toString());
        }
    }
}
