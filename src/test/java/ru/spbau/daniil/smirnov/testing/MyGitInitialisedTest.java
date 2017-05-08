package ru.spbau.daniil.smirnov.testing;

import org.junit.Before;
import ru.spbau.daniil.smirnov.mygit.MyGitActionHandler;

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
