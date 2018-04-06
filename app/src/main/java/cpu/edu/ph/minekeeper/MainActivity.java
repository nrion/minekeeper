package cpu.edu.ph.minekeeper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {
    private MainGameView mainGameView;
    private int level;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        level = intent.getIntExtra("level", 0);

        this.mainGameView = new MainGameView(this, level);
        setContentView(mainGameView);
    }

    public void restartGame() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("level", level);
        finish();
        startActivity(intent);
    }

    protected void onPause() {
        super.onPause();
        mainGameView.stopGame();
    }

    protected void onResume() {
        super.onResume();
        mainGameView.startGame();
    }
}

