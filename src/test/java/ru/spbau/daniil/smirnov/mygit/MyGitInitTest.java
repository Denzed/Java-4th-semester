package ru.spbau.daniil.smirnov.mygit;

import org.junit.Assert;
import org.junit.Test;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitDoubleInitializationException;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitIllegalArgumentException;
import ru.spbau.daniil.smirnov.mygit.objects.Branch;
import ru.spbau.daniil.smirnov.testing.MyGitTest;

import java.nio.file.Paths;
import java.util.Collections;

/**
 * Test set for MyGit init command
 */
public class MyGitInitTest extends MyGitTest {
    @Test
    public void InitTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        actionHandler = new MyGitActionHandler(folderPath);
        checkExistenceAndType(myGitPath, true);
        checkExistenceAndType(myGitHEADPath, false);
        checkExistenceAndType(myGitIndexPath, false);
        checkExistenceAndType(myGitObjectsPath, true);
        checkExistenceAndType(myGitBranchesPath, true);
        Assert.assertEquals(Collections.singletonList(new Branch("master")), actionHandler.listBranches());
    }

    @Test(expected = MyGitDoubleInitializationException.class)
    public void DoubleInitTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler.init(folderPath);
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void NotAbsoluteInitPathTest() throws Exception {
        MyGitActionHandler.init(Paths.get(""));
    }
}
