package com.example.newframeanimationtest;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class NormalFrameActivity extends AppCompatActivity {

    public static void open(Context context){
        Intent intent = new Intent(context, NormalFrameActivity.class);
        context.startActivity(intent);
    }

    private ImageView mImageView;
    private AnimationDrawable mAnimationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);

        mImageView = findViewById(R.id.image_list);
        startAnim();
    }

    private void startAnim() {
        mAnimationDrawable = (AnimationDrawable) mImageView.getDrawable();
        mAnimationDrawable.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
    }

    private void clear(){
        mAnimationDrawable.stop();
        for (int i = 0; i < mAnimationDrawable.getNumberOfFrames(); i++) {
            Drawable frame = mAnimationDrawable.getFrame(i);
            if (frame instanceof BitmapDrawable) {
                ((BitmapDrawable) frame).getBitmap().recycle();
            }
            frame.setCallback(null);
        }
        mAnimationDrawable = null;
    }


}
