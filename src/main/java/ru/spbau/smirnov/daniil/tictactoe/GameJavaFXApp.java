package ru.spbau.smirnov.daniil.tictactoe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Entrance point to the application
 */
public class GameJavaFXApp extends Application {
    private final int minSize = 320;

    private Stage primaryStage;
    private VBox rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    static void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Tic-Tac-Toe Online");

        initGame();
    }

    private void initGame() {
        try {
            @NotNull
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/GameLayout.fxml"));
            rootLayout = loader.load();

            @NotNull
            final GameLayoutController gameLayoutController = loader.getController();
            gameLayoutController.setGameJavaFXApp(this);

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setMinWidth(minSize);
            primaryStage.setMinHeight(minSize);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load the game layout. Application will be stopped");
            Platform.exit();
            System.exit(0);
        }
    }
}
