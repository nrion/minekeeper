package cpu.edu.ph.minekeeper;

import android.graphics.Point;

public class RevealCellCommand implements Command {
    private Board board;
    private Point currentPoint;
    private Point previousPoint;

    public RevealCellCommand(Board board, int x, int y) {
        this.board = board;
        this.currentPoint = new Point(x, y);
    }

    @Override
    public void execute() {
        previousPoint = currentPoint;
        board.revealCell(currentPoint.x, currentPoint.y, true);
    }

    @Override
    public void unexecute() {
        // you can only undo if you have stepped on a mine
        if (board.getCells()[previousPoint.x][previousPoint.y].isMine()) {
            board.revealCell(previousPoint.x, previousPoint.y, false);
        }
    }
}
