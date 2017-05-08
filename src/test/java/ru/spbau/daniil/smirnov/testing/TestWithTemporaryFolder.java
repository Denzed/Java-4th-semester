package ru.spbau.daniil.smirnov.testing;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;

/**
 * A base class for tests which use {@link TemporaryFolder}
 */
public class TestWithTemporaryFolder {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected Path folderPath;

    @Before
    public void initialise() throws Exception {
        folderPath = temporaryFolder.getRoot().toPath();
    }
}
