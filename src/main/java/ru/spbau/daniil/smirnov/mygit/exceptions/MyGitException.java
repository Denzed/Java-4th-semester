package ru.spbau.daniil.smirnov.mygit.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Base class for MyGit exceptions
 */
public class MyGitException extends Exception {
    public MyGitException(@NotNull String message) {
        super(message);
    }
}
