package ru.spbau.daniil.smirnov.myftp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import ru.spbau.daniil.smirnov.myftp.client.Client;
import ru.spbau.daniil.smirnov.myftp.server.ServerCommandLineApp;

import java.io.File;
import java.nio.file.Files;

import static ru.spbau.daniil.smirnov.myftp.client.gui.JavaFXApp.showError;

/**
 * Class used to control Main layout loaded from MainLayout.fxml
 */
public class MainLayoutController {
    @NotNull
    final private Client client;

    @FXML
    private TreeView<FileWrapper> fileTreeView;

    private JavaFXApp javaFXApp;


    // Constructs our controller
    public MainLayoutController() {
        client = new Client(ServerCommandLineApp.PORT);
    }

    @FXML
    private void initialize() {
    }

    void setJavaFXApp(JavaFXApp javaFXApp) {
        this.javaFXApp = javaFXApp;
        fileTreeView.setRoot(
                new FileBrowserItem(
                        client,
                        new FileWrapper(this.javaFXApp.getRootDirectory(), true),
                        false));
    }

    @FXML
    private void onMouseClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2) {
            getAndSaveFile();
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            getAndSaveFile();
        }
    }

    private void getAndSaveFile() {
        TreeItem<FileWrapper> item = fileTreeView.getSelectionModel().getSelectedItem();
        if (item != null && item.isLeaf()) {
            File from = item.getValue();
            File to = new FileChooser().showSaveDialog(null);
            if (to != null) {
                try {
                    Files.write(to.toPath(), client.get(from.getAbsolutePath()));
                } catch (Exception e) {
                    showError("An exception occurred while getting file from server. The action will be cancelled.\n"
                            + e.getMessage());
                }
            } else {
                showError("No place to save specified. File transfer will be cancelled.");
            }
        }
    }
}
