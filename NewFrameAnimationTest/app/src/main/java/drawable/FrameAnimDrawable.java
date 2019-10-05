package drawable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FrameAnimDrawable extends Drawable implements Animatable {

    private static final int DEFAULT_FPS = 25;
    private int fps = DEFAULT_FPS;
    private final Paint mPaint;
    private final
    @DrawableRes
    int[] RES_IDS;
    private int resIndex;

    private final Resources mResources;

    private ValueAnimator mAnimator;

    private int mBaseFrameHeight = 0;
    private int mBaseFrameWidth = 0;
    private int mParentViewHeight = 0;
    private int mParentViewWidth = 0;

    private int mInDensity = 0;
    private int mInTargetDensity = 0;

    private boolean mIsFirstAnimation = false;

    public FrameAnimDrawable(@NonNull int[] RES_IDS, @NonNull Resources resources,
                             int baseFrameWidth, int baseFrameHeight) {
        this(DEFAULT_FPS, RES_IDS, resources, baseFrameHeight, baseFrameWidth);
    }

    public FrameAnimDrawable(int fps, @NonNull int[] RES_IDS, @NonNull Resources resources,
                             int baseFrameWidth, int baseFrameHeight) {
        this.fps = fps;
        this.RES_IDS = RES_IDS;
        this.mResources = resources;
        this.mBaseFrameWidth = baseFrameWidth;
        this.mBaseFrameHeight = baseFrameHeight;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);

        if (RES_IDS.length <= 0) {
            throw new RuntimeException(" FrameAnimDrawable RES_IDS can not empty !!!");
        }

        createAnimator();
    }

    private void createAnimator() {
        mAnimator = ValueAnimator.ofInt(0, RES_IDS.length - 1);
        mAnimator.setDuration((long) (1.0f * RES_IDS.length / (fps - 1) * 1000));

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int index = ((int) animation.getAnimatedValue());
                if (resIndex != index) {
                    invalidate(index);
                }
            }
        });

        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (mFrameAnimDrawableListenter != null) {
                    mFrameAnimDrawableListenter.onStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mFrameAnimDrawableListenter != null) {
                    mFrameAnimDrawableListenter.onStop();
                }
                if (mAnimator != null) {
                    mAnimator.removeAllUpdateListeners();
                    mAnimator.removeAllListeners();
                }
            }
        });
    }

    public void invalidate(int index) {
        this.resIndex = index;
        invalidateSelf();
    }

    public int getFrameCount() {
        return RES_IDS.length;
    }

    private Bitmap mLastBitmap;

    @Override
    public void draw(@NonNull Canvas canvas) {
        try {
            if (mResources != null && mParentViewHeight != 0 && mParentViewWidth != 0) {
                if (mLastBitmap == null) {
                    mLastBitmap = Bitmap.createBitmap(mParentViewWidth, mParentViewHeight, Bitmap.Config.ARGB_4444);
                }
                int curFrame = resIndex;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(mResources, RES_IDS[curFrame], options);

                options.inScaled = true;
                options.inDensity = mInDensity;
                options.inTargetDensity = mInTargetDensity;
                options.inJustDecodeBounds = false;
                options.inMutable = true;

                Bitmap bitmap = BitmapFactory.decodeResource(mResources, RES_IDS[curFrame], options);

                int canvasWidth = canvas.getWidth();

                int bitMapHeight = bitmap.getHeight();
                int bitMapWidth = bitmap.getWidth();

                if (mIsFirstAnimation && curFrame == RES_IDS.length - 1) {
                    canvas.drawBitmap(bitmap, canvasWidth / 2 - bitMapWidth / 2,
                            mParentViewHeight / 2 - bitMapHeight / 2, mPaint);
                } else {
                    canvas.drawBitmap(bitmap, canvasWidth / 2 - bitMapWidth / 2,
                            mParentViewHeight - bitMapHeight, mPaint);
                }

                if (RES_IDS != null && curFrame == RES_IDS.length - 1 && mIsFirstAnimation) {
                    if (mFrameAnimDrawableListenter != null) {
                        mFrameAnimDrawableListenter.onStop();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void start() {
        // If the animators has not ended, do nothing.
        if (mAnimator.isStarted()) {
            return;
        }
        startAnimator();
        invalidateSelf();
    }

    private void startAnimator() {
        mAnimator.start();
    }

    @Override
    public void stop() {
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
    }

    @Override
    public boolean isRunning() {
        if (mAnimator != null) {
            return mAnimator.isRunning();
        }
        return false;
    }

    @Override
    public int getIntrinsicWidth() {
        return mParentViewWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mParentViewHeight;
    }

    public void release() {
        if (mLastBitmap != null) {
            mLastBitmap.recycle();
        }
    }

    public void setParentViewHeight(int parentViewWidth, int parentViewHeight) {
        this.mParentViewWidth = parentViewWidth;
        this.mParentViewHeight = parentViewHeight;

        int targetDensity = mResources.getDisplayMetrics().densityDpi;
        float xSScale = ((float) mBaseFrameWidth) / ((float) mParentViewWidth);
        float ySScale = ((float) mBaseFrameHeight) / ((float) mParentViewHeight);

        float startScale = xSScale > ySScale ? xSScale : ySScale;

        mInDensity = (int) (targetDensity * startScale);
        mInTargetDensity = targetDensity;
    }

    public interface FrameAnimDrawableListenter {
        void onStart();

        void onStop();
    }

    private FrameAnimDrawableListenter mFrameAnimDrawableListenter;

    public void setFrameAnimDrawableListenter(FrameAnimDrawableListenter frameAnimDrawableListenter) {
        this.mFrameAnimDrawableListenter = frameAnimDrawableListenter;
    }

    public void setIsFirstAnimation(boolean isFirstAnimation) {
        this.mIsFirstAnimation = isFirstAnimation;
    }

}