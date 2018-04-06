package cpu.edu.ph.minekeeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = MainGameView.class.getSimpleName();

    private MainThread thread;

    private static Bitmap icons;
    private Board board;
    private Cell[][] cells;
    private Game game;

    private int rows;
    private int columns;

    private double cellSizePercent;
    private double sideMarginPercent = 0.05; // 5% of the screen width
    private double topMarginPercent = 0.09; // 9% of the screen height
    private double topBoardMarginPercent = topMarginPercent + 0.03; // 13% of the screen height

    private float topOptionsY;
    private float bottomOptionsY;

    private double[] leftOptionsXPercents  = new double[3];
    private double[] rightOptionsXPercents = new double[3];

    private float leftNewGameX;
    private float rightNewGameX;

    private String flagSwitch = "OFF";
    private boolean flagPressed;

    public MainGameView(Context context, int level) {
        super(context);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);

        int columnNumber;
        int rowNumber;
        int life;
        int mineAmount;

        // difficulty
        if (level == 1) { // beginner
            columnNumber = 9;
            rowNumber = 9;
            life = 3;
            mineAmount = 10;
        }
        else if (level == 2) { // intermediate
            columnNumber = 16;
            rowNumber = 16;
            life = 9;
            mineAmount = 40;
        }
        else { // expert
            columnNumber = 16;
            rowNumber = 20;
            life = 15;
            mineAmount = 99;
        }

        board = new Board(columnNumber, rowNumber, life, mineAmount);
        cells = board.getCells();
        game = new Game(board);

        this.rows = board.getRowCount();
        this.columns = board.getColumnCount();
    }

    public void stopGame() {
        thread.setRunning(false);
    }

    public void startGame() {
        thread.setRunning(true);
    }

    public static Bitmap getIcons() {
        return icons;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        icons = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icons);

        if (!thread.isAlive() && thread.getState() != Thread.State.NEW) {
            thread = new MainThread(getHolder(), this);
        }

        thread.setRunning(true);
        thread.start();

        cellSizePercent = (1 - (sideMarginPercent * 2)) / columns;
        int cellWidth = (int) Math.round(getWidth() * cellSizePercent);

        Cell.setSizeOfAllCells(cellWidth);

        // board width stays, the height changes
        for (int i = 0; i < columns; i++) {
            int x = (int) Math.round(getWidth() * (sideMarginPercent + cellSizePercent * i));

            for (int j = 0; j < rows; j++) {
                int y = (int) Math.round(getHeight() * (topBoardMarginPercent)
                        + getWidth() * cellSizePercent * j);
                cells[i][j].move(x, y);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;

        while (retry) {
            try {
                thread.join();
                retry = false;
            }
            catch (InterruptedException ex) {
                // do nothing
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() >= Math.round(getWidth() * leftOptionsXPercents[1])
                    && event.getX() <= Math.round(getWidth() * rightOptionsXPercents[1])) {
                if (event.getY() >= topOptionsY && event.getY() <= bottomOptionsY) {
                    if (flagPressed) {
                        flagPressed = false;
                        flagSwitch = "OFF";
                    }
                    else {
                        flagPressed = true;
                        flagSwitch = "ON";
                    }
                }
            }

            for (int i = 0; i < columns; i++) {
                for (int j = 0; j < rows; j++) {
                    if (!cells[i][j].isOpen()) {
                        cells[i][j].handleTouch(
                                (int) event.getX(),
                                (int) event.getY()
                        );
                    }

                    if (cells[i][j].isTouched()) {
                        if (flagPressed) {
                            if (cells[i][j].isFlagged()) {
                                game.unflagCellPressed(i, j);
                            }
                            else {
                                game.flagCellPressed(i, j);
                            }
                        }
                        else {
                            if (!board.gameOver() && !board.hasWon() && !board.hasSteppedOnAMine()
                                    && !cells[i][j].isOpen() && !cells[i][j].isFlagged()) {
                                game.revealCellPressed(i, j);
                            }
                        }
                    }
                }
            }


            if (event.getX() >= Math.round(getWidth() * leftOptionsXPercents[0])
                && event.getX() <= Math.round(getWidth() * rightOptionsXPercents[0])) {
                if (event.getY() >= topOptionsY && event.getY() <= bottomOptionsY) {
                    // check first if the move is valid
                    if (!board.gameOver() && !board.hasWon() && board.hasSteppedOnAMine()) {
                        game.undoPressed();
                    }
                }
            }

            if (event.getX() >= leftNewGameX && event.getX() <= rightNewGameX) {
                if (event.getY() >= topOptionsY && event.getY() <= bottomOptionsY) {
                    ((MainActivity) getContext()).restartGame();
                }
            }
        }
        return true;
    }
    public void draw(Canvas canvas) {
        if (canvas == null) {
            return;
        }

        Typeface droidbb = Typeface.createFromAsset(getContext().getAssets(), "fonts/droidbb.ttf");

        float mainTextSize = Math.round(getWidth() * 0.1);
        float subtextSize = Math.round(getWidth() * 0.05);

        Paint paint = new Paint();
        paint.setColor(Color.rgb(54, 61, 67));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(droidbb);
        paint.setTextSize(mainTextSize);

        Bitmap newGame = BitmapFactory.decodeResource(
                getContext().getResources(), R.drawable.newgame);
        Bitmap undoAndFlag = BitmapFactory.decodeResource(
                getContext().getResources(), R.drawable.undoredoflag);

        Bitmap undo = null;
        Bitmap flag = null;
        Bitmap[] options = {
                undo, flag
        };

        double bottomBoardPercent = topBoardMarginPercent
                + (cellSizePercent * getWidth() / getHeight()) * rows;
        double bottomMessagePercent = bottomBoardPercent + 0.05;
        double topOptionsPercent = bottomMessagePercent + 0.05;
        double bottomOptionsPercent = topOptionsPercent + 0.05;

        topOptionsY = Math.round(getHeight() * topOptionsPercent);
        bottomOptionsY = Math.round(getHeight() * bottomOptionsPercent);

        // drawing the bg
        canvas.drawRGB(255, 255, 255);

        // drawing the title
        canvas.drawText("MINESWEEPR",
                Math.round(getWidth() * 0.5),
                Math.round(getHeight() * topMarginPercent), paint);

        //drawing the board
        for (Cell[] cellArray : board.getCells()) {
            for (Cell cell : cellArray) {
                cell.draw(canvas);
            }
        }

        // drawing the board's description
        String message;
        if (board.hasWon()) {
            board.revealMines();
            message = "YOU WON!";
        }
        else {
            if (board.gameOver()) {
                board.revealMines();
                message = "YOU LOST!";
            }
            else {
                paint.setTextSize(subtextSize);
                message = "LIFE " + board.getLife() + ", MINE " + board.getRemainingFlags();
            }
        }
        canvas.drawText(message,
                Math.round(getWidth() * 0.5),
                Math.round(getHeight() * bottomMessagePercent), paint);

        // creating + drawing bitmaps
        int imgWidth = undoAndFlag.getWidth();
        int imgHeight = undoAndFlag.getHeight() / 2;

        double leftOptionXPercent = sideMarginPercent;
        double rightOptionXPercent;

        for (int i = 0; i < options.length; i++) {
            rightOptionXPercent = leftOptionXPercent + 0.15;

            leftOptionsXPercents[i] = leftOptionXPercent;
            rightOptionsXPercents[i] = rightOptionXPercent;

            options[i] = Bitmap.createBitmap(undoAndFlag, 0, imgHeight * i, imgWidth, imgHeight);
            canvas.drawBitmap(options[i], null, new RectF(
                    Math.round(getWidth() * leftOptionXPercent), topOptionsY,
                    Math.round(getWidth() * rightOptionXPercent), bottomOptionsY), null);
            leftOptionXPercent = rightOptionXPercent + 0.02;
        }

        // drawing on/off flag switch
        paint.setTextSize(subtextSize);
        paint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText("" + flagSwitch,
                Math.round(getWidth() * rightOptionsXPercents[1]), bottomOptionsY, paint);

        //drawing new game
        leftNewGameX = Math.round(getWidth() * 0.75);
        rightNewGameX = Math.round(getWidth() * (1 - sideMarginPercent));

        canvas.drawBitmap(newGame, null, new RectF(
                leftNewGameX, topOptionsY, rightNewGameX, bottomOptionsY), null);
    }
}
