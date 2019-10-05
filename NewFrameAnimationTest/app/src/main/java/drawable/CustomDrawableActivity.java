package drawable;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newframeanimationtest.R;

public class CustomDrawableActivity extends AppCompatActivity {

    public static void open(Context context){
        Intent intent = new Intent(context, CustomDrawableActivity.class);
        context.startActivity(intent);
    }

    private ImageView mImageView;
    private FrameAnimDrawable mAnimationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        mImageView = findViewById(R.id.image_list);
        initDrawable();

    }

    private void initDrawable(){
        int[] resIds = getRes();
        if (resIds == null || resIds.length == 0) {
            return;
        }

        mAnimationDrawable = new FrameAnimDrawable(24, resIds, getResources(), 1125, 1950);
        mAnimationDrawable.setIsFirstAnimation(true);
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mAnimationDrawable.setParentViewHeight(mImageView.getWidth(), mImageView.getHeight());
                mImageView.setImageDrawable(mAnimationDrawable);
                mAnimationDrawable.start();
            }
        });
    }

    public void startAnim() {
        if (mAnimationDrawable != null) {
            mImageView.post(new Runnable() {
                @Override
                public void run() {
                    mAnimationDrawable.setParentViewHeight(mImageView.getWidth(), mImageView.getHeight());
                    mImageView.setImageDrawable(mAnimationDrawable);

                }
            });
        }
    }

    private int[] getRes() {
        if (getResources() != null) {
            TypedArray typedArray = getResources().obtainTypedArray(R.array.array_list);
            if (typedArray != null) {
                int len = typedArray.length();
                int[] resId = new int[len];
                for (int i = 0; i < len; i++) {
                    resId[i] = typedArray.getResourceId(i, -1);
                }
                typedArray.recycle();
                return resId;
            }
            return null;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
    }

    private void clear(){
        mAnimationDrawable.stop();
        mAnimationDrawable.release();
    }
}
