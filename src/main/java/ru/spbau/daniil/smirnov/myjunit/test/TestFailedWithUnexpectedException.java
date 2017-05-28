package ru.spbau.daniil.smirnov.myjunit.test;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Represents a test run with an exception which does not match the expected one
 */
public class TestFailedWithUnexpectedException extends TestRunResult {
    private final Class<? extends Throwable> expected;

    private final Class<? extends Throwable> actual;

    /**
     * Generates a {@link TestRunResult} of a test run with an exception which does not match the expected one
     * @param runMethod failed method
     * @param expected expected exception
     * @param actual actual exception
     */
    public TestFailedWithUnexpectedException(@NotNull Method runMethod,
                                             @NotNull Class<? extends Throwable> expected,
                                             @NotNull Class<? extends Throwable> actual) {
        super(runMethod);
        this.expected = expected;
        this.actual = actual;
    }

    /**
     * Converts test run result to a readable form
     * @return test run result in a readable form
     */
    @NotNull
    @Override
    public String toString() {
        return String.format("Test %s failed: expected %s instead of %s", runMethod, expected, actual);
    }
}
