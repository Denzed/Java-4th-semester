package ru.spbau.daniil.smirnov.myftp.client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myftp.client.Client;
import ru.spbau.daniil.smirnov.myftp.server.ServerCommandLineApp;
import ru.spbau.daniil.smirnov.myftp.server.actions.ListDirectoryAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

class FilesystemBrowserBuilder {
    @NotNull
    final private Client client;

    FilesystemBrowserBuilder() {
        client = new Client(ServerCommandLineApp.PORT);
    }

    @NotNull
    TreeView buildFileSystemBrowser(@NotNull String rootDirectory) {
        TreeItem<FileWrapper> root = createNode(new FileWrapper(rootDirectory, true), false);
        TreeView<FileWrapper> treeView = new TreeView<>(root);
        treeView.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2) {
                TreeItem<FileWrapper> item = treeView.getSelectionModel().getSelectedItem();
                if (item.isLeaf()) {
                    File from = item.getValue();
                    File to = new FileChooser().showSaveDialog(null);
                    if (to != null) {
                        try {
                            Files.write(to.toPath(), client.get(from.getAbsolutePath()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            // TODO: add print to status bar
                        }
                    } else {
                        // TODO: add print to status bar that save cancelled
                    }
                }
            }
        });
        return treeView;
    }

    @NotNull
    private TreeItem<FileWrapper> createNode(@NotNull final FileWrapper f, boolean isFile) {
        return new TreeItem<FileWrapper>(f) {
            private boolean isLeaf = isFile;

            private boolean isFirstTimeChildren = true;

            @NotNull
            @Override
            public ObservableList<TreeItem<FileWrapper>> getChildren() {
                if (isFirstTimeChildren) {
                    try {
                        super.getChildren().setAll(buildChildren(this));
                        isFirstTimeChildren = false;
                    } catch (IOException e) {
                        // TODO: add status bar message
                        e.printStackTrace();
                    }
                }
                return super.getChildren();
            }

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
                            children.add(createNode(childFile, entry.isFile()));
                        }
                        return children;
                    }
                }
                return FXCollections.emptyObservableList();
            }
        };
    }
}
