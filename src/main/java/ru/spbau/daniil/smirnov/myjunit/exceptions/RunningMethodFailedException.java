package ru.spbau.daniil.smirnov.myjunit.exceptions;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Thrown if an exception was thrown while running method. Underlying exception's message will be shown
 */
public class RunningMethodFailedException extends MyJUnitException {
    private Throwable underlying;

    public RunningMethodFailedException(@NotNull Method method, @NotNull Throwable e) {
        super(String.format("Running method %s failed with message %s", method, e.getMessage()));
        underlying = e;
    }

    @NotNull
    public Throwable getUnderlying() {
        return underlying;
    }
}
