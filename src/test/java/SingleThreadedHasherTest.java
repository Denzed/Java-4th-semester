import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileOutputStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test set for single threaded MD5 hasher
 */
public class SingleThreadedHasherTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path folderPath;
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
        FILE_PATHS = FILES.stream().map(name -> Paths.get(folderPathString, name)).collect(Collectors.toList());
        for (int i = 0; i < FILES.size(); i++) {
            temporaryFolder.newFile(FILES.get(i));
            try (FileOutputStream fileOutputStream =
                         new FileOutputStream(FILE_PATHS.get(i).toFile())) {
                fileOutputStream.write(FILE_CONTENTS.get(i).getBytes());
            }
        }
    }

    @Test(expected = NoSuchFileException.class)
    public void noFileTest() throws Exception {
        Path nonExistant = Paths.get(folderPath.getParent().toString(),
                folderPath.getFileName() + "11");
        SingleThreadedMD5Hasher.getHashFromPath(nonExistant);
    }

    @Test
    public void fileHashTest() throws Exception {
        for (int i = 0; i < FILES.size(); i++) {
            Assert.assertArrayEquals(
                    MessageDigest.getInstance("MD5").digest(FILE_CONTENTS.get(i).getBytes()),
                    SingleThreadedMD5Hasher.getHashFromPath(FILE_PATHS.get(i)));
        }
    }

    @Test
    public void subsequentCallTest() throws Exception {
        Assert.assertEquals(SingleThreadedMD5Hasher.getHashFromPath(folderPath),
                SingleThreadedMD5Hasher.getHashFromPath(folderPath));
    }
}
