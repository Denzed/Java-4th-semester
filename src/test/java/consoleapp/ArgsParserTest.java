package consoleapp;

import mygit.MyGitActionHandler;
import org.junit.Test;
import testing.TestWithTemporaryFolder;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.mock;

/**
 * Test set for the console app argument parser
 */
public class ArgsParserTest extends TestWithTemporaryFolder {

    private static final PrintStream MOCK_STREAM = mock(PrintStream.class);

    private ArgsParser parser;

    @Override
    public void initialise() throws Exception {
        super.initialise();
        parser = new ArgsParser(MOCK_STREAM, folderPath);
    }

    @Test(expected = InvalidCommandException.class)
    public void parseEmptyTest() throws Exception {
        parser.parse(new String[0]);
    }

    @Test
    public void parseHelpTest() throws Exception {
        final String[] args = {"help"};
        parser.parse(args);
    }

    @Test
    public void parseInitTest() throws Exception {
        final String[] args = {"init"};
        parser.parse(args);
    }

    @Test
    public void parseResetTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        final String[] args = {"reset", folderPath.toString()};
        parser.parse(args);
    }

    @Test
    public void parseRmTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        final Path file = Paths.get(folderPath.toString(), "greeting.txt");
        Files.createFile(file);
        final String[] args = {"rm", file.toString()};
        parser.parse(args);
    }

    @Test
    public void parseCleanTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        final String[] args = {"clean"};
        parser.parse(args);
    }

    @Test
    public void parseLogTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        final String[] args = {"log"};
        parser.parse(args);
    }

    @Test
    public void parseStatusTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        final String[] args = {"status"};
        parser.parse(args);
    }

    @Test
    public void parseBranchTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        final String[] argsToList = {"branch"};
        parser.parse(argsToList);
        final String[] argsToCreate = {"branch", "test"};
        parser.parse(argsToCreate);
        final String[] argsToDelete = {"branch", "-d", "test"};
        parser.parse(argsToDelete);
    }

    @Test
    public void parseCheckoutTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        final String[] args = {"checkout", "master"};
        parser.parse(args);
    }

    @Test
    public void parseCommitTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        final Path file = Paths.get(folderPath.toString(), "greeting.txt");
        Files.createFile(file);
        final String[] args1 = {"add", file.toString()};
        parser.parse(args1);
        final String[] args2 = {"commit", "hello"};
        parser.parse(args2);
    }

    @Test
    public void parseMergeTest() throws Exception {
        MyGitActionHandler.init(folderPath);
        final String[] branchCreateArgs = {"branch", "test"};
        parser.parse(branchCreateArgs);
        final String[] args = {"merge", "test"};
        parser.parse(args);
    }
}