package ru.spbau.daniil.smirnov.memory;

import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class which represents game field
 */
class GameField extends GridPane {
    private static final int N = 6;
    private static final double MIN_TILE_SIZE = 30;
    public static final double MIN_SIZE = N * MIN_TILE_SIZE;

    private String buttonNumbers[][];
    private Button buttons[][];

    private int firstClickedRow = -1;
    private int firstClickedColumn = -1;
    private int secondClickedRow = -1;
    private int secondClickedColumn = -1;

    GameField() {
        super();

        setAutoResizable();
        generateButtonNumbers();
        generateButtons();
    }

    private void setAutoResizable() {
        setMinSize(MIN_SIZE, MIN_SIZE);

        for (int j = 0; j < N; j++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(cc);
        }

        for (int j = 0; j < N; j++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            getRowConstraints().add(rc);
        }
    }

    private void generateButtons() {
        buttons = new Button[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Button button = new Button();
                button.setMinSize(MIN_TILE_SIZE, MIN_TILE_SIZE);
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                int coords[] = new int[]{i, j};
                button.setOnAction(event -> onButtonClicked(coords[0], coords[1]));
                GridPane.setConstraints(button, j, i);
                getChildren().add(button);
                buttons[i][j] = button;
            }
        }
    }

    private void generateButtonNumbers() {
        List<String> buttonLabels = new ArrayList<>(N * N);
        for (int i = 0; i < N * N / 2; ++i) {
            String string = Integer.toString(i);
            buttonLabels.add(string);
            buttonLabels.add(string);
        }
        Collections.shuffle(buttonLabels);
        buttonNumbers = new String[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                buttonNumbers[i][j] = buttonLabels.get(i * N + j);
            }
        }
    }

    private void onButtonClicked(int row, int column) {
        if (getClickedButtonsCount() == 2) {
            if (!buttons[firstClickedRow][firstClickedColumn].isDisabled()) {
                hideButton(firstClickedRow, firstClickedColumn);
                hideButton(secondClickedRow, secondClickedColumn);
            }
            clearClickedButtons();
        } else if (getClickedButtonsCount() == 1
                && (row != firstClickedRow
                || column != firstClickedColumn)) {
            setSecondClicked(row, column);
            showButton(row, column);
            if (buttonNumbers[firstClickedRow][firstClickedColumn].equals(
                    buttonNumbers[secondClickedRow][secondClickedColumn])) {
                buttons[firstClickedRow][firstClickedColumn].setDisable(true);
                buttons[secondClickedRow][secondClickedColumn].setDisable(true);
            }
        } else if (getClickedButtonsCount() == 0) {
            setFirstClicked(row, column);
            showButton(row, column);
        }
    }

    private void showButton(int row, int column) {
        buttons[row][column].setText(buttonNumbers[row][column]);
    }

    private void hideButton(int row, int column) {
        buttons[row][column].setText("");
    }

    private int getClickedButtonsCount() {
        return (firstClickedRow == -1
                ? 0
                : (secondClickedRow == -1 ? 1 : 2));
    }

    private void clearClickedButtons() {
        firstClickedColumn = -1;
        firstClickedRow = -1;
        secondClickedColumn = -1;
        secondClickedRow = -1;
    }

    private void setFirstClicked(int row, int column) {
        firstClickedRow = row;
        firstClickedColumn = column;
    }

    private void setSecondClicked(int row, int column) {
        secondClickedRow = row;
        secondClickedColumn = column;
    }
}
