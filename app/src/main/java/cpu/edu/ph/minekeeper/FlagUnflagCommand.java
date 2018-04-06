package cpu.edu.ph.minekeeper;

import android.graphics.Point;

public class FlagUnflagCommand implements Command {
    private Board board;
    private Point point;

    public FlagUnflagCommand(Board board, int x, int y) {
        this.board = board;
        this.point = new Point(x, y);
    }


    @Override
    public void execute() {
        board.flagUnflagCell(point.x, point.y, true);
    }

    @Override
    public void unexecute() {
        board.flagUnflagCell(point.x, point.y, false);
    }
}