package ru.spbau.daniil.smirnov.myjunit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.daniil.smirnov.myjunit.annotations.TestAnnotationType;
import ru.spbau.daniil.smirnov.myjunit.exceptions.CannotConstructClassException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.test.TestFailedException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.test.TestIgnoredException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.test.TestUnexpectedException;
import ru.spbau.daniil.smirnov.myjunit.testing.TestClass;
import ru.spbau.daniil.smirnov.myjunit.testing.TestClassWithoutPublicNullaryConstructor;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Test set for {@link ClassTestRunner#runTest} methods
 */
@RunWith(MockitoJUnitRunner.class)
public class SupplementaryMethodsRunnerTest {
    private Class<?> classWithTests = TestClass.class;

    @Spy
    private TestClass classInstance = new TestClass();

    @Mock
    private PrintStream printStream;

    @InjectMocks
    final private ClassTestRunner<TestClass> classTestRunner =
            new ClassTestRunner<>(TestClass.class,
                    Mockito.mock(PrintStream.class)); // will be overwritten by injector

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
    }

    @Test(expected = TestFailedException.class)
    public void runPrivateTest() throws Exception {
        classTestRunner.runTest(classWithTests.getDeclaredMethod("privateTest"));
        Mockito.verifyZeroInteractions(classInstance);
    }

    @Test(expected = TestFailedException.class)
    public void runStaticTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("staticTest"));
        Mockito.verifyZeroInteractions(classInstance);
    }

    @Test(expected = TestIgnoredException.class)
    public void runIgnoredTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("ignoredTest"));
        Mockito.verifyZeroInteractions(classInstance);
    }

    @Test(expected = TestFailedException.class)
    public void runArgumentTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("testRequiresArguments", int.class));
        Mockito.verifyZeroInteractions(classInstance);
    }

    @Test(expected = TestUnexpectedException.class)
    public void runUnexpectedExceptionTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("testUnexpectedException"));
        Mockito.verify(classInstance).testUnexpectedException();
    }

    @Test
    public void runNormalTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("normalTest"));
        Mockito.verify(classInstance).normalTest();
    }

    @Test
    public void runExpectedExceptionTest() throws Exception {
        classTestRunner.runTest(classWithTests.getMethod("testExpectedException"));
        Mockito.verify(classInstance).testExpectedException();
    }

    @Test
    public void getAndGroupMethodsTest() throws Exception {
        Map<TestAnnotationType,List<Method>> methodsGrouped = classTestRunner.getAndGroupMethods();
        Assert.assertEquals(Collections.singletonList(classWithTests.getMethod("testBeforeClass")),
                            methodsGrouped.get(TestAnnotationType.BEFORE_CLASS));
        Assert.assertEquals(Collections.singletonList(classWithTests.getMethod("testAfterClass")),
                            methodsGrouped.get(TestAnnotationType.AFTER_CLASS));
        Assert.assertEquals(Collections.singletonList(classWithTests.getMethod("testBefore")),
                            methodsGrouped.get(TestAnnotationType.BEFORE));
        Assert.assertEquals(Collections.singletonList(classWithTests.getMethod("testAfter")),
                            methodsGrouped.get(TestAnnotationType.AFTER));
        final String testNames[] = new String[]{
                "staticTest",
                "testExpectedException",
                "testUnexpectedException",
                "normalTest",
                "privateTest",
                "ignoredTest"};
        final Set<Method> testMethodList = new HashSet<>();
        testMethodList.add(classWithTests.getDeclaredMethod("testRequiresArguments", int.class));
        for (String testName : testNames) {
            testMethodList.add(classWithTests.getDeclaredMethod(testName));
        }
        Assert.assertEquals(testMethodList,
                            new HashSet<>(methodsGrouped.get(TestAnnotationType.TEST)));
    }

    @After
    public void verifyNoPrints() {
        Mockito.verifyZeroInteractions(printStream);
    }
}
