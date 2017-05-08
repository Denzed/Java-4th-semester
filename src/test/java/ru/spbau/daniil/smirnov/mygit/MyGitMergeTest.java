package ru.spbau.daniil.smirnov.mygit;

import org.junit.Test;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitIllegalArgumentException;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitMissingPrerequisitesException;
import ru.spbau.daniil.smirnov.testing.MyGitInitialisedTest;

import java.io.File;
import java.io.FileWriter;

/**
 * Test set for MyGit merge command
 */
public class MyGitMergeTest extends MyGitInitialisedTest {
    @Test
    public void MergeTest() throws Exception {
        File file = temporaryFolder.newFile(FILES.get(0));
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(FILE_CONTENTS.get(0));
        }
        String[] paths = {FILE_PATHS.get(0).toString()};
        actionHandler.add(paths);
        actionHandler.commit("added file");
        actionHandler.createBranch(BRANCHES.get(0));
        actionHandler.checkout(BRANCHES.get(0));
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(NEW_FILE_CONTENTS.get(0));
        }
        actionHandler.add(paths);
        actionHandler.commit("modified file in another branch");
        actionHandler.checkout("master");
        actionHandler.merge(BRANCHES.get(0));
        checkFileContents(file, NEW_FILE_CONTENTS.get(0));
    }

    @Test(expected = MyGitMissingPrerequisitesException.class)
    public void MergeWithoutCommitTest() throws Exception {
        temporaryFolder.newFile(FILES.get(0));
        String[] paths = {FILE_PATHS.get(0).toString()};
        actionHandler.add(paths);
        actionHandler.commit("added file");
        actionHandler.createBranch(BRANCHES.get(0));
        actionHandler.checkout(BRANCHES.get(0));
        actionHandler.add(paths);
        actionHandler.merge("master");
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void SelfMergeTest() throws Exception {
        actionHandler.merge("master");
    }
}
