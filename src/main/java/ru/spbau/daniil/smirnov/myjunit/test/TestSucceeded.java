package ru.spbau.daniil.smirnov.myjunit.test;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Represents a successful test run
 */
public class TestSucceeded extends TestRunResult {
    private final long runningTime;

    /**
     * Constructs a {@link TestRunResult} of a successful test run
     * @param runMethod tested method
     * @param runningTime its running time
     */
    public TestSucceeded(@NotNull Method runMethod, long runningTime) {
        super(runMethod);
        this.runningTime = runningTime;
    }

    /**
     * Converts test run result to a readable form
     * @return test run result in a readable form
     */
    @NotNull
    @Override
    public String toString() {
        return String.format("Test %s succeeded in %s milliseconds", runMethod, runningTime);
    }
}
