package cpu.edu.ph.minekeeper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class Cell {
    private static final String TAG = Cell.class.getSimpleName();

    private boolean flagged;
    private boolean open;
    private int value;

    private static int width;
    private static int height;

    private int canvasX;
    private int canvasY;

    private boolean touched;

    public Cell(int value) {
        this.value = value;
    }

    public static void setSizeOfAllCells(int length) {
        Cell.width = length;
        Cell.height = length;
    }

    public void move(int x, int y) {
        this.canvasX = x;
        this.canvasY = y;
    }

    public void handleTouch(int eventX, int eventY) {
        touched = false;

        if (eventX >= canvasX && eventX <= canvasX + width) {
            if (eventY >= canvasY && eventY <= canvasY + height) {
                touched = true;
            }
        }
    }

    public boolean isTouched() {
        return touched;
    }

    public int getValue() {
        return value;
    }

    public void setAsMine() {
        value = -1; 
    }

    public boolean isMine() {
        return value == -1;
    }

    public void increaseValue() {
        if (!isMine()) { // if the cell is not a mine
            value++; 
        }
    }
    
    public void flag() {
        flagged = true; 
    }
    
    public void unflag() {
        flagged = false;
    }
    
    public boolean isFlagged() {
        return flagged; 
    }
    
    public void reveal() {
        open = true; 
    }
    
    public void conceal() {
        open = false; 
    }
    
    public boolean isOpen() {
        return open; 
    }

    public void draw(Canvas canvas) {
        Bitmap icons = MainGameView.getIcons();

        int cellWidth = icons.getWidth(); // original width
        int cellHeight = Math.round(icons.getHeight() / 15); // original height
        int sourceY;

        if (!open) {
            if (flagged) {
                sourceY = cellHeight;
            }
            else {
                sourceY = 0;
            }
        }
        else {
            if (isMine() && touched) {
                sourceY = cellHeight * 2;
            }
            else if (isMine() && flagged) {
                sourceY = cellHeight * 3;
            }
            else {
                sourceY = cellHeight * (value + 5);
            }
        }

        Bitmap iconImage = Bitmap.createBitmap(
                icons, 0, sourceY, cellWidth, cellHeight);

        iconImage = Bitmap.createScaledBitmap(
                iconImage, width, height, true);

        canvas.drawBitmap(
                iconImage, canvasX, canvasY, null);
    }
}
