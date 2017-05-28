package ru.spbau.daniil.smirnov.myjunit;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myjunit.annotations.Test;
import ru.spbau.daniil.smirnov.myjunit.annotations.TestAnnotationType;
import ru.spbau.daniil.smirnov.myjunit.exceptions.CannotConstructClassException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.InvalidMethodException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.MyJUnitException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.RunningMethodFailedException;
import ru.spbau.daniil.smirnov.myjunit.test.*;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

    /**
     * Constructs the test runner
     * @param classWithTests class which contains tests defined by annotations from
     * {@link ru.spbau.daniil.smirnov.myjunit.annotations} package
     */
    ClassTestRunner(@NotNull Class<T> classWithTests) {
        this.classWithTests = classWithTests;
    }

    /**
     * Runs the tests
     */
    void runTests(@NotNull PrintStream printStream) {
        Map<TestAnnotationType, List<Method>> methodsGrouped = getAndGroupMethods();
        printStream.println(String.format("Running @BeforeClass-annotated methods on class %s", classWithTests));
        try {
            if (classInstance == null) {
                classInstance = constructClassToTest();
            }
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
            TestRunResult testRunResult = runTest(
                    method,
                    methodsGrouped.get(TestAnnotationType.BEFORE),
                    methodsGrouped.get(TestAnnotationType.AFTER));
            printStream.println(testRunResult);
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

    private T constructClassToTest()
            throws CannotConstructClassException {
        try {
            return classWithTests.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CannotConstructClassException(classWithTests);
        }
    }

    private TestRunResult runTest(@NotNull Method testMethod,
                          @NotNull List<Method> beforeTest,
                          @NotNull List<Method> afterTest) {
        String reason = testMethod.getAnnotation(Test.class).ignore();
        if (!reason.equals("")) {
            return new TestIgnored(testMethod, reason);
        }
        long runningTime;
        try {
            for (Method method : beforeTest) {
                runMethod(method);
            }
            long start = System.currentTimeMillis();
            Class<? extends Throwable> expected = testMethod.getAnnotation(Test.class).expected();
            try {
                runMethod(testMethod);
                if (!expected.equals(Test.None.class)) {
                    return new TestFinishedWithoutExpectedException(testMethod, expected);
                }
            } catch (RunningMethodFailedException e) {
                Throwable actual = e.getUnderlying();
                if (!expected.isInstance(actual)) {
                    return new TestFailedWithUnexpectedException(testMethod, expected, actual.getClass());
                }
            }
            runningTime = System.currentTimeMillis() - start;
            for (Method method : afterTest) {
                runMethod(method);
            }
        } catch (MyJUnitException e) {
            return new TestFailedWithInternalException(testMethod, e);
        }
        return new TestSucceeded(testMethod, runningTime);
    }

    private void runMethod(@NotNull Method method)
            throws InvalidMethodException, RunningMethodFailedException {
        if (Modifier.isStatic(method.getModifiers())) {
            throw new InvalidMethodException(method, "should not be static");
        }
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
        Method methods[] = classWithTests.getDeclaredMethods();
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
