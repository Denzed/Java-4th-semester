package ru.spbau.daniil.smirnov.myftp.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Occurs when the given argument is invalid
 */
public class MyFTPIllegalArgumentException extends MyFTPException {
    public MyFTPIllegalArgumentException(@NotNull String message) {
        super(message);
    }
}
