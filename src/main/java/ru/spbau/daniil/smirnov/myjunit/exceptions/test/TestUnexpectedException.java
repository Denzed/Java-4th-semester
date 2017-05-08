package ru.spbau.daniil.smirnov.myjunit.exceptions.test;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Thrown if an unexpected exception occurs while running test
 */
public class TestUnexpectedException extends TestException {
    public TestUnexpectedException(@NotNull Method testMethod,
                                   @NotNull Class<? extends Throwable> expected,
                                   @NotNull Class<? extends Throwable> actual) {
        super(String.format("Test %s failed: expected %s instead of %s", testMethod, expected, actual));
    }
}
