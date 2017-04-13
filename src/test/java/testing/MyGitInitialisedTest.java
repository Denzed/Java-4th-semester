package testing;

import mygit.MyGitActionHandler;
import org.junit.Before;

/**
 * Base class for tests with initialised MyGit repository
 */
public class MyGitInitialisedTest extends MyGitTest {
    @Override
    @Before
    public void initialise() throws Exception {
        super.initialise();
        MyGitActionHandler.init(folderPath);
        actionHandler = new MyGitActionHandler(folderPath);
    }
}
