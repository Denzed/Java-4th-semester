package ru.spbau.daniil.smirnov.mygit;

import org.junit.Assert;
import org.junit.Test;
import ru.spbau.daniil.smirnov.testing.MyGitInitialisedTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test for MyGit reset command
 */
public class MyGitResetTest extends MyGitInitialisedTest {
    @Test
    public void WorkTest() throws Exception {
        final Path path = Paths.get(folderPath.toString(), "input");
        Files.write(path, "1".getBytes());
        actionHandler.add(new String[]{path.toString()});
        actionHandler.commit("1");
        Files.write(path, "2".getBytes());
        actionHandler.reset(new String[]{path.toString()});
        Assert.assertArrayEquals("1".getBytes(), Files.readAllBytes(path));
    }
}
