package ru.spbau.daniil.smirnov.consoleapp;

import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.mygit.MyGitActionHandler;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitException;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitIllegalStateException;
import ru.spbau.daniil.smirnov.mygit.objects.Branch;
import ru.spbau.daniil.smirnov.mygit.objects.CommitInfo;
import ru.spbau.daniil.smirnov.mygit.objects.FileStatus;
import ru.spbau.daniil.smirnov.mygit.objects.HeadStatus;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Parses command line arguments and invokes corresponding methods
 */
class ArgsParser {
    private static final String ADD = "add";
    private static final String BRANCH = "branch";
    private static final String CHECKOUT = "checkout";
    private static final String CLEAN = "clean";
    private static final String COMMIT = "commit";
    private static final String HELP = "help";
    private static final String INIT = "init";
    private static final String LOG = "log";
    private static final String MERGE = "merge";
    private static final String RESET = "reset";
    private static final String RM = "rm";
    private static final String STATUS = "status";

    @NotNull
    private final PrintStream printStream;

    @NotNull
    private final Path currentDirectory;

    /**
     * Constructs ArgsParser which writes its output to the given {@code printStream} with assumption that it currently
     * is in directory {@code currentDirectory}
     * @param printStream stream to write output into
     * @param currentDirectory directory which is assumed to be the root directory for the parser
     */
    ArgsParser(@NotNull PrintStream printStream,
               @NotNull Path currentDirectory) {
        this.printStream = printStream;
        this.currentDirectory = currentDirectory;
    }

    /**
     * Parses command line arguments and executes them if parsed successfully.
     * @param args command line arguments
     * @throws InvalidCommandException if the unsupported command is entered or wrong arguments are supplied
     * @throws MyGitException if an internal error occurs
     * @throws IOException if filesystem I/O error occurs
     */
    void parse(@NotNull String[] args)
            throws InvalidCommandException, MyGitException, IOException {
        if (args.length == 0 || args[0].equals(HELP)) {
            showHelp();
            return;
        }
        if (args[0].equals(INIT)) {
            MyGitActionHandler.init(currentDirectory);
            printStream.println("Successfully initialised MyGit repository at " + currentDirectory.toString());
            return;
        }

        final MyGitActionHandler actionHandler = new MyGitActionHandler(currentDirectory);

        switch (args[0]) {
            case ADD:
                handleAddCommand(actionHandler, argsWithoutCommand(args));
                break;
            case RM:
                handleRmCommand(actionHandler, argsWithoutCommand(args));
                break;
            case RESET:
                handleResetCommand(actionHandler, argsWithoutCommand(args));
                break;
            case CLEAN:
                actionHandler.clean();
                break;
            case LOG:
                handleLogCommand(actionHandler);
                break;
            case STATUS:
                handleStatusCommand(actionHandler);
                break;
            case BRANCH:
                handleBranchCommand(actionHandler, argsWithoutCommand(args));
                break;
            case COMMIT:
                handleCommitCommand(actionHandler, argsWithoutCommand(args));
                break;
            case CHECKOUT:
                handleCheckoutCommand(actionHandler, argsWithoutCommand(args));
                break;
            case MERGE:
                handleMergeCommand(actionHandler, argsWithoutCommand(args));
                break;
        }
    }

    private void handleStatusCommand(@NotNull MyGitActionHandler actionHandler)
            throws MyGitIllegalStateException, IOException {
        final HeadStatus headStatus = actionHandler.getHeadStatus();
        if (headStatus.getType().equals(Branch.TYPE)) {
            printStream.println("On branch " + headStatus.getName());
        } else {
            printStream.println("On commit " + headStatus.getName());
        }

        final Map<Path, FileStatus> status = actionHandler.status();
        for (Path path : status.keySet()) {
            printStream.println(
                    status.get(path).getState() +
                    ": " +
                    path.toString());
        }
    }

    private void handleResetCommand(@NotNull MyGitActionHandler actionHandler,
                                    @NotNull String[] args)
            throws MyGitException, IOException, InvalidCommandException {
        if (args.length > 0) {
            actionHandler.reset(args);
        } else {
            throw new InvalidCommandException(RESET + ": at least one file to reset must be specified");
        }
    }

    private void handleAddCommand(@NotNull MyGitActionHandler actionHandler,
                                  @NotNull String[] args)
            throws InvalidCommandException, MyGitException, IOException {
        if (args.length > 0) {
            actionHandler.add(args);
        } else {
            throw new InvalidCommandException(ADD + ": at least one file to add must be specified");
        }
    }

    private void handleRmCommand(@NotNull MyGitActionHandler actionHandler,
                                 @NotNull String[] args)
            throws InvalidCommandException, MyGitException, IOException {
        if (args.length > 0) {
            actionHandler.rm(Arrays
                    .stream(args)
                    .map(Paths::get)
                    .collect(Collectors.toList()));
        } else {
            throw new InvalidCommandException(ADD + ": at least one file to remove must be specified");
        }
    }

    private void handleMergeCommand(@NotNull MyGitActionHandler actionHandler,
                                    @NotNull String[] args)
            throws InvalidCommandException, MyGitException, IOException {
        if (args.length > 0) {
            actionHandler.merge(args[0]);
        } else {
            throw new InvalidCommandException(MERGE + ": branch name to merge with must be specified");
        }
    }

    private void handleCheckoutCommand(@NotNull MyGitActionHandler actionHandler,
                                       @NotNull String[] args)
            throws InvalidCommandException, MyGitException, IOException {
        if (args.length > 0) {
            actionHandler.checkout(args[0]);
        } else {
            throw new InvalidCommandException(CHECKOUT + ": branch name must be specified");
        }
    }

    private void handleCommitCommand(@NotNull MyGitActionHandler actionHandler,
                                     @NotNull String[] args)
            throws MyGitException, IOException, InvalidCommandException {
        if (args.length > 0) {
            actionHandler.commit(args[0]);
        } else {
            throw new InvalidCommandException(COMMIT + ": revision name must be specified");
        }
    }

    private void handleBranchCommand(@NotNull MyGitActionHandler actionHandler,
                                     @NotNull String[] args)
            throws IOException, MyGitException, InvalidCommandException {
        if (args.length == 0) {
            final HeadStatus headStatus = actionHandler.getHeadStatus();

            final String currentBranchName;
            if (headStatus.getType().equals(Branch.TYPE)) {
                currentBranchName = headStatus.getName();
            } else {
                currentBranchName = null;
                printStream.println("Detached HEAD at " + headStatus.getName());
            }

            for (Branch branch : actionHandler.listBranches()) {
                printStream.println((branch.getName().equals(currentBranchName) ? "* " : "  ")
                        + branch.getName());
            }
        } else if (args.length == 1) {
            actionHandler.createBranch(args[0]);
        } else if (args.length == 2 && args[0].equals("-d")) {
            actionHandler.deleteBranch(args[1]);
        } else if (args.length == 2 && args[1].equals("-d")) {
            actionHandler.deleteBranch(args[0]);
        } else {
            throw new InvalidCommandException(BRANCH + ": too many arguments");
        }
    }

    @NotNull
    private static String[] argsWithoutCommand(@NotNull String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private void handleLogCommand(@NotNull MyGitActionHandler actionHandler)
            throws MyGitException, IOException {
        for (CommitInfo commitInfo : actionHandler.log()) {
            printStream.println("Author: " + commitInfo.getAuthor() + "\n" +
                                "Date:   " + commitInfo.getCreationDate() + "\n" +
                                "\n" +
                                "\t" + commitInfo.getMessage() + "\n" +
                                "\n" +
                                "commit " + commitInfo.getRevisionHash());
        }
    }

    private void showHelp() {
        printStream.println(
            "usage: mygit <command> [<args>]\n" +
            "\n" +
            "initialise an empty repository:\n" +
            "  " + INIT + "\n" +
            "\n" +
            "work with current branch:\n" +
            "  " + ADD + " [<files>]\n" +
            "  " + RESET + " [<files>]\n" +
            "  " + RM + "\n" +
            "\n" +
            "examine the commit history:\n" +
            "  " + LOG + "\n" +
            "\n" +
            "grow and tweak your common history:\n" +
            "  " + BRANCH + " [<name> | -d <name> | <name> -d ]\n" +
            "  " + CHECKOUT + " <branch> | <revision>\n" +
            "  " + COMMIT + " <message>\n" +
            "  " + MERGE + " <branch>\n" +
            "\n" +
            "show the working tree status:\n" +
            "  " + STATUS + "\n" +
            "\n" +
            "remove unstaged files:\n" +
            "  " + CLEAN + "\n" +
            "\n" +
            "'mygit help' list all available commands.");
    }
}
