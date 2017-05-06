package ru.spbau.daniil.smirnov.myjunit.exceptions;

/**
 * Base class for all MyJunit exceptions
 */
public class MyJUnitException extends Exception {
    /**
     * Constructs the exception
     * @param message message to show
     */
    public MyJUnitException(String message) {
        super(message);
    }

    /**
     * Constructs the exception with default message
     */
    public MyJUnitException() {
        this("MyJunitException -- an error happened while testing");
    }
}
