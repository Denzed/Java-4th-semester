package mygit;

import mygit.objects.FileStatus;
import org.junit.Assert;
import org.junit.Test;
import testing.MyGitInitialisedTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

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
        final Path directory = Paths.get(folderPath.toString(), "out");
        Files.createDirectory(directory);
        final Path fileInDirectory = Paths.get(directory.toString(), "out1");
        Files.createFile(fileInDirectory);
        actionHandler.clean();

        final Map<Path,FileStatus> differences = actionHandler.status();
        Assert.assertEquals(1, differences.size());
        for (Path path : differences.keySet()) {
            Assert.assertEquals(FileStatus.STAGED, differences.get(path));
            Assert.assertEquals("in1", path.getFileName().toString());
        }

        Assert.assertEquals(2, Files.list(folderPath).collect(Collectors.toList()).size());
    }
}
