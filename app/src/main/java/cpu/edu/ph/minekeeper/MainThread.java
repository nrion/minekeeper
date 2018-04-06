package cpu.edu.ph.minekeeper;

import android.graphics.Canvas;
import android.provider.Settings;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private SurfaceHolder holder;
    private MainGameView gameView;
    private boolean running;

    public MainThread(SurfaceHolder holder, MainGameView gameView) {
        super();
        this.holder = holder;
        this.gameView = gameView;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void run() {
        Canvas canvas = null;
        while (running) {
            try {
                canvas = holder.lockCanvas();
                synchronized (holder) {
                    gameView.draw(canvas);
                }
            }
            finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
