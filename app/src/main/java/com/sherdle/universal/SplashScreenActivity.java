package com.sherdle.universal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;

public class SplashScreenActivity extends AppCompatActivity {
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        progress = (ProgressBar) findViewById(R.id.progress_bar_id);
        Sprite foldingCube = new FoldingCube();
        progress.setIndeterminateDrawable(foldingCube);

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                for (int i=0; i<100;)
                {
                    try
                    {
                        sleep(300);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    progress.setProgress(0);
                    i = i+10;

                }
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            }
        };
        thread.start();

    }
}
