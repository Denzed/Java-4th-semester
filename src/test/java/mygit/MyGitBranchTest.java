package mygit;

import mygit.exceptions.MyGitIllegalArgumentException;
import mygit.objects.Branch;
import org.junit.Assert;
import org.junit.Test;
import testing.MyGitInitialisedTest;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Test set for MyGit branch command
 */
public class MyGitBranchTest extends MyGitInitialisedTest {
    @Test
    public void BranchTest() throws Exception {
        for (String branchName : BRANCHES) {
            actionHandler.createBranch(branchName);
        }
        Set<String> expectedBranches = new HashSet<>(BRANCHES);
        expectedBranches.add("master");
        Assert.assertEquals(expectedBranches,
                actionHandler.listBranches().stream().map(Branch::getName).collect(Collectors.toSet()));
        actionHandler.deleteBranch(BRANCHES.get(0));
        expectedBranches.remove(BRANCHES.get(0));
        Assert.assertEquals(expectedBranches,
                actionHandler.listBranches().stream().map(Branch::getName).collect(Collectors.toSet()));
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void DoubleBranchCreateTest() throws Exception {
        actionHandler.createBranch(BRANCHES.get(0));
        actionHandler.createBranch(BRANCHES.get(0));
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void DoubleBranchDeleteTest() throws Exception {
        actionHandler.createBranch(BRANCHES.get(0));
        actionHandler.deleteBranch(BRANCHES.get(0));
        actionHandler.deleteBranch(BRANCHES.get(0));
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void MissingBranchDeleteTest() throws Exception {
        actionHandler.deleteBranch(BRANCHES.get(0));
    }
}
