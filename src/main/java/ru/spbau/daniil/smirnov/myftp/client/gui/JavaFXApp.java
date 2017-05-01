package ru.spbau.daniil.smirnov.myftp.client.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXApp extends Application {
    private final Group root = new Group();

    private final Scene scene = new Scene(root);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MyFTP spbau.daniil.smirnov.myftp.client");
        // TODO: add root directory dialog
        root.getChildren().add(
                new FilesystemBrowserBuilder().buildFileSystemBrowser("/"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
