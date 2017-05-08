package ru.spbau.daniil.smirnov.myjunit.exceptions.test;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myjunit.exceptions.MyJUnitException;

import java.lang.reflect.Method;

/**
 * Thrown if the test method fails
 */
public class TestFailedException extends TestException {
    public TestFailedException(@NotNull Method testMethod, MyJUnitException e) {
        super(String.format("Test %s failed due to exception\n: %s", testMethod, e));
    }
}
