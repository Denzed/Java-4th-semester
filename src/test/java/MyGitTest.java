import mygit.MyGitActionHandler;
import mygit.exceptions.MyGitDoubleInitializationException;
import mygit.exceptions.MyGitEmptyCommitException;
import mygit.exceptions.MyGitIllegalArgumentException;
import mygit.exceptions.MyGitMissingPrerequisitesException;
import mygit.objects.Branch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MyGitTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path folderPath, myGitPath, myGitIndexPath, myGitObjectsPath, myGitHEADPath, myGitBranchesPath;
    private final List<String> FILES = Arrays.asList("file1", "file2", "file3");
    private List<Path> FILE_PATHS;
    private final List<String> FILE_CONTENTS = Arrays.asList("file1_contents", "file2_contents", "file3_contents");
    private final List<String> NEW_FILE_CONTENTS =
            Arrays.asList("new_file1_contents", "new_file2_contents", "new_file3_contents");
    private final List<String> BRANCHES = Arrays.asList("branch1", "branch2", "branch3");

    @Before
    public void initialise() throws Exception {
        folderPath = temporaryFolder.getRoot().toPath();
        String folderPathString = folderPath.toString();
        myGitPath = Paths.get(folderPathString, ".mygit");
        myGitIndexPath = Paths.get(folderPathString, ".mygit", "index");
        myGitObjectsPath = Paths.get(folderPathString, ".mygit", "objects");
        myGitBranchesPath = Paths.get(folderPathString, ".mygit", "branches");
        myGitHEADPath = Paths.get(folderPathString, ".mygit", "HEAD");
        FILE_PATHS = FILES.stream().map(name -> Paths.get(folderPathString, name)).collect(Collectors.toList());
    }

    private void checkExistenceAndType(Path path, boolean isDirectory) {
        Assert.assertTrue(Files.exists(path));
        Assert.assertTrue(Files.isDirectory(path) == isDirectory);
    }

    @Test
    public void InitTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        checkExistenceAndType(myGitPath, true);
        checkExistenceAndType(myGitHEADPath, false);
        checkExistenceAndType(myGitIndexPath, false);
        checkExistenceAndType(myGitObjectsPath, true);
        checkExistenceAndType(myGitBranchesPath, true);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        Assert.assertEquals(Collections.singletonList(new Branch("master")), actionHandler.listBranches());
    }

    @Test(expected = MyGitDoubleInitializationException.class)
    public void DoubleInitTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler.init(folderPath);
    }

    @Test
    public void BranchTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
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
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        actionHandler.createBranch(BRANCHES.get(0));
        actionHandler.createBranch(BRANCHES.get(0));
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void DoubleBranchDeleteTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        actionHandler.createBranch(BRANCHES.get(0));
        actionHandler.deleteBranch(BRANCHES.get(0));
        actionHandler.deleteBranch(BRANCHES.get(0));
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void MissingBranchDeleteTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        actionHandler.deleteBranch(BRANCHES.get(0));
    }

    @Test
    public void AddTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        for (String filename : FILES) {
            temporaryFolder.newFile(filename);
        }
        for (Path path : FILE_PATHS) {
            String[] paths = {path.toString()};
            actionHandler.addPathsToIndex(paths);
        }
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void AddMissingFileTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        String[] paths = {FILE_PATHS.get(0).toString()};
        actionHandler.addPathsToIndex(paths);
    }

    @Test
    public void CommitTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        for (String filename : FILES) {
            temporaryFolder.newFile(filename);
        }
        for (Path path : FILE_PATHS) {
            String[] paths = {path.toString()};
            actionHandler.addPathsToIndex(paths);
        }
        actionHandler.commit("added some files");
    }

    @Test(expected = MyGitEmptyCommitException.class)
    public void EmptyCommitTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        actionHandler.commit("added some files (no)");
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void EmptyCommitMessageTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        for (String filename : FILES) {
            temporaryFolder.newFile(filename);
        }
        for (Path path : FILE_PATHS) {
            String[] paths = {path.toString()};
            actionHandler.addPathsToIndex(paths);
        }
        actionHandler.commit("");
    }

    @Test(expected = MyGitMissingPrerequisitesException.class)
    public void CheckoutWithoutCommitTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        File file = temporaryFolder.newFile(FILES.get(0));
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(FILE_CONTENTS.get(0));
        }
        String[] paths = {FILE_PATHS.get(0).toString()};
        actionHandler.addPathsToIndex(paths);
        actionHandler.createBranch(BRANCHES.get(0));
        actionHandler.checkout(BRANCHES.get(0));
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void MissingBranchCheckoutTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        actionHandler.checkout(BRANCHES.get(0));
    }

    @Test
    public void MergeTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        File file = temporaryFolder.newFile(FILES.get(0));
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(FILE_CONTENTS.get(0));
        }
        String[] paths = {FILE_PATHS.get(0).toString()};
        actionHandler.addPathsToIndex(paths);
        actionHandler.commit("added file");
        actionHandler.createBranch(BRANCHES.get(0));
        actionHandler.checkout(BRANCHES.get(0));
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(NEW_FILE_CONTENTS.get(0));
        }
        actionHandler.addPathsToIndex(paths);
        actionHandler.commit("modified file in another branch");
        actionHandler.checkout("master");
        actionHandler.merge(BRANCHES.get(0));
        checkFileContents(file, NEW_FILE_CONTENTS.get(0));
    }

    @Test(expected = MyGitMissingPrerequisitesException.class)
    public void MergeWithoutCommitTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        temporaryFolder.newFile(FILES.get(0));
        String[] paths = {FILE_PATHS.get(0).toString()};
        actionHandler.addPathsToIndex(paths);
        actionHandler.commit("added file");
        actionHandler.createBranch(BRANCHES.get(0));
        actionHandler.checkout(BRANCHES.get(0));
        actionHandler.addPathsToIndex(paths);
        actionHandler.merge("master");
    }

    @Test(expected = MyGitIllegalArgumentException.class)
    public void SelfMergeTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        MyGitActionHandler actionHandler = new MyGitActionHandler(folderPath);
        actionHandler.merge("master");
    }

    static private void checkFileContents(File file, String expectedContents) throws IOException {
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            if (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            while (line != null) {
                stringBuilder.append("\n");
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            Assert.assertEquals(expectedContents, stringBuilder.toString());
        }
    }
}
