package ru.spbau.daniil.smirnov.myjunit;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.daniil.smirnov.myjunit.testing.TestClass;

import java.io.PrintStream;

/**
 * Test set for {@link ClassTestRunner#runTest} methods
 */
@RunWith(MockitoJUnitRunner.class)
public class RunTestsRunnerTest {
    final private int TOTAL_TESTS = 8;
    final private int IGNORED_TESTS = 1;
    final private int SUCCESSFUL_TESTS = 2;

    @NotNull
    final private PrintStream printStream = Mockito.mock(PrintStream.class);

    @NotNull
    @Spy
    final TestClass classInstance = new TestClass();

    @NotNull
    @InjectMocks
    final private ClassTestRunner<TestClass> classTestRunner = new ClassTestRunner<>(TestClass.class);

    @Test
    public void runTestsTest() throws Exception {
        classTestRunner.runTests(printStream);
        Mockito.verify(classInstance).testBeforeClass();
        Mockito.verify(classInstance).testAfterClass();
        Mockito.verify(classInstance).testExpectedExceptionThrown();
        Mockito.verify(classInstance).testExpectedExceptionNotThrown();
        Mockito.verify(classInstance).testUnexpectedException();
        Mockito.verify(classInstance, Mockito.never()).testRequiresArguments(
                Mockito.anyInt());
        Mockito.verify(classInstance, Mockito.never()).ignoredTest();
        Mockito.verify(classInstance, Mockito.times(TOTAL_TESTS - IGNORED_TESTS)).testBefore();
        Mockito.verify(classInstance, Mockito.times(SUCCESSFUL_TESTS)).testAfter();
        Mockito.verify(printStream, Mockito.atLeastOnce()).println(Mockito.anyString());
    }
}
