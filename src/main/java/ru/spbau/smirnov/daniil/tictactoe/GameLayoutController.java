package ru.spbau.smirnov.daniil.tictactoe;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import ru.spbau.smirnov.daniil.tictactoe.net.RequestSender;

/**
 * Class used to control Game layout loaded from GameLayout.fxml
 */
public class GameLayoutController {
    private final GameHandler gameHandler;

    @FXML
    private GridPane gameField;

    @FXML
    private Label statusLabel;

    private GameJavaFXApp gameJavaFXApp;


    // Constructs our controller
    public GameLayoutController() {
        gameHandler = new GameHandler();
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void onMouseClicked(MouseEvent mouseEvent) {
        for (Node node: gameField.getChildren()) {
            if (node instanceof Button &&
                    node.getBoundsInParent().contains(mouseEvent.getSceneX(),
                                                      mouseEvent.getSceneY())) {
                int row = GridPane.getRowIndex(node);
                int column = GridPane.getColumnIndex(node);
                if (gameHandler.canMakeTurn(row, column, gameHandler.getUs())) {
                    ((Button) node).setText(
                            gameHandler.getCurrentPlayer() == GameHandler.Figure.O ? "O" : "X");
                    gameHandler.makeTurn(row, column);
                    new RequestSender(GameHandler.PORT).sendTurnRequest(row, column);
                    statusLabel.setText(gameHandler.getStatus());
                }
                return;
            }
        }
    }

    void setGameJavaFXApp(GameJavaFXApp gameJavaFXApp) {
        this.gameJavaFXApp = gameJavaFXApp;
    }
}