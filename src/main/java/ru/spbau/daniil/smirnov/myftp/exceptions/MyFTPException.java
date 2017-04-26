package ru.spbau.daniil.smirnov.myftp.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Base class for all exceptions related to MyFTP library
 */
public class MyFTPException extends Exception {
    public MyFTPException(@NotNull String message) {
        super(message);
    }
}
