package ru.spbau.daniil.smirnov.myftp.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception which occurs when operation fails on the server side
 */
public class MyFTPUnsuccessfulOperationException extends MyFTPException {
    public MyFTPUnsuccessfulOperationException(@NotNull String message) {
        super(message);
    }
}
