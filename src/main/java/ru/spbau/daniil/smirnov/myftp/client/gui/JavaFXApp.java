package ru.spbau.daniil.smirnov.myftp.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

public class JavaFXApp extends Application {
    private final int minWidth = 320;
    private final int minHeight = 480;

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private String rootDirectory;

    /**
     * Entrance point to the application from the command line
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    static void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    /**
     * Entrance point to the JavaFX application
     * @param primaryStage primary {@link Stage}
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MyFTP client");

        initFileBrowser();
    }

    private void initFileBrowser() {
        try {
            rootDirectory = askForRootDirectory();
            if (rootDirectory == null) {
                showError("No root directory specified. Application will be closed");
                Platform.exit();
                System.exit(0);
            }

            @NotNull
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/client/gui/MainLayout.fxml"));
            rootLayout = loader.load();

            @NotNull
            final MainLayoutController mainLayoutController = loader.getController();
            mainLayoutController.setJavaFXApp(this);

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setMinWidth(minWidth);
            primaryStage.setMinHeight(minHeight);
        } catch (IOException e) {
            showError("Could not load the main layout. Application will be stopped");
            Platform.exit();
            System.exit(0);
        }
    }

    @Nullable
    private String askForRootDirectory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Root directory");
        dialog.setHeaderText(null);
        dialog.setContentText("Please, enter the root directory:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    @NotNull
    String getRootDirectory() {
        return rootDirectory;
    }
}
