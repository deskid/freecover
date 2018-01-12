package com.github.deskid.freecover;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FreeCover {
    private Activity mOwnActivity;
    private View mTargetView;
    private RelativeLayout mImageLayout;
    private OverlayCover mOverlayCover;
    private ImageCoverBuilder mImageCoverBuilder;
    @HoleStyle
    private String mStyle;
    private int mRadius;
    private int mBackgroundColor = 0xCC000000;
    private final ViewGroup mDecorView;

    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String FLOAT = "float";
    public static final String TOP_CENTER = "top_center";
    public static final String BOTTOM_CENTER = "bottom_center";

    private WebImageView mWebImageView;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef(value = {LEFT, RIGHT, TOP, BOTTOM, FLOAT, TOP_CENTER, BOTTOM_CENTER})
    public @interface Anchor {
    }

    public static final String CIRCLE = "circle";
    public static final String RECTANGLE = "rectangle";
    public static final String OVAL = "oval";
    public static final String ROUNDRECT = "roundrect";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef(value = {CIRCLE, RECTANGLE, OVAL, ROUNDRECT})
    public @interface HoleStyle {
    }

    private FreeCover(Activity activity, View targetView) {
        mOwnActivity = activity;
        mTargetView = targetView;
        mDecorView = (ViewGroup) activity.getWindow().getDecorView();
    }

    public static FreeCover init(Activity activity, View targetView) {
        return new FreeCover(activity, targetView);
    }

    public FreeCover setImageCover(ImageCoverBuilder imageCoverCreator) {
        mImageCoverBuilder = imageCoverCreator;
        return this;
    }

    public FreeCover setHoleStyle(@HoleStyle String style) {
        mStyle = style;
        return this;
    }

    public FreeCover setRadius(int radius) {
        mRadius = radius;
        return this;
    }

    public FreeCover setBackgroundColor(int color) {
        mBackgroundColor = color;
        return this;
    }

    private void setupOverlayCover() {
        mOverlayCover = new OverlayCover(mOwnActivity, mStyle, mRadius, mBackgroundColor, mTargetView);
        mOverlayCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanUp();
            }
        });
    }

    private void setupImageCover() {
        if (mImageCoverBuilder != null) {
            mImageLayout = new RelativeLayout(mTargetView.getContext());
            mWebImageView = new WebImageView(mTargetView.getContext());
            mWebImageView.setBackgroundColor(Color.TRANSPARENT);
            mWebImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            int w = ViewGroup.LayoutParams.WRAP_CONTENT;
            int h = ViewGroup.LayoutParams.WRAP_CONTENT;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
            mImageLayout.addView(mWebImageView, params);

            if (!TextUtils.isEmpty(mImageCoverBuilder.getImgUrl())) {
                mWebImageView.setImageUrl(mImageCoverBuilder.getImgUrl(), new Callback() {
                    @Override
                    public void onSuccess() {
                        show();
                    }

                    @Override
                    public void onError() {
                    }
                });
            } else {
                mWebImageView.setImageRes(mImageCoverBuilder.getImgRes());
                show();
            }

            ScreenUtils.scaleViewByWidthRatio(mWebImageView, mImageCoverBuilder.getWidth(), mImageCoverBuilder.getHeight());
            RelativeLayout.LayoutParams mImageLayoutParams = (RelativeLayout.LayoutParams) mWebImageView.getLayoutParams();

            int[] pos = new int[2];
            mTargetView.getLocationInWindow(pos);

            switch (mImageCoverBuilder.getAnchor()) {
                case TOP:
                    mImageLayoutParams.leftMargin = pos[0] + (int) (0.5 * (mTargetView.getWidth() - mImageLayoutParams.width)) - mImageCoverBuilder.getLeftOffset() + mImageCoverBuilder.getRightOffset();
                    mImageLayoutParams.topMargin = pos[1] - mImageLayoutParams.height - mImageCoverBuilder.getTopOffset() + mImageCoverBuilder.getBottomOffset();
                    break;
                case TOP_CENTER:
                    mImageLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    mImageLayoutParams.topMargin = pos[1] - mImageLayoutParams.height - mImageCoverBuilder.getTopOffset() + mImageCoverBuilder.getBottomOffset();
                    break;
                case BOTTOM:
                    mImageLayoutParams.leftMargin = pos[0] + (int) (0.5 * (mTargetView.getWidth() - mImageLayoutParams.width)) - mImageCoverBuilder.getLeftOffset() + mImageCoverBuilder.getRightOffset();
                    mImageLayoutParams.topMargin = pos[1] + mTargetView.getHeight() - mImageCoverBuilder.getTopOffset() + mImageCoverBuilder.getBottomOffset();
                    break;
                case BOTTOM_CENTER:
                    mImageLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    mImageLayoutParams.topMargin = pos[1] + mTargetView.getHeight() - mImageCoverBuilder.getTopOffset() + mImageCoverBuilder.getBottomOffset();
                    break;
                case LEFT:
                    mImageLayoutParams.leftMargin = pos[0] - mImageLayoutParams.width - mImageCoverBuilder.getLeftOffset() + mImageCoverBuilder.getRightOffset();
                    mImageLayoutParams.topMargin = pos[1] + (int) (0.5 * (mTargetView.getHeight() - mImageLayoutParams.height)) - mImageCoverBuilder.getTopOffset() + mImageCoverBuilder.getBottomOffset();
                    break;
                case RIGHT:
                    mImageLayoutParams.leftMargin = pos[0] + mTargetView.getWidth() - mImageCoverBuilder.getLeftOffset() + mImageCoverBuilder.getRightOffset();
                    mImageLayoutParams.topMargin = pos[1] + (int) (0.5 * (mTargetView.getHeight() - mImageLayoutParams.height)) - mImageCoverBuilder.getTopOffset() + mImageCoverBuilder.getBottomOffset();
                    break;
                case FLOAT:
                    mImageLayoutParams.leftMargin = pos[0] - mImageLayoutParams.width - mImageCoverBuilder.getLeftOffset() + mImageCoverBuilder.getRightOffset();
                    mImageLayoutParams.topMargin = pos[1] - mImageLayoutParams.height - mImageCoverBuilder.getTopOffset() + mImageCoverBuilder.getBottomOffset();
                    break;
            }

            mWebImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cleanUp();
                    mTargetView.performClick();
                }
            });

        }

    }

    public FreeCover setupView() {
        mTargetView.post(new Runnable() {
            @Override
            public void run() {
                setupOverlayCover();
                setupImageCover();
            }
        });
        return this;
    }

    public void cleanUp() {
        if (mOverlayCover != null) {
            mDecorView.removeView(mOverlayCover);
        }
        if (mImageLayout != null) {
            mDecorView.removeView(mImageLayout);
        }
    }

    public void show() {
        int width, height;
        width = height = FrameLayout.LayoutParams.MATCH_PARENT;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        if (mOverlayCover != null) {
            mDecorView.addView(mOverlayCover, layoutParams);
        }
        if (mImageLayout != null) {
            mDecorView.addView(mImageLayout, layoutParams);
            Animation animation = AnimationUtils.loadAnimation(mOwnActivity, R.anim.center_pop);
            mWebImageView.startAnimation(animation);
        }
    }

    public static class ImageCoverBuilder {
        private String mImgUrl;
        @DrawableRes
        private int mImgRes;
        private int mWidth;
        private int mHeight;

        @Anchor
        private String mAnchor;
        private int mLeftOffset;
        private int mRightOffset;
        private int mTopOffset;
        private int mBottomOffset;

        public ImageCoverBuilder() {
            mImgUrl = "";
            mImgRes = 0;
            mWidth = 0;
            mHeight = 0;
            mAnchor = TOP;
            mLeftOffset = 0;
            mRightOffset = 0;
            mTopOffset = 0;
            mBottomOffset = 0;
        }

        public ImageCoverBuilder setImgUrl(String imgUrl) {
            mImgUrl = imgUrl;
            return this;
        }

        public ImageCoverBuilder setImgRes(@DrawableRes int imgRes) {
            mImgRes = imgRes;
            return this;
        }

        public ImageCoverBuilder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public ImageCoverBuilder setHeight(int height) {
            mHeight = height;
            return this;
        }

        public ImageCoverBuilder setBottomOffset(int bottomOffset) {
            mBottomOffset = bottomOffset;
            return this;
        }

        public ImageCoverBuilder setLeftOffset(int leftOffset) {
            mLeftOffset = leftOffset;
            return this;
        }

        public ImageCoverBuilder setRightOffset(int rightOffset) {
            mRightOffset = rightOffset;
            return this;
        }

        public ImageCoverBuilder setTopOffset(int topOffset) {
            mTopOffset = topOffset;
            return this;
        }

        public ImageCoverBuilder setAnchor(@Anchor String anchor) {
            mAnchor = anchor;
            return this;
        }

        public int getHeight() {
            return mHeight;
        }

        public String getImgUrl() {
            return mImgUrl;
        }

        public int getBottomOffset() {
            return mBottomOffset;
        }

        public int getLeftOffset() {
            return mLeftOffset;
        }

        public int getRightOffset() {
            return mRightOffset;
        }

        public int getTopOffset() {
            return mTopOffset;
        }

        @Anchor
        public String getAnchor() {
            return mAnchor;
        }

        public int getWidth() {
            return mWidth;
        }

        @DrawableRes
        public int getImgRes() {
            return mImgRes;

        }
    }

}
