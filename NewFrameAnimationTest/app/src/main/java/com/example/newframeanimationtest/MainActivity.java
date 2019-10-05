package com.example.newframeanimationtest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import drawable.CustomDrawableActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.normal_frame_animation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NormalFrameActivity.open(MainActivity.this);
            }
        });

        findViewById(R.id.custom_frame_animation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDrawableActivity.open(MainActivity.this);
            }
        });
    }
}
