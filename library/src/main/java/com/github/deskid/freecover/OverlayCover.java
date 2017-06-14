package com.github.deskid.freecover;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import static com.github.deskid.freecover.FreeCover.CIRCLE;
import static com.github.deskid.freecover.FreeCover.OVAL;
import static com.github.deskid.freecover.FreeCover.RECTANGLE;
import static com.github.deskid.freecover.FreeCover.ROUNDRECT;

public class OverlayCover extends View {
    private static final Property<OverlayCover, Float> PROGRESS_PROPERTY =
            new Property<OverlayCover, Float>(Float.class, "progressProperty") {
                @Override
                public Float get(OverlayCover object) {
                    return object.getAnimationProgress();
                }

                @Override
                public void set(OverlayCover object, Float value) {
                    object.setAnimationProgress(value);
                }
            };

    private Paint mPaint;
    private Bitmap mEraserBitmap;
    private Canvas mEraserCanvas;
    private View mTargetView;
    @FreeCover.HoleStyle
    private String mStyle = CIRCLE;
    private int mBackgroundColor;
    private GestureDetector mDetector;
    private int[] mPosition = new int[2];
    private Rect mTargetViewDrawingRect = new Rect();
    private int mRadius = 0; // drawCircle,drawRoundRect
    private Rect mRect = new Rect();      // drawRect
    private RectF mRectF = new RectF();    // drawRoundRect,drawOval
    private float mAnimationProgress;

    private OverlayCover(final Context context) {
        super(context);
    }

    private OverlayCover(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlayCover(Context context, @FreeCover.HoleStyle String style, int backgroundColor, View targetView) {
        this(context, style, 0, backgroundColor, targetView);
    }

    public OverlayCover(Context context, @FreeCover.HoleStyle String style, int radius, int backgroundColor, View targetView) {
        super(context);
        mTargetView = targetView;
        mStyle = style;
        mRadius = radius;
        mBackgroundColor = backgroundColor;
        init();
    }

    private void init() {
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        mEraserBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
        //Although ALPHA_8 consumes less memory and more efficient
        //in some device it may turn out black and lose transparent
//        mEraserBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ALPHA_8);
        mEraserCanvas = new Canvas(mEraserBitmap);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xFFFFFFFF);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mTargetView.getLocationOnScreen(mPosition);
        mTargetView.getDrawingRect(mTargetViewDrawingRect);
        mTargetViewDrawingRect.offset(mPosition[0], mPosition[1]);

        mDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(final MotionEvent e) {
                int x = (int) e.getRawX();
                int y = (int) e.getRawY();
                if (mTargetViewDrawingRect.contains(x, y)) {
                    mTargetView.performClick();
                    return true;
                }
                return false;
            }
        });

        int centerX = mPosition[0] + mTargetView.getWidth() / 2;
        int centerY = mPosition[1] + mTargetView.getHeight() / 2;

        switch (mStyle) {
            case CIRCLE:
                mRadius = 0;
                break;
            case RECTANGLE:
                mRect.left = centerX;
                mRect.right = centerX;
                mRect.top = centerY;
                mRect.bottom = centerY;
                break;
            case OVAL:
                mRectF.left = centerX;
                mRectF.right = centerX;
                mRectF.top = centerY;
                mRectF.bottom = centerY;
                break;
            case ROUNDRECT:
                if (mRadius == 0) {
                    if (mTargetView.getHeight() > mTargetView.getWidth()) {
                        mRadius = mTargetView.getWidth() / 2;
                    } else {
                        mRadius = mTargetView.getHeight() / 2;
                    }
                }
                mRectF.left = centerX;
                mRectF.right = centerX;
                mRectF.top = centerY;
                mRectF.bottom = centerY;
                break;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAnimationProgress = 0;
        ObjectAnimator progrssAnimator = ObjectAnimator.ofFloat(this, PROGRESS_PROPERTY, 0.0f, 1);
        progrssAnimator.setInterpolator(new OvershootInterpolator());
        progrssAnimator.setDuration(200);
        progrssAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mEraserCanvas.setBitmap(null);
        if (mEraserBitmap != null) {
            mEraserBitmap.recycle();
        }
        mEraserBitmap = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mEraserBitmap.eraseColor(Color.TRANSPARENT);
        mEraserCanvas.drawColor(mBackgroundColor);

        computeDrawRectWhenNeeded();

        switch (mStyle) {
            case CIRCLE:
                if (mTargetView.getHeight() > mTargetView.getWidth()) {
                    mRadius = (int) ((mAnimationProgress * 0.5 * mTargetView.getHeight()));
                } else {
                    mRadius = (int) (mAnimationProgress * 0.5 * mTargetView.getWidth());
                }
                mEraserCanvas.drawCircle(mPosition[0] + mTargetView.getWidth() / 2,
                        mPosition[1] + mTargetView.getHeight() / 2, mRadius, mPaint);
                break;
            case RECTANGLE:
                mEraserCanvas.drawRect(mRect, mPaint);
                break;
            case OVAL:
                mEraserCanvas.drawOval(mRectF, mPaint);
                break;
            case ROUNDRECT:
                mEraserCanvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
                break;
        }
        canvas.drawBitmap(mEraserBitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public float getAnimationProgress() {
        return mAnimationProgress;
    }

    public void setAnimationProgress(final float progressInSection) {
        mAnimationProgress = progressInSection;
        invalidate();
    }

    public void computeDrawRectWhenNeeded() {
        if (mStyle.equals(CIRCLE)) {
            return;
        }
        mRect.left = mPosition[0] + (int) ((1 - mAnimationProgress) * 0.5 * mTargetView.getWidth());
        mRect.top = mPosition[1] + (int) ((1 - mAnimationProgress) * 0.5 * mTargetView.getHeight());
        mRect.right = mPosition[0] + (int) ((1 + mAnimationProgress) * 0.5 * mTargetView.getWidth());
        mRect.bottom = mPosition[1] + (int) ((1 + mAnimationProgress) * 0.5 * mTargetView.getHeight());

        mRectF.left = mRect.left;
        mRectF.top = mRect.top;
        mRectF.right = mRect.right;
        mRectF.bottom = mRect.bottom;
    }
}
