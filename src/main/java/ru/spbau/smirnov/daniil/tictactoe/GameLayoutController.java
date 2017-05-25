package ru.spbau.smirnov.daniil.tictactoe;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import ru.spbau.smirnov.daniil.tictactoe.net.RequestSender;

/**
 * Class used to control Game layout loaded from GameLayout.fxml
 */
public class GameLayoutController {
    private final static int STATUS_UPDATE_INTERVAL = 300;

    private final GameHandler gameHandler;

    private final Timeline statusUpdater;

    @FXML
    private GridPane gameField;

    @FXML
    private Label statusLabel;

    private GameJavaFXApp gameJavaFXApp;

    // Constructs our controller
    public GameLayoutController() {
        gameHandler = new GameHandler();
        statusUpdater = new Timeline(
                new KeyFrame(
                        Duration.millis(STATUS_UPDATE_INTERVAL),
                        event -> statusLabel.setText(gameHandler.getStatus())));
        statusUpdater.setCycleCount(Timeline.INDEFINITE);
    }

    @FXML
    private void initialize() {
        for (Node node: gameField.getChildren()) {
            if (node instanceof Button) {
                node.setOnMouseClicked(event -> onMouseClicked((Button) node));
            }
        }

        statusUpdater.play();
    }

    private void onMouseClicked(Button button) {
        int row = GridPane.getRowIndex(button);
        int column = GridPane.getColumnIndex(button);
        if (gameHandler.canMakeTurn(row, column, gameHandler.getUs())) {
            button.setText(gameHandler.getCurrentPlayer() == GameHandler.Figure.O ? "O" : "X");
            gameHandler.makeTurn(row, column);
            new RequestSender(GameHandler.PORT).sendTurnRequest(row, column);
        }
    }

    void setGameJavaFXApp(GameJavaFXApp gameJavaFXApp) {
        this.gameJavaFXApp = gameJavaFXApp;
    }
}