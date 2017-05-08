package ru.spbau.daniil.smirnov.myjunit.exceptions.test;

/**
 * Base class for all exception related to tests. It is made independent of
 * {@link ru.spbau.daniil.smirnov.myjunit.exceptions.MyJUnitException}.
 */
public class TestException extends Exception {
    /**
     * Constructs the exception
     * @param message message to show
     */
    TestException(String message) {
        super(message);
    }

    /**
     * Constructs the exception with default message
     */
    public TestException() {
        this("TestException -- an error happened while running test");
    }
}
