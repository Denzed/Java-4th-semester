package mygit.objects;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Base class for file difference between repository HEAD and filesystem
 */
public class FileDifference {
    @NotNull
    private Path path;
    @NotNull
    private final FileDifferenceType type;
    @NotNull
    private final FileDifferenceStageStatus stageStatus;

    /**
     * Constructs FileDifference with a given path, difference type and stage status
     * @param path file path
     * @param type file difference type
     * @param stageStatus file stage status
     */
    public FileDifference(@NotNull Path path, @NotNull FileDifferenceType type, @NotNull FileDifferenceStageStatus stageStatus) {
        this.path = path;
        this.type = type;
        this.stageStatus = stageStatus;
    }

    /**
     * Gets file path
     * @return file path
     */
    @NotNull
    public Path getPath() {
        return path;
    }

    /**
     * Gets file difference type
     * @return file difference type
     */
    @NotNull
    public FileDifferenceType getType() {
        return type;
    }

    /**
     * Gets file stage status
     * @return file stage status
     */
    @NotNull
    public FileDifferenceStageStatus getStageStatus() {
        return stageStatus;
    }
}