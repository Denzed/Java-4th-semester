package ru.spbau.daniil.smirnov.mygit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitDoubleInitializationException;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitIllegalArgumentException;
import ru.spbau.daniil.smirnov.mygit.exceptions.MyGitIllegalStateException;
import ru.spbau.daniil.smirnov.mygit.logger.Log4j2ContextBuilder;
import ru.spbau.daniil.smirnov.mygit.objects.*;
import ru.spbau.daniil.smirnov.mygit.utils.MyGitHasher;
import ru.spbau.daniil.smirnov.mygit.utils.MyGitSHA1Hasher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class which purpose is to maintain internal repository representation in the filesystem
 */
class InternalUpdater {
    @NotNull
    private final Path myGitRootDirectory;

    @NotNull
    private final MyGitHasher hasher;

    @NotNull
    private final Logger logger;

    static InternalUpdater init(@NotNull Path directory)
            throws MyGitDoubleInitializationException, MyGitIllegalStateException, IOException {
        final Path myGitRootPath = Paths.get(directory.toString(), ".mygit");
        if (Files.exists(myGitRootPath)) {
            throw new MyGitDoubleInitializationException();
        }
        Files.createDirectory(myGitRootPath);

        InternalUpdater internalUpdater;
        try {
            internalUpdater = new InternalUpdater(directory, new MyGitSHA1Hasher());
        } catch (MyGitIllegalArgumentException ignored) {
            throw new IllegalStateException();
        }

        Files.createDirectory(Paths.get(myGitRootPath.toString(), "objects"));
        Files.createDirectory(Paths.get(myGitRootPath.toString(), "branches"));
        Files.createFile(Paths.get(myGitRootPath.toString(), "HEAD"));
        Files.createFile(Paths.get(myGitRootPath.toString(), "index"));
        internalUpdater.setHeadStatus(new HeadStatus(Branch.TYPE, "master"));
        final String commitHash = createInitialCommit(internalUpdater);

        internalUpdater.writeBranch("master", commitHash);
        return internalUpdater;
    }

    InternalUpdater(@NotNull Path myGitRootDirectory,
                    @NotNull MyGitHasher hasher)
            throws MyGitIllegalArgumentException {
        if (!myGitRootDirectory.isAbsolute()) {
            throw new MyGitIllegalArgumentException("MyGit root directory path should be absolute");
        }
        this.myGitRootDirectory = myGitRootDirectory;
        this.hasher = hasher;
        final LoggerContext loggerContext =
                Log4j2ContextBuilder.createContext(myGitRootDirectory);
        this.logger = loggerContext.getRootLogger();
        logger.trace("Initialized logger");
    }

    @NotNull
    String writeObjectToFilesystem(@NotNull Object object)
            throws IOException, MyGitIllegalStateException {
        final String hash = getObjectHash(object);

        MyGitHasher.HashParts hashParts;
        try {
            hashParts = hasher.splitHash(hash);
        } catch (MyGitIllegalArgumentException e) {
            throw new MyGitIllegalStateException("Got an illegal hash value: " + hash);
        }

        final Path directoryPath = Paths.get(myGitRootDirectory.toString(), ".mygit", "objects",
                hashParts.getDirectoryHash());
        final Path filePath = Paths.get(directoryPath.toString(), hashParts.getFilenameHash());
        if (!directoryPath.toFile().exists()) {
            Files.createDirectory(directoryPath);
        }
        if (!filePath.toFile().exists()) {
            Files.createFile(filePath);
        }

        try (
                FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(object);
        }
        return hash;
    }

    @NotNull
    Set<Path> readIndexPaths()
            throws IOException, MyGitIllegalStateException {
        final File indexFile = getIndexFile();
        return Files.lines(indexFile.toPath())
                .map(Paths::get)
                .collect(Collectors.toCollection(HashSet::new));
    }

    void writeIndexPaths(@NotNull Set<Path> indexPaths)
            throws MyGitIllegalStateException, IOException {
        final File indexFile = getIndexFile();
        try (
                FileWriter fileWriter = new FileWriter(indexFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            for (Path indexPath: indexPaths) {
                bufferedWriter.write(indexPath + "\n");
            }
        }
    }

    void writeBranch(@NotNull String branchName,
                     @NotNull String commitHash)
            throws IOException {
        final Path branchPath = getBranchPath(branchName);
        if (!branchPath.toFile().exists()) {
            Files.createFile(branchPath);
        }
        try (FileWriter writer = new FileWriter(branchPath.toFile())) {
            writer.write(commitHash);
        }
    }

    void deleteBranch(@NotNull String branchName)
            throws IOException {
        final Path branchPath = getBranchPath(branchName);
        Files.delete(branchPath);
    }

    void setHeadStatus(@NotNull HeadStatus headStatus)
            throws IOException, MyGitIllegalStateException {
        final File headFile = getHeadFile();
        try (FileWriter fileWriter = new FileWriter(headFile)) {
            fileWriter.write(headStatus.getType() + "\n");
            fileWriter.write(headStatus.getName());
        }
    }

    void moveHeadToCommitHash(@NotNull String commitHash)
            throws IOException, MyGitIllegalStateException {
        final HeadStatus headStatus = getHeadStatus();
        HeadStatus currentHeadStatus;
        if (headStatus.getType().equals(Branch.TYPE)) {
            final String branchName = headStatus.getName();
            writeBranch(branchName, commitHash);
            currentHeadStatus = new HeadStatus(Branch.TYPE, branchName);
        } else {
            currentHeadStatus = new HeadStatus(Commit.TYPE, commitHash);
        }
        setHeadStatus(currentHeadStatus);
    }

    @NotNull
    HeadStatus getHeadStatus()
            throws IOException, MyGitIllegalStateException {
        final File headFile = getHeadFile();
        final List<String> headLines = Files.lines(headFile.toPath()).collect(Collectors.toList());

        if (headLines.size() != 2) {
            throw new MyGitIllegalStateException("Corrupted HEAD file --- wrong number of lines");
        }

        final String headType = headLines.get(0);
        final String headPath = headLines.get(1);

        if (!(headType.equals(Branch.TYPE) || headType.equals(Commit.TYPE))) {
            throw new MyGitIllegalStateException("Corrupted HEAD file --- unknown type");
        }

        return new HeadStatus(headType, headPath);
    }

    @NotNull
    String getHeadCommitHash()
            throws MyGitIllegalStateException, IOException {
        final HeadStatus headStatus = getHeadStatus();
        return (headStatus.getType().equals(Branch.TYPE)
                ? getBranchCommitHash(headStatus.getName())
                : headStatus.getName());
    }

    @NotNull
    Commit getHeadCommit()
            throws MyGitIllegalStateException, IOException {
        return readCommit(getHeadCommitHash());
    }

    @NotNull
    Tree getHeadTree()
            throws MyGitIllegalStateException, IOException {
        return readTree(getHeadCommit().getRootTreeHash());
    }

    @NotNull
    Tree getBranchTree(@NotNull String branchName)
            throws MyGitIllegalStateException, IOException {
        final String branchCommitHash = getBranchCommitHash(branchName);
        return readTree(readCommit(branchCommitHash).getRootTreeHash());
    }

    @NotNull
    String getBranchCommitHash(@NotNull String branchName)
            throws MyGitIllegalStateException, IOException {
        final File branchFile = getBranchPath(branchName).toFile();
        if (!branchFile.exists()) {
            throw new MyGitIllegalStateException("Branch '" + branchName + "' does not exist");
        }

        final List<String> branchLines =
                Files.lines(branchFile.toPath()).collect(Collectors.toCollection(ArrayList::new));
        if (branchLines.size() != 1) {
            throw new MyGitIllegalStateException("Corrupted branch file for branch '" + branchName
                    + "' --- wrong number of lines");
        }
        return branchLines.get(0);
    }

    @NotNull
    Commit readCommit(@NotNull String commitHash)
            throws MyGitIllegalStateException, IOException {
        return readObjectAndCast(commitHash, Commit.class);
    }

    @NotNull
    Tree readTree(@NotNull String treeHash)
            throws MyGitIllegalStateException, IOException {
        return readObjectAndCast(treeHash, Tree.class);
    }

    @NotNull
    Blob readBlob(@NotNull String blobHash)
            throws MyGitIllegalStateException, IOException {
        return readObjectAndCast(blobHash, Blob.class);
    }

    @NotNull
    String computeFileHashFromPath(@NotNull Path path)
            throws IOException, MyGitIllegalStateException {
        if (!path.toFile().isFile()) {
            throw new MyGitIllegalStateException("Given path is not a file");
        }
        final byte[] data = Files.readAllBytes(path);
        final Blob blob = new Blob(data);
        return getObjectHash(blob);
    }

    @NotNull
    String writeBlobFromPath(@NotNull Path path)
            throws MyGitIllegalStateException, IOException {
        final byte[] data = Files.readAllBytes(path);
        final Blob blob = new Blob(data);
        return writeObjectToFilesystem(blob);
    }

    @NotNull
    private Object readObject(@NotNull String objectHash)
            throws MyGitIllegalStateException, IOException {
        MyGitHasher.HashParts hashParts;
        try {
            hashParts = hasher.splitHash(objectHash);
        } catch (MyGitIllegalArgumentException ignored) {
            throw new MyGitIllegalStateException("Got an illegal hash value: " + objectHash);
        }

        final File objectFile = Paths.get(myGitRootDirectory.toString(), ".mygit", "objects",
                hashParts.getDirectoryHash(), hashParts.getFilenameHash()).toFile();
        if (!objectFile.exists()) {
            throw new MyGitIllegalStateException("Object at '" + objectFile.getAbsolutePath() + "' does not exist");
        }

        try (
                FileInputStream fileInputStream = new FileInputStream(objectFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new MyGitIllegalStateException("Could not read object's file: + " + e.getMessage());
        }
    }

    void moveFromCommitToCommit(@NotNull Commit fromCommit,
                                @NotNull Commit toCommit)
            throws MyGitIllegalStateException, IOException {
        final Tree fromTree = readTree(fromCommit.getRootTreeHash());
        final Tree toTree = readTree(toCommit.getRootTreeHash());
        deleteFilesFromTree(fromTree, myGitRootDirectory);
        loadFilesFromTree(toTree, myGitRootDirectory);
    }

    /**
     * Computes the object's hash using current hasher.
     * @param object an object to hash
     * @return hash of the object
     * @throws IOException if an exception occurs in a hasher
     */
    String getObjectHash(@NotNull Object object)
            throws IOException {
        return hasher.getHashFromObject(object);
    }

    @NotNull
    private <T> T readObjectAndCast(@NotNull String objectHash,
                                    @NotNull Class<T> objectClass)
            throws MyGitIllegalStateException, IOException {
        return objectClass.cast(readObject(objectHash));
    }

    void loadFilesFromTree(@NotNull Tree tree,
                           @NotNull Path path)
            throws MyGitIllegalStateException, IOException {
        logger.trace("loading files from tree to path=" + path.toString() + "-- started");
        for (Tree.TreeEdge childEdge : tree.getEdgesToChildren()) {
            final Path childPath = Paths.get(path.toString(), childEdge.getName());
            if (childEdge.getType().equals(Blob.TYPE)) {
                if (Files.exists(childPath) && !getTypeForPath(childPath).equals(Blob.TYPE)) {
                    deleteAtPath(childPath);
                }
                if (!Files.exists(childPath)) {
                    Files.createFile(childPath);
                }
                final Blob childBlob = readBlob(childEdge.getHash());
                Files.write(childPath, childBlob.getContents());
            } else {
                if (Files.exists(childPath) && !getTypeForPath(childPath).equals(Tree.TYPE)) {
                    Files.delete(childPath);
                }
                if (!Files.exists(childPath)) {
                    Files.createDirectory(childPath);
                }
                final Tree childTree = readTree(childEdge.getHash());
                loadFilesFromTree(childTree, childPath);
            }
        }
        logger.trace("loading files from tree -- started");
    }

    private void deleteFilesFromTree(@NotNull Tree tree,
                                     @NotNull Path path)
            throws MyGitIllegalStateException, IOException {
        logger.trace("deleting files from tree at path=" + path.toString() + " -- started");
        for (Tree.TreeEdge childEdge : tree.getEdgesToChildren()) {
            final Path childPath = Paths.get(path.toString(), childEdge.getName());
            final File childFile = childPath.toFile();
            if (!childFile.exists()) {
                continue;
            }
            if (childFile.isDirectory() && childEdge.isDirectory()) {
                deleteFilesFromTree(readTree(childEdge.getHash()), childPath);
            }
            deleteAtPath(childPath);
        }
        logger.trace("deleting files from tree -- started");
    }

    private Path getBranchPath(@NotNull String branchName) {
        return Paths.get(myGitRootDirectory.toString(), ".mygit", "branches", branchName);
    }

    @NotNull
    private String getTypeForPath(@NotNull Path path) {
        return path.toFile().isDirectory() ? Tree.TYPE : Blob.TYPE;
    }

    @NotNull
    private File getIndexFile()
            throws MyGitIllegalStateException {
        final File indexFile = Paths.get(myGitRootDirectory.toString(), ".mygit", "index").toFile();
        if (!indexFile.exists()) {
            throw new MyGitIllegalStateException("Index file at '" + indexFile.getAbsolutePath() + "' does not exist");
        }
        return indexFile;
    }

    @NotNull
    private File getHeadFile()
            throws MyGitIllegalStateException {
        final File headFile = Paths.get(myGitRootDirectory.toString(), ".mygit", "HEAD").toFile();
        if (!headFile.exists()) {
            throw new MyGitIllegalStateException("HEAD file at '" + headFile.getAbsolutePath() + "' does not exist");
        }
        return headFile;
    }

    static void deleteAtPath(@NotNull Path pathToDelete) throws IOException {
        //noinspection ResultOfMethodCallIgnored
        if (Files.isDirectory(pathToDelete)) {
            Files.walk(pathToDelete)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } else {
            Files.delete(pathToDelete);
        }
    }

    @NotNull
    List<Branch> listBranches()
            throws MyGitIllegalStateException, IOException {
        final File branchesDirectory = Paths.get(myGitRootDirectory.toString(), ".mygit", "branches").toFile();
        if (!branchesDirectory.exists()) {
            throw new MyGitIllegalStateException("Branches directory at '" + branchesDirectory + "' does not exist");
        }

        final File[] branches = branchesDirectory.listFiles();
        if (branches == null) {
            throw new IOException("Error occurred while reading the directory: " + branchesDirectory);
        }
        return Arrays.stream(branches)
                .map(file -> new Branch(file.getName()))
                .collect(Collectors.toList());
    }

    @NotNull
    List<String> listCommitHashes()
            throws MyGitIllegalStateException, IOException {
        final Path objectsPath = Paths.get(myGitRootDirectory.toString(), ".mygit", "objects");
        final List<String> objectHashes = Files.walk(objectsPath)
                .filter(path -> !path.toFile().isDirectory())
                .map(path -> path.getParent().getFileName().toString() + path.getFileName().toString())
                .collect(Collectors.toList());

        final List<String> commitHashes = new ArrayList<>();
        for (String objectHash : objectHashes) {
            if (readObject(objectHash) instanceof Commit) {
                commitHashes.add(objectHash);
            }
        }
        return commitHashes;
    }

    @NotNull
    private static String createInitialCommit(@NotNull InternalUpdater internalUpdater)
            throws MyGitIllegalStateException, IOException {
        final String treeHash = internalUpdater.writeObjectToFilesystem(new Tree());
        final Commit primaryCommit = new Commit(treeHash);
        return internalUpdater.writeObjectToFilesystem(primaryCommit);
    }

    @NotNull
    Logger getLogger() {
        return logger;
    }
}
