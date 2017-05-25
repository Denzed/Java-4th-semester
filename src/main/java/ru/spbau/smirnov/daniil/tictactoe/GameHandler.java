package ru.spbau.smirnov.daniil.tictactoe;

import ru.spbau.smirnov.daniil.tictactoe.net.RequestSender;
import ru.spbau.smirnov.daniil.tictactoe.net.Server;

/**
 * Class which handles internal game logic
 */
public class GameHandler {
    static final int PORT = 4179;
    private static final int N = 3;
    private final Figure field[][];

    private final Figure us;

    private Figure winner = Figure.NONE;
    private Figure currentPlayer;
    private State state;
    private Server server = null;
    private boolean fieldFull;

    GameHandler() {
        field = new Figure[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                field[i][j] = Figure.NONE;
            }
        }
        currentPlayer = Figure.X;
        boolean registered = tryToRegister();
        if (registered) {
            us = Figure.O;
            state = State.GAME_RUNNING;
        } else {
            startServer();
            us = Figure.X;
            state = State.WAITING_FOR_PLAYER;
        }
    }

    Figure getUs() {
        return us;
    }

    private boolean isGameEnded() {
        return isFieldFull()
                || isThreeInARowVertical()
                || isThreeInARowHorizontal()
                || isThreeInARowDiagonal();
    }

    private boolean isThreeInARowVertical() {
        return isThreeInARow(0, 0, 0, 1)
                || isThreeInARow(1, 0, 0, 1)
                || isThreeInARow(2, 0, 0, 1);
    }

    private boolean isThreeInARowHorizontal() {
        return isThreeInARow(0, 0, 1, 0)
                || isThreeInARow(0, 1, 1, 0)
                || isThreeInARow(0, 2, 1, 0);
    }

    private boolean isThreeInARowDiagonal() {
        return isThreeInARow(0, 0, 1, 1)
                || isThreeInARow(2, 0, -1, 1);
    }

    private boolean isThreeInARow(int x0, int y0, int dx, int dy) {
        return field[x0][y0] == field[x0 + dx][y0 + dy]
                && field[x0][y0] == field[x0 + 2 * dx][y0 + 2 * dy];
    }

    private boolean isFieldFull() {
        for (Figure line[] : field) {
            for (Figure cell : line) {
                if (cell == Figure.NONE) {
                    return false;
                }
            }
        }
        return true;
    }

    private void startServer() {
        server = new Server(PORT, this);
        server.start();
    }

    private boolean tryToRegister() {
        return new RequestSender(PORT).sendRegisterRequest();
    }

    public void makeTurn(int row, int column) {
        if (canMakeTurn(row, column, currentPlayer)) {
            field[row][column] = currentPlayer;
            if (isGameEnded()) {
                if (isThreeInARowVertical()
                        || isThreeInARowHorizontal()
                        || isThreeInARowDiagonal()) {
                    winner = currentPlayer;
                }
                state = State.GAME_ENDED;
                server.stop();
            }
            currentPlayer = (currentPlayer == Figure.O ? Figure.X : Figure.O);

        }
    }

    String getStatus() {
        if (state == State.WAITING_FOR_PLAYER) {
            return "Waiting for other player to connect...";
        } else if (state == State.GAME_RUNNING) {
            return (currentPlayer == us ? "Your turn" : "Waiting for other player to make their turn...");
        } else {
            return (getWinner() == Figure.NONE ? "It's a draw" : getWinner() + " won");
        }
    }

    public boolean canMakeTurn(int row, int column, Figure player) {
        return (state == State.GAME_RUNNING
                && player == getCurrentPlayer()
                && field[row][column] == Figure.NONE);
    }

    Figure getCurrentPlayer() {
        return currentPlayer;
    }

    public Figure getOtherPlayer() {
        return (us == Figure.O ? Figure.X : Figure.O);
    }

    Figure getWinner() {
        return winner;
    }

    public int register() {
        if (state == State.WAITING_FOR_PLAYER) {
            state = State.GAME_RUNNING;
            return 1;
        }
        return 0;
    }

    public enum State {
        WAITING_FOR_PLAYER, GAME_RUNNING, GAME_ENDED
    }

    public enum Figure {
        X, O, NONE
    }
}
