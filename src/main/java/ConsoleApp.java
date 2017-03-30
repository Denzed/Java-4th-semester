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
            long singleTime = System.currentTimeMillis();
            System.out.println();
            System.out.println("Computed in " + (singleTime - startTime) + " milliseconds");
        } catch (Exception e) {
            System.out.println("Unsuccessful operation: " + e.getMessage());
        }
    }
}