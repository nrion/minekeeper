package cpu.edu.ph.minekeeper;

public class Game {
    private Board board;
    private Command commandToBeUndone;

    private static final String TAG = Game.class.getSimpleName();

    public Game(Board board) {
        this.board = board;
        this.board.generateMines();
        this.board.generateValues();
    }

    public void revealCellPressed(int x, int y) {
        Command command = new RevealCellCommand(board, x, y);
        commandToBeUndone = command;
        command.execute();
    }

    public void flagCellPressed(int x, int y) {
        Command flagCommand = new FlagUnflagCommand(board, x, y);
        flagCommand.execute();

    }

    public void unflagCellPressed(int x, int y) {
        Command unflagCommand = new FlagUnflagCommand(board, x, y);
        unflagCommand.unexecute();
    }

    public void undoPressed() {
        if (commandToBeUndone != null) {
            commandToBeUndone.unexecute();
        }
    }
}