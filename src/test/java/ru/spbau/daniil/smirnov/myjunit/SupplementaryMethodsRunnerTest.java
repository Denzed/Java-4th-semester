package ru.spbau.daniil.smirnov.myjunit;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.daniil.smirnov.myjunit.annotations.TestAnnotationType;
import ru.spbau.daniil.smirnov.myjunit.exceptions.CannotConstructClassException;
import ru.spbau.daniil.smirnov.myjunit.test.*;
import ru.spbau.daniil.smirnov.myjunit.testing.TestClass;
import ru.spbau.daniil.smirnov.myjunit.testing.TestClassWithoutPublicNullaryConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static ru.spbau.daniil.smirnov.myjunit.PrivateMethodGetter.getMethodAndMakeItPublic;

/**
 * Test set for {@link ClassTestRunner#runTest} methods
 */
@RunWith(MockitoJUnitRunner.class)
public class SupplementaryMethodsRunnerTest {
    private Class<?> classWithTests = TestClass.class;

    private Method runTestMethod;

    private boolean setUpCompleted = false;

    @Spy
    private TestClass classInstance = new TestClass();

    @InjectMocks
    final private ClassTestRunner<TestClass> classTestRunner =
            new ClassTestRunner<>(TestClass.class); // will be overwritten by injector

    @Before
    public void setUp() throws NoSuchMethodException {
        if (setUpCompleted) {
            return;
        }
        runTestMethod =
                getMethodAndMakeItPublic(ClassTestRunner.class, "runTest", Method.class, List.class, List.class);
        setUpCompleted = true;
    }

    public TestRunResult runTest(@NotNull Method method) throws InvocationTargetException, IllegalAccessException {
        return (TestRunResult) runTestMethod.invoke(
                classTestRunner,
                method,
                Collections.emptyList(),
                Collections.emptyList());
    }

    @Before
    public void constructClass() throws Exception {
        final ClassTestRunner<TestClass> testRunner =
                new ClassTestRunner<>(TestClass.class);
        getMethodAndMakeItPublic(ClassTestRunner.class, "constructClassToTest").invoke(testRunner);
    }

    @Test(expected = CannotConstructClassException.class)
    public void ClassWithoutPublicNullaryConstructorTest() throws Throwable {
        final ClassTestRunner<TestClassWithoutPublicNullaryConstructor> testRunner =
            new ClassTestRunner<>(TestClassWithoutPublicNullaryConstructor.class);
        try {
            getMethodAndMakeItPublic(ClassTestRunner.class, "constructClassToTest").invoke(testRunner);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Test
    public void runPrivateTest() throws Exception {
        TestRunResult testRunResult = runTest(classWithTests.getDeclaredMethod("privateTest"));
        Assert.assertTrue(testRunResult instanceof TestFailedWithInternalException);
        Mockito.verifyZeroInteractions(classInstance);
    }

    @Test
    public void runStaticTest() throws Exception {
        TestRunResult testRunResult = runTest(classWithTests.getMethod("staticTest"));
        Assert.assertTrue(testRunResult instanceof TestFailedWithInternalException);
        Mockito.verifyZeroInteractions(classInstance);
    }

    @Test
    public void runIgnoredTest() throws Exception {
        TestRunResult testRunResult = runTest(classWithTests.getMethod("ignoredTest"));
        Assert.assertTrue(testRunResult instanceof TestIgnored);
        Mockito.verifyZeroInteractions(classInstance);
    }

    @Test
    public void runArgumentTest() throws Exception {
        TestRunResult testRunResult = runTest(classWithTests.getMethod("testRequiresArguments", int.class));
        Assert.assertTrue(testRunResult instanceof TestFailedWithInternalException);
        Mockito.verifyZeroInteractions(classInstance);
    }

    @Test
    public void runUnexpectedExceptionTest() throws Exception {
        TestRunResult testRunResult = runTest(classWithTests.getMethod("testUnexpectedException"));
        Assert.assertTrue(testRunResult instanceof TestFailedWithUnexpectedException);
        Mockito.verify(classInstance).testUnexpectedException();
    }

    @Test
    public void runNormalTest() throws Exception {
        TestRunResult testRunResult = runTest(classWithTests.getMethod("normalTest"));
        Assert.assertTrue(testRunResult instanceof TestSucceeded);
        Mockito.verify(classInstance).normalTest();
    }

    @Test
    public void runExpectedExceptionThrownTest() throws Exception {
        TestRunResult testRunResult = runTest(classWithTests.getMethod("testExpectedExceptionThrown"));
        Assert.assertTrue(testRunResult instanceof TestSucceeded);
        Mockito.verify(classInstance).testExpectedExceptionThrown();
    }

    @Test
    public void runExpectedExceptionNotThrownTest() throws Exception {
        TestRunResult testRunResult = runTest(classWithTests.getMethod("testExpectedExceptionNotThrown"));
        Assert.assertTrue(testRunResult instanceof TestFinishedWithoutExpectedException);
        Mockito.verify(classInstance).testExpectedExceptionNotThrown();
    }

    @Test
    public void getAndGroupMethodsTest() throws Exception {
        Object invocationResult =
                getMethodAndMakeItPublic(ClassTestRunner.class, "getAndGroupMethods").invoke(classTestRunner);
        Map<TestAnnotationType,List<Method>> methodsGrouped = new HashMap<>();
        if (methodsGrouped.getClass().isInstance(invocationResult)) {
            //noinspection unchecked
            methodsGrouped.putAll((Map<? extends TestAnnotationType, ? extends List<Method>>) invocationResult);
        } else {
            Assert.fail();
        }
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
                "testExpectedExceptionThrown",
                "testExpectedExceptionNotThrown",
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
}
