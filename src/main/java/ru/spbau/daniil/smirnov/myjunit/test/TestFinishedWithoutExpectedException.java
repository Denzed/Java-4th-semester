package ru.spbau.daniil.smirnov.myjunit.test;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Represents a test run without an exception although it expected one
 */
public class TestFinishedWithoutExpectedException extends TestRunResult {
    @NotNull
    private final Class<? extends Throwable> expected;

    /**
     * Constructs a {@link TestRunResult} of a test run without an exception although it expected one
     * @param runMethod failed test
     * @param expected the expected exception
     */
    public TestFinishedWithoutExpectedException(@NotNull Method runMethod,
                                                @NotNull Class<? extends Throwable> expected) {
        super(runMethod);
        this.expected = expected;
    }

    /**
     * Converts test run result to a readable form
     * @return test run result in a readable form
     */
    @NotNull
    @Override
    public String toString() {
        return String.format(
                "Test %s finished without an exception however it expected %s to occur",
                runMethod,
                expected);
    }
}
