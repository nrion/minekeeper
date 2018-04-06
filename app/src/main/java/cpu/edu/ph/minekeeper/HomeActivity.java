package cpu.edu.ph.minekeeper;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);

        Typeface droidbb = Typeface.createFromAsset(getAssets(), "fonts/droidbb.ttf");

        TextView[] textViews = {
                (TextView) findViewById(R.id.title),
                (TextView) findViewById(R.id.beginner),
                (TextView) findViewById(R.id.intermediate),
                (TextView) findViewById(R.id.expert),
                (TextView) findViewById(R.id.help),
                (TextView) findViewById(R.id.creator)
        };

        for (TextView textView : textViews) {
            textView.setTypeface(droidbb);
        }
    }

    public void startBeginner(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("level", 1);
        startActivity(intent);
    }

    public void startIntermediate(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("level", 2);
        startActivity(intent);
    }

    public void startExpert(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("level", 3);
        startActivity(intent);
    }
}
