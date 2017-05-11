package ru.spbau.daniil.smirnov.memory;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entrance point to the application
 */
public class MemoryMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new GameField());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
