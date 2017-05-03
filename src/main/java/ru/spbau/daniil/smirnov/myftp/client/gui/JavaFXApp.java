package ru.spbau.daniil.smirnov.myftp.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class JavaFXApp extends Application {
    private final Group root = new Group();

    private final Scene scene = new Scene(root);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MyFTP client");
        String rootDirectory = getRootDirectory();
        if (rootDirectory == null) {
            showError("No root directory specified. Application will be closed");
            Platform.exit();
            System.exit(0);
        }
        root.getChildren().add(
                new FilesystemBrowserBuilder().buildFileSystemBrowser(rootDirectory));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Nullable
    private String getRootDirectory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Root directory");
        dialog.setHeaderText(null);
        dialog.setContentText("Please, enter the root directory:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    static void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
