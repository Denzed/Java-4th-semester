package ru.spbau.smirnov.daniil.tictactoe.net.actions;

/**
 * Represents making turn
 */
public class MakeTurnAction {
    public static final int ACTION_CODE = 2;

    private final int row;
    private final int column;

    /**
     * Generates the action
     * @param row row to make turn into
     * @param column columnt to make turn into
     */
    public MakeTurnAction(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Gets the desired row
     * @return the desired row
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the desired column
     * @return the desired column
     */
    public int getColumn() {
        return column;
    }
}
