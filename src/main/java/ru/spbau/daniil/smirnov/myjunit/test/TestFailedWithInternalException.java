package ru.spbau.daniil.smirnov.myjunit.test;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myjunit.exceptions.MyJUnitException;

import java.lang.reflect.Method;

/**
 * Represents a test failed because of a MyJUnit-internal exception
 */
public class TestFailedWithInternalException extends TestRunResult {
    @NotNull
    private final MyJUnitException causeOfFail;

    /**
     * Constructs a {@link TestRunResult} of a test failed because of a MyJUnit-internal exception
     * @param runMethod failed method
     * @param causeOfFail {@link MyJUnitException} which caused the fail
     */
    public TestFailedWithInternalException(@NotNull Method runMethod,
                                           @NotNull MyJUnitException causeOfFail) {
        super(runMethod);
        this.causeOfFail = causeOfFail;
    }

    /**
     * Converts test run result to a readable form
     * @return test run result in a readable form
     */
    @NotNull
    @Override
    public String toString() {
        return String.format(
                "Test %s failed due to a MyJUnit-internal exception\n: %s",
                runMethod,
                causeOfFail);
    }
}
