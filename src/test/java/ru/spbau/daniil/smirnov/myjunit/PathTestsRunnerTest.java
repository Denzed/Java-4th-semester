package ru.spbau.daniil.smirnov.myjunit;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ru.spbau.daniil.smirnov.myjunit.testing.TestClass;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static ru.spbau.daniil.smirnov.myjunit.PrivateMethodGetter.getMethodAndMakeItPublic;

/**
 * Test set for {@link PathTestsRunner}
 */
public class PathTestsRunnerTest {
    @NotNull
    final private Class<TestClass> testClass = TestClass.class;

    private File testFile;
    private PrintStream printStream;
    private PathTestsRunner pathTestsRunner;

    @Before
    @Test
    public void initialise() throws IOException, URISyntaxException {
        URL url = testClass.getClassLoader().getResource(
                testClass.getName().replace('.', '/') + ".class");
        Assert.assertNotNull(url);
        Path loadedFromPath = Paths.get(url.toURI());
        testFile = loadedFromPath.toFile();

        printStream = Mockito.mock(PrintStream.class);
        pathTestsRunner = Mockito.spy(new PathTestsRunner(loadedFromPath, printStream));
    }

    @Test
    public void findClassFilesInPathTest() throws Exception {
        Assert.assertEquals(
                Collections.singletonList(testFile.toPath()),
                getMethodAndMakeItPublic(PathTestsRunner.class,
                                         "findClassFilesInPath",
                                         Path.class).invoke(null, testFile.toPath()));
    }

    @Test
    public void loadClassAndRunTestsTest() throws Exception {
        getMethodAndMakeItPublic(PathTestsRunner.class,
                "loadClassAndRunTests",
                File.class).invoke(pathTestsRunner, testFile);
        Mockito.verify(printStream, Mockito.never()).printf(
                Mockito.eq("Loading class from file %s failed with message:\n%s\n%s\n"),
                Mockito.anyVararg());
    }

    @Test
    public void pathTestsRunnerTest() throws Exception {
        pathTestsRunner.runTests();

        Mockito.verify(printStream, Mockito.never()).printf(
                Mockito.eq("An error occurred while reading file tree:\n%s\n"),
                Mockito.anyString());
    }
}
