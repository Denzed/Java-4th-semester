package ru.spbau.daniil.smirnov.myjunit.exceptions;

/**
 * Thrown if the given class does not have a constructor with no arguments
 */
public class CannotConstructClassException extends MyJUnitException {
    public CannotConstructClassException() {
        super("The given class does not exist or not accessible or the same applies to its nullary constructor");
    }
}
