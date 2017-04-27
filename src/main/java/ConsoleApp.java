import hashers.ForkJoinMD5Hasher;
import hashers.SingleThreadedMD5Hasher;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

/**
 * Provides console application
 */
public class ConsoleApp {
    /**
     * Entrance point to the console application
     *
     * @param args command line arguments
     */
    public static void main(@NotNull String[] args) {
        if (args.length == 0) {
            throw new InvalidParameterException("Please provide a path to hash");
        }
        Path path = Paths.get(args[0]);
        try {
            System.out.print("Single threaded hash: ");
            long startTime = System.currentTimeMillis();
            System.out.write(SingleThreadedMD5Hasher.getHashFromPath(path));
            long endTime = System.currentTimeMillis();
            System.out.println();
            System.out.println("Computed in " + (endTime - startTime) + " milliseconds");
            System.out.print("Multi threaded hash: ");
            startTime = System.currentTimeMillis();
            System.out.write(ForkJoinMD5Hasher.getHashFromPath(path));
            endTime = System.currentTimeMillis();
            System.out.println();
            System.out.println("Computed in " + (endTime - startTime) + " milliseconds");
        } catch (Exception e) {
            System.out.println("Unsuccessful operation: " + e.getMessage());
        }
    }
}