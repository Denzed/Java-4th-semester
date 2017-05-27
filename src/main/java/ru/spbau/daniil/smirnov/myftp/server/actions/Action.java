package ru.spbau.daniil.smirnov.myftp.server.actions;

import ru.spbau.daniil.smirnov.myftp.exceptions.MyFTPIllegalArgumentException;

import java.io.IOException;

/**
 * Base class for all actions library can perform
 */
public abstract class Action {
    /**
     * Perform the action and return the result
     *
     * @return result of the performed action
     */
    public abstract byte[] perform() throws IOException, MyFTPIllegalArgumentException;
}
