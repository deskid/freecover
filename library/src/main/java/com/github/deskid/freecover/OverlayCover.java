package com.github.deskid.freecover;

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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import static com.github.deskid.freecover.FreeCover.CIRCLE;
import static com.github.deskid.freecover.FreeCover.OVAL;
import static com.github.deskid.freecover.FreeCover.RECTANGLE;
import static com.github.deskid.freecover.FreeCover.ROUNDRECT;

public class OverlayCover extends View {
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
    private Rect mRect;      // drawRect
    private RectF mRectF;    // drawRoundRect,drawOval

    private OverlayCover(final Context context) {
        super(context);
    }

    private OverlayCover(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    private OverlayCover(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        int screenWidth = DisplayUtils.getScreenWidth();
        int screenHeight = DisplayUtils.getScreenHeight();
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

        switch (mStyle) {
            case CIRCLE:
                if (mRadius == 0) {
                    if (mTargetView.getHeight() > mTargetView.getWidth()) {
                        mRadius = mTargetView.getHeight() / 2;
                    } else {
                        mRadius = mTargetView.getWidth() / 2;
                    }
                }
                break;
            case RECTANGLE:
                mRect = new Rect(mPosition[0], mPosition[1],
                        mPosition[0] + mTargetView.getWidth(),
                        mPosition[1] + mTargetView.getHeight());
                break;
            case OVAL:
                mRectF = new RectF(mPosition[0], mPosition[1],
                        mPosition[0] + mTargetView.getWidth(),
                        mPosition[1] + mTargetView.getHeight());
                break;
            case ROUNDRECT:
                if (mRadius == 0) {
                    if (mTargetView.getHeight() > mTargetView.getWidth()) {
                        mRadius = mTargetView.getWidth() / 2;
                    } else {
                        mRadius = mTargetView.getHeight() / 2;
                    }
                }
                mRectF = new RectF(mPosition[0], mPosition[1],
                        mPosition[0] + mTargetView.getWidth(),
                        mPosition[1] + mTargetView.getHeight());
                break;
        }
    }

    protected void cleanUp() {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
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

        switch (mStyle) {
            case CIRCLE:
                mEraserCanvas.drawCircle(mPosition[0] + mTargetView.getWidth() / 2, mPosition[1] + mTargetView.getHeight() / 2, mRadius, mPaint);
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
}
