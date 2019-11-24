package com.myapp.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.myapp.R;

public class GoToActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(GoToActivity.this,BotActivity.class);
                mainIntent.putExtra("selected","pay");
                GoToActivity.this.startActivity(mainIntent);
                GoToActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
