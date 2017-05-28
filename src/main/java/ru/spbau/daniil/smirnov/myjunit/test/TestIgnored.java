package ru.spbau.daniil.smirnov.myjunit.test;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myjunit.annotations.Test;

import java.lang.reflect.Method;

/**
 * Represents ignored test (if the test method has a non-empty {@link Test#ignore} string attribute)
 */
public class TestIgnored extends TestRunResult {
    @NotNull
    private final String reason;

    /**
     * Constructs a {@link TestRunResult} of an ignored test
     * @param runMethod tested method
     * @param reason reason for skipping
     */
    public TestIgnored(@NotNull Method runMethod, @NotNull String reason) {
        super(runMethod);
        this.reason = reason;
    }

    /**
     * Converts test run result to a readable form
     * @return test run result in a readable form
     */
    @NotNull
    @Override
    public String toString() {
        return String.format("Test %s ignored because:\n%s", runMethod, reason);
    }
}
