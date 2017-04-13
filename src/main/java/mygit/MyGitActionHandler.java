package mygit;

import mygit.exceptions.*;
import mygit.objects.*;
import mygit.utils.MyGitSHA1Hasher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class used as a gateway between application and MyGit repository. Handles commands and delegates them to the
 * {@link InternalUpdater} which performs them on the internal representation
 */
public class MyGitActionHandler {
    @NotNull
    private final Path myGitRepositoryRootDirectory;
    @NotNull
    private final InternalUpdater internalUpdater;

    /**
     * Initializes a MyGit repository in the given directory
     * @param directory directory to initialise a repository in
     * @throws MyGitIllegalArgumentException if the given path is not absolute
     * @throws MyGitIllegalStateException if the initialisation fails due to an internal error
     * @throws MyGitDoubleInitializationException if the repository is already initialised in the given directory
     * @throws IOException if filesystem I/O error occurs during the initialisation
     */
    static public void init(@NotNull Path directory)
            throws MyGitIllegalArgumentException, MyGitIllegalStateException,
            MyGitDoubleInitializationException, IOException {
        if (!directory.isAbsolute()) {
            throw new MyGitIllegalArgumentException("Path given as a parameter should be absolute");
        }
        InternalUpdater internalUpdater = InternalUpdater.init(directory);
        internalUpdater.getLogger().trace("init -- done in " + directory);
    }

    /**
     * Constructs a handler in the given directory
     * @param currentDirectory directory to construct handler in
     * @throws MyGitIllegalArgumentException if given path is not absolute
     * @throws MyGitIllegalStateException in case that given path does not correspond to any MyGit repository
     */
    public MyGitActionHandler(@NotNull Path currentDirectory)
            throws MyGitIllegalArgumentException, MyGitIllegalStateException {
        if (!currentDirectory.isAbsolute()) {
            throw new MyGitIllegalArgumentException("Path given as a parameter should be absolute");
        }
        Path tmpPath = currentDirectory;
        while (tmpPath != null && !Files.exists(Paths.get(tmpPath.toString(), ".mygit"))) {
            tmpPath = tmpPath.getParent();
        }
        if (tmpPath == null) {
            throw new MyGitIllegalStateException("Given path is neither a MyGit repository and nor belongs to it");
        }
        myGitRepositoryRootDirectory = tmpPath;
        internalUpdater = new InternalUpdater(myGitRepositoryRootDirectory, new MyGitSHA1Hasher());
    }

    /**
     * Adds paths to the current index.
     *
     * @param arguments list of paths to add to the index
     * @throws MyGitIllegalArgumentException if the array contains invalid paths
     * @throws IOException if filesystem I/O error occurs
     * @throws MyGitIllegalStateException if an internal error occurs
     */
    public void add(@NotNull String[] arguments)
            throws MyGitIllegalArgumentException, MyGitIllegalStateException, IOException {
        internalUpdater.getLogger().trace("add -- started with paths:");
        for (String argument : arguments) {
            internalUpdater.getLogger().trace(argument);
        }
        performUpdateToIndex(arguments, paths -> paths::add);
        internalUpdater.getLogger().trace("add -- done");
    }

    /**
     * Resets files to their last checked out state
     *
     * @param arguments list of paths to remove from the index
     * @throws MyGitIllegalArgumentException if the array contains invalid paths
     * @throws IOException if filesystem I/O error occurs
     * @throws MyGitIllegalStateException if an internal error occurs
     */
    public void reset(@NotNull String[] arguments)
            throws MyGitIllegalArgumentException, IOException, MyGitIllegalStateException {
        internalUpdater.getLogger().trace("reset -- started with paths:");
        for (String argument : arguments) {
            internalUpdater.getLogger().trace(argument);
        }
        performUpdateToIndex(arguments, paths -> paths::remove);
        Set<Path> args = Arrays.stream(arguments)
                .map(path -> Paths.get(path))
                .collect(Collectors.toSet());
        resetStates(internalUpdater.getHeadTree(), myGitRepositoryRootDirectory, args);
        internalUpdater.getLogger().trace("reset -- done");
    }

    /**
     * Checks out a branch given by its name or a commit given by its hash and sets it a new HEAD
     * <p>
     * As it replaces any differing files the index must be empty at the moment of the checkout
     * @param revision revision to checkout onto
     * @throws MyGitMissingPrerequisitesException in case the index is not empty
     * @throws IOException if filesystem I/O error occurs
     * @throws MyGitIllegalStateException if an internal error occurs
     * @throws MyGitIllegalArgumentException if such revision does not exist
     */
    public void checkout(@NotNull String revision)
            throws MyGitMissingPrerequisitesException, IOException,
                MyGitIllegalStateException, MyGitIllegalArgumentException {
        internalUpdater.getLogger().trace("checkout -- started with revision=" + revision);
        if (foundUncommittedChanges()) {
            throw new MyGitMissingPrerequisitesException("Unstaged changes detected. Checkout cancelled");
        }
        final HeadStatus headStatus = getHeadStatus();
        final String headCommitHash = headStatus.getType().equals(Branch.TYPE)
                ? internalUpdater.getBranchCommitHash(headStatus.getName())
                : headStatus.getName();
        String newCommitHash, newHeadType;
        if (listBranches().contains(new Branch(revision))) {
            newCommitHash = internalUpdater.getBranchCommitHash(revision);
            newHeadType = Branch.TYPE;
        } else {
            if (!listCommitHashes().contains(revision)) {
                throw new MyGitIllegalArgumentException("No such revision: " + revision);
            }
            newCommitHash = revision;
            newHeadType = Commit.TYPE;
        }
        internalUpdater.moveFromCommitToCommit(
                internalUpdater.readCommit(headCommitHash),
                internalUpdater.readCommit(newCommitHash));
        internalUpdater.setHeadStatus(new HeadStatus(newHeadType, revision));
        internalUpdater.getLogger().trace("checkout -- done");
    }

    /**
     * Commit all indexed changes and move HEAD to the generated Commit
     * @param message commit message
     * @throws MyGitEmptyCommitException if there are no changes to commit
     * @throws MyGitIllegalStateException if an internal error occurs
     * @throws IOException if a filesystem I/O error occurs
     * @throws MyGitIllegalArgumentException if the commit message is empty
     */
    public void commit(@NotNull String message)
            throws MyGitIllegalStateException, IOException, MyGitIllegalArgumentException, MyGitEmptyCommitException {
        internalUpdater.getLogger().trace("commit -- started with message=" + message);
        if (message.isEmpty()) {
            throw new MyGitIllegalArgumentException("Commit message should not be empty");
        }
        if (!foundUncommittedChanges()) {
            throw new MyGitEmptyCommitException();
        }
        final Tree headTree = internalUpdater.getHeadTree();
        final Set<Path> indexedPaths = internalUpdater.readIndexPaths();
        final String newTreeHash = rebuildTree(headTree, myGitRepositoryRootDirectory, indexedPaths);
        final Commit commit = new Commit(newTreeHash,
                                         message,
                                         Collections.singletonList(internalUpdater.getHeadCommitHash()));
        final String commitHash = internalUpdater.writeObjectToFilesystem(commit);
        internalUpdater.moveHeadToCommitHash(commitHash);
        internalUpdater.writeIndexPaths(new HashSet<>());
        internalUpdater.getLogger().trace("commit -- done");
    }

    /**
     * Creates a new branch with the given name
     * @param branchName name of the branch to create
     * @throws MyGitIllegalArgumentException if such branch already exists
     * @throws MyGitIllegalStateException if an internal error occurs
     * @throws IOException if filesystem I/O error occurs
     */
    public void createBranch(@NotNull String branchName)
            throws MyGitIllegalArgumentException, MyGitIllegalStateException, IOException {
        internalUpdater.getLogger().trace("branch create -- started with name=" + branchName);
        if (listBranches().contains(new Branch(branchName))) {
            throw new MyGitIllegalArgumentException("Branch '" + branchName + "' already exists");
        }
        internalUpdater.writeBranch(branchName, internalUpdater.getHeadCommitHash());
        internalUpdater.getLogger().trace("branch create -- done");
    }

    /**
     * Delete a branch with the given name
     * @param branchName name of the branch to delete
     * @throws MyGitIllegalArgumentException if such branch does not exist or is currently checked-out
     * @throws MyGitIllegalStateException if an internal error occurs
     * @throws IOException if filesystem I/O error occurs
     */
    public void deleteBranch(@NotNull String branchName)
            throws MyGitIllegalStateException, IOException, MyGitIllegalArgumentException {
        internalUpdater.getLogger().trace("branch delete -- started with name=" + branchName);
        HeadStatus headStatus = getHeadStatus();
        if (headStatus.getType().equals(Branch.TYPE) && headStatus.getName().equals(branchName)) {
            throw new MyGitIllegalArgumentException("Cannot delete currently checked-out branch '" + branchName + "'");
        }
        if (!listBranches().contains(new Branch(branchName))) {
            throw new MyGitIllegalArgumentException("Branch '" + branchName + "' does not exist");
        }
        internalUpdater.deleteBranch(branchName);
        internalUpdater.getLogger().trace("branch delete -- done");
    }

    /**
     * Gets commit logs of all HEAD commit's ancestors
     * @return List of {@link CommitInfo} of HEAD commit's ancestors in chronological order
     * @throws MyGitIllegalStateException if an internal error occurs
     * @throws IOException if filesystem I/O error occurs
     */
    @NotNull
    public List<CommitInfo> log() throws MyGitIllegalStateException, IOException {
        internalUpdater.getLogger().trace("log -- started");
        final TreeSet<Commit> commitTree = new TreeSet<>();
        buildCommitTree(internalUpdater.getHeadCommit(), commitTree);
        final List<CommitInfo> output = new LinkedList<>();
        for (Commit commit : commitTree) {
            output.add(new CommitInfo(internalUpdater.getObjectHash(commit),
                    commit.getCommitMessage(),
                    commit.getCommitAuthor(),
                    commit.getCreationDate()));
        }
        Collections.reverse(output);
        internalUpdater.getLogger().trace("log -- done");
        return output;
    }

    /**
     * Merges HEAD with the branch given by its name
     * <p>
     * Conflicts are resolved in such a way that the most recently modified file is chosen.
     * HEAD should not be in a detached state and no staged changes may exist
     * @param branchName branch to be merged into HEAD
     * @throws MyGitMissingPrerequisitesException either if HEAD is detached or there are staged changes
     * @throws MyGitIllegalStateException if an internal error occurs
     * @throws IOException if filesystem I/O error occurs
     * @throws MyGitIllegalArgumentException either if one tries to merge HEAD with itself or the specified branch
     * does not exist
     */
    public void merge(@NotNull String branchName)
            throws MyGitMissingPrerequisitesException, IOException,
                MyGitIllegalStateException, MyGitIllegalArgumentException {
        internalUpdater.getLogger().trace("merge -- started with branch name=" + branchName);
        if (foundUncommittedChanges()) {
            throw new MyGitMissingPrerequisitesException("Unstaged changes detected. Merge cancelled");
        }
        final HeadStatus headStatus = getHeadStatus();
        if (headStatus.getType().equals(Commit.TYPE)) {
            throw new MyGitMissingPrerequisitesException("Cannot merge with detached HEAD");
        }
        if (headStatus.getName().equals(branchName)) {
            throw new MyGitIllegalArgumentException("Cannot merge branch with itself");
        }
        if (!listBranches().contains(new Branch(branchName))) {
            throw new MyGitIllegalArgumentException("No such branch: " + branchName);
        }
        final Tree headTree = internalUpdater.getHeadTree();
        final Tree otherTree = internalUpdater.getBranchTree(branchName);
        final String mergedTreeHash = mergeTrees(headTree, otherTree);
        final List<String> parentHashes = Arrays.asList(internalUpdater.getBranchCommitHash(headStatus.getName()),
                                                        internalUpdater.getBranchCommitHash(branchName));
        final Commit mergeCommit = new Commit(mergedTreeHash, "Merge commit", parentHashes);
        final String mergeCommitHash = internalUpdater.writeObjectToFilesystem(mergeCommit);
        internalUpdater.writeBranch(headStatus.getName(), mergeCommitHash);
        internalUpdater.moveFromCommitToCommit(internalUpdater.getHeadCommit(), mergeCommit);
        internalUpdater.getLogger().trace("merge -- done");
    }

    /**
     * Show the working tree status
     *
     * @return Map of file path to file change statuses
     * @throws MyGitIllegalStateException if an internal error occurs
     * @throws IOException                if filesystem I/O error occurs
     */
    @NotNull
    public Map<Path, FileStatus> status()
            throws MyGitIllegalStateException, IOException {
        internalUpdater.getLogger().trace("status -- started");
        Map<Path, FileStatus> result = new HashMap<>();
        Set<Path> indexPaths = internalUpdater.readIndexPaths();
        indexPaths.forEach(path -> result.put(path, FileStatus.STAGED));
        buildStagingStatus(internalUpdater.getHeadTree(),
                myGitRepositoryRootDirectory,
                indexPaths,
                result);
        internalUpdater.getLogger().trace("status -- done");
        return result;
    }

    /**
     * Remove files from the working tree and from the index
     *
     * @param paths paths to files to remove
     * @throws IOException                   if filesystem I/O error occurs
     * @throws MyGitIllegalArgumentException if some files are missing
     * @throws MyGitIllegalStateException if an internal error occurs
     */
    public void rm(@NotNull List<Path> paths)
            throws IOException, MyGitIllegalArgumentException, MyGitIllegalStateException {
        internalUpdater.getLogger().trace("rm -- started with paths:");
        for (Path path : paths) {
            internalUpdater.getLogger().trace(path);
        }
        resetIndexPaths(paths
                .stream()
                .map(Path::toString)
                .collect(Collectors.toList()).toArray(new String[]{}));
        for (Path path : paths) {
            if (Files.isRegularFile(path)) {
                Files.delete(path);
            } else {
                InternalUpdater.deleteDirectoryRecursively(path);
            }
        }
    }

    /**
     * Remove unstaged files
     *
     * @throws MyGitIllegalStateException if an internal error occurs
     * @throws IOException                if filesystem I/O error occurs
     */
    public void clean() throws MyGitIllegalStateException, IOException {
        internalUpdater.getLogger().trace("clean -- started");
        cleanTree(internalUpdater.getHeadTree(),
                  myGitRepositoryRootDirectory,
                  internalUpdater.readIndexPaths()
                                 .stream()
                                 .map(myGitRepositoryRootDirectory::relativize)
                                 .collect(Collectors.toSet()));
        internalUpdater.getLogger().trace("clean -- done");
    }

    private void resetStates(@Nullable Tree currentTree,
                             @NotNull Path pathPrefix,
                             @NotNull Set<Path> args)
            throws IOException, MyGitIllegalStateException {
        final List<Tree.TreeEdge> treeEdges = currentTree == null
                ? new ArrayList<>()
                : currentTree.getEdgesToChildren();
        for (Tree.TreeEdge childEdge : treeEdges) {
            final Path childPath = Paths.get(pathPrefix.toString(), childEdge.getName());
            if (childEdge.getType().equals(Tree.TYPE)) {
                Tree childTree = internalUpdater.readTree(childEdge.getHash());
                if (args.contains(childPath)) {
                    internalUpdater.loadFilesFromTree(childTree,
                            childPath);
                } else {
                    resetStates(childTree, childPath, args);
                }
            } else {
                if (args.contains(childPath)) {
                    Tree tempTree = new Tree();
                    tempTree.addEdgeToChild(childEdge);
                    internalUpdater.loadFilesFromTree(tempTree, pathPrefix);
                }
            }
        }
    }

    private boolean foundUncommittedChanges()
            throws MyGitIllegalStateException, IOException {
        Map<Path,FileStatus> statusMap = status();
        return statusMap
                .values()
                .stream()
                .anyMatch(fileStatus -> !fileStatus.equals(FileStatus.UNSTAGED))
            || !internalUpdater.readIndexPaths().isEmpty();
    }

    private void cleanTree(@Nullable Tree currentTree,
                           @NotNull Path pathPrefix,
                           @NotNull Set<Path> stagedPaths)
            throws IOException, MyGitIllegalStateException {
        final List<Path> unstagedPaths = Files.list(pathPrefix)
                .filter(path -> !isMyGitInternalPath(path))
                .collect(Collectors.toList());
        final List<Tree.TreeEdge> treeEdges = currentTree == null
                ? new ArrayList<>()
                : currentTree.getEdgesToChildren();
        for (Tree.TreeEdge childEdge : treeEdges) {
            final Path childPath = Paths.get(pathPrefix.toString(), childEdge.getName());
            unstagedPaths.remove(childPath);
            if (childEdge.getType().equals(Tree.TYPE)) {
                cleanTree(internalUpdater.readTree(childEdge.getHash()),
                          childPath,
                          stagedPaths);
            }
        }
        unstagedPaths.removeAll(stagedPaths);
        for (Path path : unstagedPaths) {
            if (Files.isDirectory(path)) {
                InternalUpdater.deleteDirectoryRecursively(path);
            } else {
                Files.delete(path);
            }
        }
    }

    private void resetIndexPaths(@NotNull String[] arguments)
            throws MyGitIllegalStateException, MyGitIllegalArgumentException, IOException {
        performUpdateToIndex(arguments, paths -> paths::remove);
    }

    private void buildStagingStatus(@Nullable Tree currentTree,
                                    @NotNull Path pathPrefix,
                                    @NotNull Set<Path> stagedPaths,
                                    @NotNull Map<Path, FileStatus> result)
            throws IOException, MyGitIllegalStateException {
        internalUpdater.getLogger().trace("Building stage status for path=" + pathPrefix.toString() + " -- started");
        final List<Path> unstagedPaths = Files.list(pathPrefix)
                .filter(path -> !isMyGitInternalPath(path))
                .collect(Collectors.toList());
        unstagedPaths.removeAll(stagedPaths);
        final List<Tree.TreeEdge> treeEdges = currentTree == null
                ? new ArrayList<>()
                : currentTree.getEdgesToChildren();
        for (Tree.TreeEdge childEdge : treeEdges) {
            final Path childPath = Paths.get(pathPrefix.toString(), childEdge.getName());
            unstagedPaths.remove(childPath);
            if (childEdge.getType().equals(Tree.TYPE)) {
                buildStagingStatus(internalUpdater.readTree(childEdge.getHash()),
                        childPath,
                        stagedPaths,
                        result);
            } else {
                buildFileStagingStatus(childPath, stagedPaths, childEdge.getHash(), result);
            }
        }
        unstagedPaths.forEach(path -> result.put(path, FileStatus.UNSTAGED));
        internalUpdater.getLogger().trace("Building stage status for path=" + pathPrefix.toString() + " -- done");
    }

    private void buildFileStagingStatus(@NotNull Path filePath,
                                              @NotNull Set<Path> stagedPaths,
                                              @NotNull String committedHash,
                                              @NotNull Map<Path, FileStatus> result)
            throws IOException, MyGitIllegalStateException {
        FileStatus fileStatus;
        if (stagedPaths.contains(filePath)) {
            fileStatus = FileStatus.STAGED;
        } else if (!(filePath.toFile().exists() && filePath.toFile().isFile())) {
            fileStatus = FileStatus.DELETED;
        } else if (!committedHash.equals(internalUpdater.computeFileHashFromPath(filePath))) {
            fileStatus = FileStatus.MODIFIED;
        } else {
            return;
        }
        result.put(filePath, fileStatus);
        internalUpdater.getLogger().trace("Stage status for path=" + filePath.toString() + " -- " + fileStatus.name());
    }

    private void performUpdateToIndex(@NotNull String[] arguments,
                                      @NotNull Function<Set<Path>, Consumer<Path>> action)
            throws MyGitIllegalStateException, MyGitIllegalArgumentException, IOException {
        internalUpdater.getLogger().trace("updating index -- started");
        internalUpdater.getLogger().trace("updating index -- checking paths");
        final List<Path> argsPaths = new ArrayList<>();
        for (String argument : arguments) {
            internalUpdater.getLogger().trace("checking path " + argument);
            Path path = Paths.get(argument);
            try {
                myGitRepositoryRootDirectory.relativize(path);
            } catch (IllegalArgumentException e) {
                throw new MyGitIllegalArgumentException("Path '" + argument + "' is located outside MyGit repository");
            }
            if (isMyGitInternalPath(path)) {
                throw new MyGitIllegalArgumentException("Path '" + argument + "' is internal for MyGit repository files");
            }
            if (!Files.exists(path)) {
                throw new MyGitIllegalArgumentException("Path '" + argument + "' does not exist");
            }
            argsPaths.add(path);
            if (path.toFile().isDirectory()) {
                Files.list(path).forEach(argsPaths::add);
            }
            internalUpdater.getLogger().trace("checking path " + argument + " -- OK");
        }
        internalUpdater.getLogger().trace("updating index -- checking paths -- done");
        internalUpdater.getLogger().trace("updating index itself");
        final Set<Path> indexedPaths = internalUpdater.readIndexPaths();
        final Consumer<Path> indexUpdater = action.apply(indexedPaths);
        argsPaths.forEach(indexUpdater);
        internalUpdater.writeIndexPaths(indexedPaths);
        internalUpdater.getLogger().trace("updating index -- done");
    }

    @NotNull
    private String mergeTrees(@NotNull Tree baseTree, @NotNull Tree otherTree)
            throws MyGitIllegalStateException, IOException {
        final Tree mergedTree = new Tree();
        final ListIterator<Tree.TreeEdge> baseIterator = baseTree.getEdgesToChildren().listIterator();
        final ListIterator<Tree.TreeEdge> otherIterator = otherTree.getEdgesToChildren().listIterator();
        while (baseIterator.hasNext() && otherIterator.hasNext()) {
            final Tree.TreeEdge baseTreeEdge = baseIterator.next();
            final Tree.TreeEdge otherTreeEdge = otherIterator.next();
            int comparisonResult = baseTreeEdge.getName().compareTo(otherTreeEdge.getName());
            if (comparisonResult == 0) {
                if (baseTreeEdge.isDirectory() && otherTreeEdge.isDirectory()) {
                    final Tree baseChildTree = internalUpdater.readTree(baseTreeEdge.getName());
                    final Tree otherChildTree = internalUpdater.readTree(otherTreeEdge.getName());
                    mergedTree.addEdgeToChild(new Tree.TreeEdge(
                            mergeTrees(baseChildTree, otherChildTree),
                            baseTreeEdge.getName(),
                            baseTreeEdge.getType()));
                } else {
                    mergedTree.addEdgeToChild(
                            baseTreeEdge.getCreationDate().compareTo(otherTreeEdge.getCreationDate()) > 0
                                    ? baseTreeEdge
                                    : otherTreeEdge);
                }
            } else if (comparisonResult < 0) {
                mergedTree.addEdgeToChild(baseTreeEdge);
                otherIterator.previous();
            } else {
                mergedTree.addEdgeToChild(otherTreeEdge);
                baseIterator.previous();
            }
        }
        while (baseIterator.hasNext()) {
            mergedTree.addEdgeToChild(baseIterator.next());
        }
        while (otherIterator.hasNext()) {
            mergedTree.addEdgeToChild(otherIterator.next());
        }
        return internalUpdater.writeObjectToFilesystem(mergedTree);
    }

    @NotNull
    private String rebuildTree(@Nullable Tree currentTree,
                               @NotNull Path pathPrefix,
                               Set<Path> indexedPaths)
            throws IOException, MyGitIllegalStateException {
        internalUpdater.getLogger().trace("rebuilding tree at path=" + pathPrefix.toString() + " -- started");
        final List<Path> filePaths = Files.list(pathPrefix)
                .filter(path -> !isMyGitInternalPath(path))
                .collect(Collectors.toList());
        final Tree newTree = new Tree();
        final List<Tree.TreeEdge> treeEdges = currentTree == null
                ? new ArrayList<>()
                : currentTree.getEdgesToChildren();
        for (Tree.TreeEdge childEdge : treeEdges) {
            final Path childPath = Paths.get(pathPrefix.toString(), childEdge.getName());
            filePaths.remove(childPath);
            final Tree.TreeEdge newTreeEdge = rebuildTreeEdge(childEdge, childPath, indexedPaths);
            if (newTreeEdge != null) {
                newTree.addEdgeToChild(newTreeEdge);
            }
        }
        internalUpdater.getLogger().trace("indexed paths:");
        for (Path path : indexedPaths) {
            internalUpdater.getLogger().trace(path.toString());
        }
        for (Path path : filePaths) {
            if (indexedPaths.contains(path)) {
                if (Files.isDirectory(path)) {
                    final String treeHash = rebuildTree(null, path, indexedPaths);
                    newTree.addEdgeToChild(new Tree.TreeEdge(treeHash, path.getFileName().toString(), Tree.TYPE));
                } else {
                    final String blobHash = internalUpdater.writeBlobFromPath(path);
                    newTree.addEdgeToChild(new Tree.TreeEdge(blobHash, path.getFileName().toString(), Blob.TYPE));
                }
                internalUpdater.getLogger().trace("added path=" + path);
            }
            internalUpdater.getLogger().trace("checked path=" + path);
        }
        internalUpdater.getLogger().trace("rebuilding tree at path=" + pathPrefix.toString() + " -- done");
        return internalUpdater.writeObjectToFilesystem(newTree);
    }

    @Nullable
    private Tree.TreeEdge rebuildTreeEdge(@NotNull Tree.TreeEdge child,
                                          @NotNull Path path,
                                          @NotNull Set<Path> indexedPaths)
            throws MyGitIllegalStateException, IOException {
        internalUpdater.getLogger().trace("rebuilding tree edge at path=" + path.toString() + " -- started");
        Tree.TreeEdge result = null;
        if (Files.exists(path)) {
            switch (child.getType()) {
                case Tree.TYPE:
                    final Tree childTree = internalUpdater.readTree(child.getHash());
                    result = path.toFile().isDirectory()
                            ? new Tree.TreeEdge(rebuildTree(childTree, path, indexedPaths),
                                                child.getName(),
                                                child.getType())
                            : new Tree.TreeEdge(internalUpdater.writeBlobFromPath(path),
                                                child.getName(),
                                                Blob.TYPE);
                    break;
                case Blob.TYPE:
                    final Blob childBlob = internalUpdater.readBlob(child.getHash());
                    if (path.toFile().isDirectory()) {
                        result = indexedPaths.contains(myGitRepositoryRootDirectory.relativize(path))
                                ? new Tree.TreeEdge(rebuildTree(null, path, indexedPaths), child.getName(), Tree.TYPE)
                                : child;
                    } else {
                        final byte[] committedContent = childBlob.getContents();
                        final byte[] currentContent = Files.readAllBytes(path);
                        result = Arrays.equals(committedContent, currentContent)
                                ? child
                                : new Tree.TreeEdge(internalUpdater.writeBlobFromPath(path),
                                                    child.getName(),
                                                    Blob.TYPE);
                    }
                    break;
                default:
                    throw new MyGitIllegalStateException("Got unknown type in process of the tree traversal: "
                            + child.getType());
            }
        }
        internalUpdater.getLogger().trace("rebuilding tree edge at path=" + path.toString() + " -- done");
        return result;
    }

    private void buildCommitTree(@NotNull Commit currentCommit, @NotNull TreeSet<Commit> commitTree)
            throws MyGitIllegalStateException, IOException {
        if (!commitTree.contains(currentCommit)) {
            commitTree.add(currentCommit);
            for (String parentHash : currentCommit.getParentCommitsHashes()) {
                final Commit parentCommit = internalUpdater.readCommit(parentHash);
                buildCommitTree(parentCommit, commitTree);
            }
        }
    }

    /**
     * Gets head status
     * @return head status as an instance of {@link HeadStatus}
     * @throws MyGitIllegalStateException if an internal error occurs
     * @throws IOException if filesystem I/O error occurs
     */
    public HeadStatus getHeadStatus() throws IOException, MyGitIllegalStateException {
        internalUpdater.getLogger().trace("getting head status -- started");
        HeadStatus headStatus = internalUpdater.getHeadStatus();
        internalUpdater.getLogger().trace("getting head status -- done");
        return headStatus;
    }

    private List<String> listCommitHashes() throws MyGitIllegalStateException, IOException {
        return internalUpdater.listCommitHashes();
    }

    /**
     * Gets the list of Branches in MyGit repository
     * @return {@link List} of Branches in MyGit repository
     * @throws MyGitIllegalStateException if an internal error occurs
     * @throws IOException if filesystem I/O error occurs
     */
    public List<Branch> listBranches() throws MyGitIllegalStateException, IOException {
        internalUpdater.getLogger().trace("listing branches -- started");
        List<Branch> branchList = internalUpdater.listBranches();
        internalUpdater.getLogger().trace("listing branches -- done");
        return branchList;
    }

    private boolean isMyGitInternalPath(@Nullable Path path) {
        return myGitRepositoryRootDirectory.equals(path)
                || pathContainsToken(myGitRepositoryRootDirectory.relativize(path), ".mygit");
    }

    private boolean pathContainsToken(@Nullable Path path, @NotNull String token) {
        return path != null && (path.endsWith(token) || pathContainsToken(path.getParent(), token));
    }

    @Nullable
    Tree.TreeEdge findElementInHeadTree(@NotNull Path path)
            throws MyGitIllegalStateException, IOException {
        path = myGitRepositoryRootDirectory.relativize(path);
        Tree.TreeEdge result = new Tree.TreeEdge(internalUpdater.getHeadCommit().getRootTreeHash(), "", "");
        for (Path token : path) {
            Tree tree = internalUpdater.readTree(result.getHash());
            result = null;
            for (Tree.TreeEdge edge : tree.getEdgesToChildren()) {
                if (edge.getName().equals(token.toString())) {
                    result = edge;
                    break;
                }
            }
            if (result == null) {
                return null;
            }
        }
        return result;
    }
}
