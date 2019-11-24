package com.myapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.myapp.R;

public class SelectActivity extends AppCompatActivity {

    private LinearLayout buyButton, payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        Initilize();
    }

    private void Initilize(){
        buyButton = findViewById(R.id.buyButton);
        payButton = findViewById(R.id.payButton);

        buyButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BotActivity.class);
                intent.putExtra("selected","buy");
                startActivity(intent);
            }
        });

        payButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BotActivity.class);
                intent.putExtra("selected","pay");
                startActivity(intent);
            }
        });

    }
}
