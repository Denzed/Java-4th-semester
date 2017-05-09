package ru.spbau.daniil.smirnov.myjunit;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myjunit.annotations.Test;
import ru.spbau.daniil.smirnov.myjunit.annotations.TestAnnotationType;
import ru.spbau.daniil.smirnov.myjunit.exceptions.CannotConstructClassException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.InvalidMethodException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.MyJUnitException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.RunningMethodFailedException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.test.TestException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.test.TestFailedException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.test.TestIgnoredException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.test.TestUnexpectedException;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Runs tests in single .class file in accordance with annotations from
 * {@link ru.spbau.daniil.smirnov.myjunit.annotations} package
 */
class ClassTestRunner<T> {
    @NotNull
    private final Class<T> classWithTests;

    private T classInstance;

    @NotNull
    private final PrintStream printStream;

    /**
     * Constructs the test runner
     * @param classWithTests class which contains tests defined by annotations from
     * {@link ru.spbau.daniil.smirnov.myjunit.annotations} package
     * @param printStream {@link PrintStream} to write output into
     */
    ClassTestRunner(@NotNull Class<T> classWithTests,
                           @NotNull PrintStream printStream) {
        this.classWithTests = classWithTests;
        this.printStream = printStream;
    }

    /**
     * Runs the tests
     */
    void runTests() {
        Map<TestAnnotationType, List<Method>> methodsGrouped = getAndGroupMethods();
        printStream.println(String.format("Running @BeforeClass-annotated methods on class %s", classWithTests));
        try {
            constructClassToTest();
            for (Method method : methodsGrouped.get(TestAnnotationType.BEFORE_CLASS)) {
                runMethod(method);
            }
        } catch (MyJUnitException e) {
            printStream.println(
                    String.format(
                            "Tests won't be run on class %s due to:\n%s",
                            classWithTests,
                            e.getMessage()));
            return;
        }
        printStream.println(String.format("Running tests on class %s", classWithTests));
        for (Method method : methodsGrouped.get(TestAnnotationType.TEST)) {
            try {
                long runningTime = runTest(
                        method,
                        methodsGrouped.get(TestAnnotationType.BEFORE),
                        methodsGrouped.get(TestAnnotationType.AFTER));
                printStream.println(String.format("Test %s succeeded in %d milliseconds", method, runningTime));
            } catch (TestException e) {
                printStream.println(e.getMessage());
            }
        }
        printStream.println(String.format("Running @AfterClass-annotated methods on class %s", classWithTests));
        for (Method method : methodsGrouped.get(TestAnnotationType.AFTER_CLASS)) {
            try {
                runMethod(method);
            } catch (MyJUnitException e) {
                printStream.println(e.getMessage());
            }
        }
        printStream.println(String.format("Finished running tests on class %s", classWithTests));
    }

    private void constructClassToTest()
            throws CannotConstructClassException {
        try {
            classInstance = classWithTests.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CannotConstructClassException(classWithTests);
        }
    }

    private long runTest(@NotNull Method testMethod,
                         @NotNull List<Method> beforeTest,
                         @NotNull List<Method> afterTest)
            throws TestIgnoredException, TestFailedException, TestUnexpectedException {
        String reason = testMethod.getAnnotation(Test.class).ignore();
        if (!reason.equals("")) {
            throw new TestIgnoredException(testMethod, reason);
        }
        long runningTime;
        try {
            for (Method method : beforeTest) {
                runMethod(method);
            }
            long start = System.currentTimeMillis();
            try {
                runMethod(testMethod);
                throw new RunningMethodFailedException(testMethod, new Test.None());
            } catch (RunningMethodFailedException e) {
                Class<? extends Throwable> expected = testMethod.getAnnotation(Test.class).expected();
                Throwable actual = e.getUnderlying();
                if (!expected.isInstance(actual)) {
                    throw new TestUnexpectedException(testMethod, expected, actual.getClass());
                }
            }
            runningTime = System.currentTimeMillis() - start;
            for (Method method : afterTest) {
                runMethod(method);
            }
        } catch (MyJUnitException e) {
            throw new TestFailedException(testMethod, e);
        }
        return runningTime;
    }

    private void runMethod(@NotNull Method method)
            throws InvalidMethodException, RunningMethodFailedException {
        if (method.getParameterCount() != 0) {
            throw new InvalidMethodException(method, "expects parameters");
        }
        try {
            method.invoke(classInstance);
        } catch (IllegalAccessException e) {
            throw new InvalidMethodException(method, "is not accessible");
        } catch (InvocationTargetException e) {
            throw new RunningMethodFailedException(method, e.getTargetException());
        }
    }

    private Map<TestAnnotationType, List<Method>> getAndGroupMethods() {
        Method methods[] = classWithTests.getMethods();
        Map<TestAnnotationType,List<Method>> methodsGrouped = new HashMap<>();
        for (TestAnnotationType type : TestAnnotationType.values()) {
            methodsGrouped.put(type, new LinkedList<>());
            Class<? extends Annotation> testAnnotation = type.getAssociatedClass();
            for (Method method : methods) {
                for (Annotation annotation : method.getAnnotations()) {
                    if (testAnnotation.isInstance(annotation)) {
                        methodsGrouped.get(type).add(method);
                    }
                }
            }
        }
        return methodsGrouped;
    }
}
