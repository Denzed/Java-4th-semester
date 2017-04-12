package mygit.objects;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link Tree.TreeEdge} class
 */
public class TreeEdgeTest {
    @Test
    public void isDirectoryTest() throws Exception {
        final Tree.TreeEdge edgeDirectory = new Tree.TreeEdge("hash", "git", Tree.TYPE);
        Assert.assertTrue(edgeDirectory.isDirectory());
        final Tree.TreeEdge edgeFile = new Tree.TreeEdge("hash", "git", Blob.TYPE);
        Assert.assertFalse(edgeFile.isDirectory());
    }
}