package ru.spbau.daniil.smirnov.myjunit.consoleapp;

import org.jetbrains.annotations.NotNull;

/**
 * Command line access to MyJUnit test runner
 */
public class CommandLineApp {
    /**
     * Runs tests found on the given path
     * @param arguments path to tests
     */
    public static void main(@NotNull String[] arguments) {
        new CommandLineArgumentsHandler(System.out).handle(arguments);
    }
}