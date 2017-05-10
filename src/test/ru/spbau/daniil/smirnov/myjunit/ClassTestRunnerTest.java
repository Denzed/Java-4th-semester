package ru.spbau.daniil.smirnov.myjunit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.daniil.smirnov.myjunit.exceptions.CannotConstructClassException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.test.TestFailedException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.test.TestIgnoredException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.test.TestUnexpectedException;
import ru.spbau.daniil.smirnov.myjunit.testing.TestClass;
import ru.spbau.daniil.smirnov.myjunit.testing.TestClassWithoutPublicNullaryConstructor;

import java.io.PrintStream;

/**
 * Test set for {@link ClassTestRunner}
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassTestRunnerTest {
    @InjectMocks
    final private ClassTestRunner<TestClass> classTestRunner =
            new ClassTestRunner<>(TestClass.class,
                                  Mockito.mock(PrintStream.class)); // will be overwritten by injector
    private Class<?> classWithTests = TestClass.class;
    @Spy
    private TestClass classInstance = new TestClass();
    @Mock
    private PrintStream printStream;

    @Before
    public void constructClass() throws Exception {
        final ClassTestRunner<TestClass> testRunner =
                new ClassTestRunner<>(TestClass.class,
                        printStream);
        testRunner.constructClassToTest();
        Mockito.verifyZeroInteractions(printStream);
    }

    @Test(expected = CannotConstructClassException.class)
    public void ClassWithoutPublicNullaryConstructorTest() throws Exception {
        final ClassTestRunner<TestClassWithoutPublicNullaryConstructor> testRunner =
            new ClassTestRunner<>(TestClassWithoutPublicNullaryConstructor.class,
                                  printStream);
        testRunner.constructClassToTest();
        Mockito.verifyZeroInteractions(printStream);
    }

    @Test(expected = TestFailedException.class)
    public void runPrivateTest() throws Exception {
        classTestRunner.runTest(classWithTests.getDeclaredMethod("privateTest"));
        Mockito.verifyZeroInteractions(classInstance);
        Mockito.verifyZeroInteractions(printStream);
    }

    @Test(expected = TestFailedException.class)
    public void runStaticTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("staticTest"));
        Mockito.verifyZeroInteractions(classInstance);
        Mockito.verifyZeroInteractions(printStream);
    }

    @Test(expected = TestIgnoredException.class)
    public void runIgnoredTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("ignoredTest"));
        Mockito.verifyZeroInteractions(classInstance);
        Mockito.verifyZeroInteractions(printStream);
    }

    @Test(expected = TestFailedException.class)
    public void runArgumentTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("testRequiresArguments", int.class));
        Mockito.verifyZeroInteractions(classInstance);
        Mockito.verifyZeroInteractions(printStream);
    }

    @Test(expected = TestUnexpectedException.class)
    public void runUnexpectedExceptionTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("testUnexpectedException"));
        Mockito.verify(classInstance).testUnexpectedException();
        Mockito.verifyZeroInteractions(printStream);
    }

    @Test
    public void runNormalTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("normalTest"));
        Mockito.verify(classInstance).normalTest();
        Mockito.verifyZeroInteractions(printStream);
    }

    @Test
    public void runExpectedExceptionTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("testExpectedException"));
        Mockito.verify(classInstance).testExpectedException();
        Mockito.verifyZeroInteractions(printStream);
    }
}
