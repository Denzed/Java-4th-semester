package ru.spbau.daniil.smirnov.myjunit.exceptions.test;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myjunit.annotations.Test;

import java.lang.reflect.Method;

/**
 * Thrown if the test method has a non-empty {@link Test#ignore} string attribute
 */
public class TestIgnoredException extends TestException {
    public TestIgnoredException(@NotNull Method testMethod, @NotNull String reason) {
        super(String.format("Test %s ignored due to: %s", testMethod, reason));
    }
}
