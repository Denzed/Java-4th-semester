package ru.spbau.daniil.smirnov.myjunit.exceptions;

/**
 * Thrown if trying to invoke method which is for some reason not accessible
 */
public class InvalidMethodException extends MyJUnitException {
    /**
     * Constructs the exception with default message
     */
    public InvalidMethodException() {
        super("Tried to invoke method which is for some reason not accessible");
    }

    /**
     * Constructs the exception with the given reason
     */
    public InvalidMethodException(String reason) {
        super("Tried to invoke method which is not accessible because of: " + reason);
    }


}
