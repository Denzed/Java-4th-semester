package ru.spbau.daniil.smirnov.server.actions;

import ru.spbau.daniil.smirnov.exceptions.MyFTPIllegalArgumentException;

import java.io.IOException;

/**
 * Base class for all actions library can perform
 *
 * @param <T> return type
 */
public abstract class Action<T> {
    /**
     * Perform the action and return the result
     *
     * @return result of the performed action
     */
    public abstract T perform() throws IOException, MyFTPIllegalArgumentException;
}
