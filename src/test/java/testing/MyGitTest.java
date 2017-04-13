package testing;

import mygit.MyGitActionHandler;
import org.junit.Assert;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MyGitTest extends TestWithTemporaryFolder {
    protected Path myGitPath, myGitIndexPath, myGitObjectsPath, myGitHEADPath, myGitBranchesPath;
    protected final List<String> FILES = Arrays.asList("file1", "file2", "file3");
    protected List<Path> FILE_PATHS;
    protected final List<String> FILE_CONTENTS = Arrays.asList("file1_contents", "file2_contents", "file3_contents");
    protected final List<String> NEW_FILE_CONTENTS =
            Arrays.asList("new_file1_contents", "new_file2_contents", "new_file3_contents");
    protected final List<String> BRANCHES = Arrays.asList("branch1", "branch2", "branch3");

    protected MyGitActionHandler actionHandler;

    @Before
    public void initialise() throws Exception {
        super.initialise();
        String folderPathString = folderPath.toString();
        myGitPath = Paths.get(folderPathString, ".mygit");
        myGitIndexPath = Paths.get(folderPathString, ".mygit", "index");
        myGitObjectsPath = Paths.get(folderPathString, ".mygit", "objects");
        myGitBranchesPath = Paths.get(folderPathString, ".mygit", "branches");
        myGitHEADPath = Paths.get(folderPathString, ".mygit", "HEAD");
        FILE_PATHS = FILES.stream().map(name -> Paths.get(folderPathString, name)).collect(Collectors.toList());
    }

    protected void checkExistenceAndType(Path path, boolean isDirectory) {
        Assert.assertTrue(Files.exists(path));
        Assert.assertTrue(Files.isDirectory(path) == isDirectory);
    }

    static protected void checkFileContents(File file, String expectedContents) throws IOException {
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
