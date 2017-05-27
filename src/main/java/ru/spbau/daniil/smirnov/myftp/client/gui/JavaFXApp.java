package ru.spbau.daniil.smirnov.myftp.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class JavaFXApp extends Application {
    static private final int MIN_WIDTH = 320;
    static private final int MIN_HEIGHT = 480;

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private String serverAddress;
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
            Pair<String,String> response = askForIPAndRootDirectory();
            if (response == null) {
                showError("No required information provided. Application will be closed");
                Platform.exit();
                System.exit(0);
            }
            serverAddress = response.getKey();
            rootDirectory = response.getValue();

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

            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
        } catch (IOException e) {
            showError("Could not load the main layout. Application will be stopped");
            Platform.exit();
            System.exit(0);
        }
    }

    @Nullable
    private Pair<String,String> askForIPAndRootDirectory() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Server IP and root directory");

        ButtonType submitButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField IP = new TextField();
        IP.setText("localhost");
        TextField rootDirectory = new TextField();
        rootDirectory.setText("/");

        gridPane.add(new Label("IP:"), 0, 0);
        gridPane.add(IP, 1, 0);
        gridPane.add(new Label("Root directory:"), 0, 1);
        gridPane.add(rootDirectory, 1, 1);

        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(IP::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return new Pair<>(IP.getText(), rootDirectory.getText());
            }
            return null;
        });
        return dialog.showAndWait().orElse(null);
    }

    @NotNull
    String getRootDirectory() {
        return rootDirectory;
    }

    @NotNull
    String getServerAddress() {
        return serverAddress;
    }
}
