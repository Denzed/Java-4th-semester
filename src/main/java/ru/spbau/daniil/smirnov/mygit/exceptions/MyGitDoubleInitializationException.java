package ru.spbau.daniil.smirnov.mygit.exceptions;

/**
 * Exception which occurs if one tries to initialise a MyGit repository twice
 */
public class MyGitDoubleInitializationException extends MyGitException {
    public MyGitDoubleInitializationException() {
        super("MyGit repository already exists");
    }
}
