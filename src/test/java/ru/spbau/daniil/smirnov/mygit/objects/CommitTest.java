package ru.spbau.daniil.smirnov.mygit.objects;

import org.junit.Assert;
import org.junit.Test;

import static java.lang.Thread.sleep;

/**
 * Test set for {@link Commit} MyGit class
 */
public class CommitTest {
    @Test
    public void equalsTest() throws Exception {
        final Commit commit1 = new Commit("treeHash1");
        sleep(100);
        final Commit commit2 = new Commit("treeHash1");
        Assert.assertFalse(commit1.equals(commit2));
    }

    @Test
    public void compareToTest() throws Exception {
        final Commit commit1 = new Commit("treeHash1");
        final Commit commit2 = new Commit("treeHash2");
        sleep(100);
        final Commit commit3 = new Commit("treeHash3");
        Assert.assertTrue(commit1.compareTo(commit2) < 0);
        Assert.assertTrue(commit2.compareTo(commit3) < 0);
        Assert.assertTrue(commit1.compareTo(commit3) < 0);
        Assert.assertFalse(commit2.compareTo(commit1) < 0);
        Assert.assertFalse(commit3.compareTo(commit2) < 0);
        Assert.assertFalse(commit3.compareTo(commit1) < 0);
    }

}