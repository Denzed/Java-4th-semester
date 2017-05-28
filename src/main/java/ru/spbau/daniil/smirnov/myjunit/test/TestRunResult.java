package ru.spbau.daniil.smirnov.myjunit.test;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Base class for all test run results
 */
public abstract class TestRunResult {
    @NotNull
    final protected Method runMethod;

    TestRunResult(@NotNull Method runMethod) {
        this.runMethod = runMethod;
    }

    /**
     * Converts test run result to a readable form
     * @return test run result in a readable form
     */
    abstract public String toString();
}
