package ru.spbau.daniil.smirnov.myftp.client.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myftp.client.Client;
import ru.spbau.daniil.smirnov.myftp.server.actions.ListDirectoryAction;

import java.io.IOException;
import java.util.List;

import static ru.spbau.daniil.smirnov.myftp.client.gui.JavaFXApp.showError;

/**
 * A {@link TreeItem}-extending class which represents a node in a FileBrowser {@link TreeView}
 */
public class FileBrowserItem extends TreeItem<FileWrapper> {
    private final boolean isLeaf;

    @NotNull
    private final Client client;

    private boolean isFirstTimeChilderRequested = true;

    /**
     * Constructs the class
     * @param client {@link Client} which is used to send requests to the server
     * @param fileWrapper {@link java.io.File} wrappper with information about current node
     * @param isFile whether this node represents a file or a directory
     */
    public FileBrowserItem(@NotNull Client client, @NotNull final FileWrapper fileWrapper, boolean isFile) {
        super(fileWrapper);
        this.client = client;
        isLeaf = isFile;
    }

    /**
     * Gets the list of children of the current {@link TreeView} node
     * @return the list of children of the current {@link TreeView} node
     */
    @NotNull
    @Override
    public ObservableList<TreeItem<FileWrapper>> getChildren() {
        if (isFirstTimeChilderRequested) {
            try {
                super.getChildren().setAll(buildChildren(this));
                isFirstTimeChilderRequested = false;
            } catch (IOException e) {
                Platform.runLater(() -> showError("An exception occurred while listing directory contents."
                        + " The action will be cancelled"));
                setExpanded(false);
            }
        }
        return super.getChildren();
    }

    /**
     * Gets whether the current node is a leaf node
     * @return {@code true} if so, {@code false} otherwise
     */
    @Override
    public boolean isLeaf() {
        return isLeaf;
    }

    @NotNull
    private ObservableList<TreeItem<FileWrapper>> buildChildren(@NotNull TreeItem<FileWrapper> treeItem)
            throws IOException {
        FileWrapper file = treeItem.getValue();

        if (file != null && !treeItem.isLeaf()) {
            List<ListDirectoryAction.ListActionResultEntry> files =
                    client.list(file.getAbsolutePath());
            if (files != null) {
                ObservableList<TreeItem<FileWrapper>> children = FXCollections.observableArrayList();
                for (ListDirectoryAction.ListActionResultEntry entry : files) {
                    FileWrapper childFile =
                            new FileWrapper(file.toPath().resolve(entry.getName()).toString());
                    children.add(new FileBrowserItem(client, childFile, entry.isFile()));
                }
                return children;
            }
        }
        return FXCollections.emptyObservableList();
    }
}