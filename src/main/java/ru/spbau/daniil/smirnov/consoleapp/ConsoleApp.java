package ru.spbau.daniil.smirnov.consoleapp;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

/**
 * Provides console access to MyGit
 */
public class ConsoleApp {
    /**
     * Entrance point to the console application
     * @param args command line arguments
     */
    public static void main(@NotNull String[] args) {
        final ArgsParser parser = new ArgsParser(System.out, Paths.get(System.getProperty("user.dir")));

        try {
            parser.parse(args);
        } catch (InvalidCommandException e) {
            System.out.println("Entered command is not supported:\n" + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unsuccessful operation: " + e.getMessage());
        }
    }

    private ConsoleApp() {}
}
