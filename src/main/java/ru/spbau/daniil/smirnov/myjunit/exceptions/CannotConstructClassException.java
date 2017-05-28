package ru.spbau.daniil.smirnov.myjunit.exceptions;

/**
 * Thrown if the given class does not have a constructor with no arguments
 */
public class CannotConstructClassException extends MyJUnitException {
    public CannotConstructClassException(Class clazz) {
        super(
                String.format(
                        "Class %s does not exist or is not accessible or the same applies to its nullary constructor",
                        clazz));
    }
}
