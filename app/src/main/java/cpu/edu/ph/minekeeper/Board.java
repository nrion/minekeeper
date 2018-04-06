package cpu.edu.ph.minekeeper;

import android.graphics.Point;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Point> minePoints = new ArrayList<>();
    private Cell[][] cells;
    private int column;
    private int row;
    private int mineCount;
    private int life;
    private int flags;

    private boolean steppedOnAMine;

    public Board(int column, int row, int life, int mineCount) {
        this.column = column;
        this.row = row;
        this.mineCount = mineCount;
        this.life = life;
        this.flags = 0;

        cells = new Cell[column][row];
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < row; j++) {
                cells[i][j] = new Cell(0);
            }
        }
    }

    public int getRowCount() {
        return row;
    }

    public int getColumnCount() {
        return column;
    }

    public boolean gameOver() {
        return life < 1;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void generateMines() {
        while (minePoints.size() != mineCount) {
            int randomX = (int) (Math.random() * column);
            int randomY = (int) (Math.random() * row);

            Point minePoint = new Point(randomX, randomY);

            if (!minePoints.contains(minePoint)) {
                minePoints.add(minePoint);
                cells[randomX][randomY].setAsMine();
            }
        }
    }

    public void generateValues() {
        for (Point mineLocation : minePoints) {
            int x = mineLocation.x;
            int y = mineLocation.y;

            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (i < column && i >= 0
                            && j < row && j >= 0) {
                        cells[i][j].increaseValue();
                    }
                }
            }
        }
    }

    public void flagUnflagCell(int x, int y, boolean flag) {
        if (!gameOver() && !hasWon()
                && !steppedOnAMine && !cells[x][y].isOpen()) {
            if (flag && !cells[x][y].isFlagged()) {
                if (flags < mineCount) {
                    cells[x][y].flag();
                    flags++;
                }
            }
            else if (!flag && cells[x][y].isFlagged()) {
                if (flags > 0) {
                    cells[x][y].unflag();
                    flags--;
                }
            }
        }
    }

    public int getRemainingFlags() {
        return mineCount - flags;
    }

    public void revealMines() {
        for (Point mineLocation : minePoints) {
            cells[mineLocation.x][mineLocation.y].reveal();
        }
    }

    public void revealCell(int x, int y, boolean reveal) {
        if (cells[x][y].isMine()) {
            if (steppedOnAMine && !reveal) {
                steppedOnAMine = false;
                cells[x][y].conceal();
            }
            else {
                life--;
                steppedOnAMine = true;
                cells[x][y].reveal();
            }
        }
        else {
            flipCellAndSurroundings(x, y);
        }
    }

    public boolean hasSteppedOnAMine() {
        return steppedOnAMine;
    }

    public int getLife() {
        return life;
    }

    public boolean hasWon() {
        int opened = 0;

        for (int i = 0; i < column; i++) {
            for (int j = 0; j < row; j++) {
                if (!minePoints.contains(new Point(i, j))) { // if there is no mine on this cell
                    if (cells[i][j].isOpen()) { // and the cell is open
                        opened++;
                    }
                }
            }
        }

        if (opened == row * column - mineCount) {
            return true;
        }
        return false;
    }

    public void flipCellAndSurroundings(int x, int y) {
        if (x < column && x >= 0
                && y < row && y >= 0) {
            if (!cells[x][y].isOpen() && !cells[x][y].isFlagged()) {
                if (cells[x][y].getValue() > 0) {
                    cells[x][y].reveal();
                }
                else if (cells[x][y].getValue() == 0) {
                    cells[x][y].reveal();
                    flipCellAndSurroundings(x + 1, y);
                    flipCellAndSurroundings(x - 1, y);
                    flipCellAndSurroundings(x, y + 1);
                    flipCellAndSurroundings(x, y - 1);
                    flipCellAndSurroundings(x - 1, y - 1);
                    flipCellAndSurroundings(x + 1, y - 1);
                    flipCellAndSurroundings(x - 1, y + 1);
                    flipCellAndSurroundings(x + 1, y + 1);
                }
            }
        }
    }
}
