package ru.spbau.daniil.smirnov.myjunit.testing;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myjunit.annotations.TestAnnotationType;
import ru.spbau.daniil.smirnov.myjunit.exceptions.CannotConstructClassException;
import ru.spbau.daniil.smirnov.myjunit.exceptions.InvalidMethodException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Runs tests in accordance with annotations from {@link ru.spbau.daniil.smirnov.myjunit.annotations} package
 */
public class TestRunner<T> {
    @NotNull
    private final Class<T> classWithTests;

    private T classInstance;

    @NotNull
    private final OutputStream outputStream;

    /**
     * Constructs the test runner
     * @param classWithTests class which contains tests defined by annotations from
     * {@link ru.spbau.daniil.smirnov.myjunit.annotations} package
     * @param outputStream {@link OutputStream} to write output into
     */
    public TestRunner(@NotNull Class<T> classWithTests,
                      @NotNull OutputStream outputStream) {
        this.classWithTests = classWithTests;
        this.outputStream = outputStream;
    }

    /**
     * Runs the tests
     */
    public void runTests()
            throws CannotConstructClassException, InvalidMethodException, IOException {
        Map<TestAnnotationType, List<Method>> methodsGrouped = getAndGroupMethods();
        constructClassToTest();
        for (Method method : methodsGrouped.get(TestAnnotationType.BEFORE_CLASS)) {
            runMethod(method);
        }
        for (Method method : methodsGrouped.get(TestAnnotationType.TEST)) {
            runTest(method,
                    methodsGrouped.get(TestAnnotationType.BEFORE),
                    methodsGrouped.get(TestAnnotationType.AFTER));
        }
        for (Method method : methodsGrouped.get(TestAnnotationType.AFTER_CLASS)) {
            runMethod(method);
        }
    }

    private void constructClassToTest()
            throws CannotConstructClassException {
        try {
            classInstance = classWithTests.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CannotConstructClassException();
        }
    }

    private void runTest(@NotNull Method testMethod,
                         @NotNull List<Method> beforeTest,
                         @NotNull List<Method> afterTest)
            throws InvalidMethodException {
        // TODO: add annotation parameters handling and other output
        for (Method method : beforeTest) {
            runMethod(method);
        }
        runMethod(testMethod);
        for (Method method : afterTest) {
            runMethod(method);
        }
    }

    private void runMethod(@NotNull Method method)
            throws InvalidMethodException {
        if (method.getParameterCount() != 0) {
            throw new InvalidMethodException("expects parameters");
        }
        if (!method.isAccessible()) {
            throw new InvalidMethodException("not accessible");
        }
        try {
            method.invoke(classInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InvalidMethodException();
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
