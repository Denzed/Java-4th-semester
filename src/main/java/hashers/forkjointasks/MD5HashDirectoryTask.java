package hashers.forkjointasks;

import hashers.MD5HashTask;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.*;

/**
 * Class for a directory hashing task that runs within a ForkJoinPool
 */
public class MD5HashDirectoryTask extends MD5HashTask {
    private final File directory;

    /**
     * Constructs a task for hashing a directory
     *
     * @param directory directory to hash
     */
    public MD5HashDirectoryTask(@NotNull File directory) {
        this.directory = directory;
    }

    /**
     * Method which invokes the computation of hash of a directory
     *
     * @return computed hash
     */
    @NotNull
    public byte[] compute() {
        List<MD5HashTask> tasks = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));
            for (File file : files) {
                tasks.add(MD5HashTask.makeTask(file));
                tasks.get(tasks.size() - 1).fork();
            }
        }
        Vector<InputStream> inputStreams = new Vector<>();
        inputStreams.add(new ByteArrayInputStream(directory.getName().getBytes()));
        for (MD5HashTask task : tasks) {
            inputStreams.add(new ByteArrayInputStream(task.join()));
        }
        MessageDigest messageDigest = getMessageDigest();
        try {
            try (DigestInputStream digestInputStream = new DigestInputStream(
                    new SequenceInputStream(inputStreams.elements()),
                    messageDigest)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (digestInputStream.read(buffer) != -1) ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageDigest.digest();
    }
}
