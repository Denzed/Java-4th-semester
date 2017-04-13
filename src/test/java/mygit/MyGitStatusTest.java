package mygit;

import mygit.objects.FileStatus;
import org.junit.Assert;
import org.junit.Test;
import testing.MyGitInitialisedTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Test for MyGit status command
 */
public class MyGitStatusTest extends MyGitInitialisedTest {
    @Test
    public void WorkTest() throws Exception {
        final Path inputsPath = Paths.get(folderPath.toString(), "inputs");
        Files.createDirectory(inputsPath);
        actionHandler.add(new String[]{inputsPath.toString()});
        Map<Path,FileStatus> fileStatusMap = actionHandler.status();
        Assert.assertEquals(1, fileStatusMap.size());
        Assert.assertEquals(FileStatus.STAGED, fileStatusMap.get(inputsPath));
        actionHandler.commit("inputs added");

        final Path input1Path = Paths.get(inputsPath.toString(), "input1.txt");
        Files.createFile(input1Path);
        final Path input2Path = Paths.get(inputsPath.toString(), "input2.txt");
        Files.createFile(input2Path);
        fileStatusMap = actionHandler.status();
        Assert.assertEquals(2, fileStatusMap .size());
        Assert.assertEquals(FileStatus.UNSTAGED, fileStatusMap.get(input1Path));
        Assert.assertEquals(FileStatus.UNSTAGED, fileStatusMap.get(input2Path));
        actionHandler.add(new String[]{input1Path.toString()});
        actionHandler.add(new String[]{input2Path.toString()});
        actionHandler.commit("inputs 1 & 2");

        Files.delete(input1Path);
        fileStatusMap = actionHandler.status();
        Assert.assertEquals(1, fileStatusMap.size());
        Assert.assertEquals(FileStatus.DELETED, fileStatusMap.get(input1Path));
        actionHandler.add(new String[]{input1Path.toString()});
        actionHandler.commit("removed 1");

        Files.write(input2Path, new byte[10]);
        fileStatusMap = actionHandler.status();
        Assert.assertEquals(1, fileStatusMap .size());
        Assert.assertEquals(FileStatus.MODIFIED, fileStatusMap.get(input2Path));
    }
}
