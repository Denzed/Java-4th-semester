package ru.spbau.daniil.smirnov.myjunit.exceptions;

import java.lang.reflect.Method;

/**
 * Thrown if trying to invoke method which is for some reason not accessible
 */
public class InvalidMethodException extends MyJUnitException {
    /**
     * Constructs the exception with the default reason
     */
    public InvalidMethodException(Method method) {
        super(String.format("Tried to invoke method %s which is not accessible for some reason", method));
    }

    /**
     * Constructs the exception with the given reason
     */
    public InvalidMethodException(Method method, String reason) {
        super(String.format("Tried to invoke method %s which is not accessible because it %s", method, reason));
    }


}
