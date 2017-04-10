package com.github.deskid.freecover;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Created by zhou on 3/24/17.
 */

@SuppressLint("AppCompatCustomView")
public class WebImageView extends ImageView {
    private Uri mUri;

    boolean mNeedResize;
    private int mWidth = 0;
    private int mHeight = 0;
    private boolean isWidthFixMode = false;

    Callback mCallback;

    public WebImageView(Context context) {
        this(context, null);
    }

    public WebImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageRes(@DrawableRes final int res) {
        setImageRes(res, null);
    }

    public void setImageRes(@DrawableRes final int res, final Callback callback) {
        super.setImageDrawable(getResources().getDrawable(res));
        if (callback != null) {
            callback.onSuccess();
        }
    }

    public void setImageUrl(String url) {
        setImageUrl(url, null);
    }

    public void setImageUrl(String url, Callback callback) {
        try {
            setImageUri(Uri.parse(url), callback, false, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageUri(Uri uri, Callback callback, boolean needResize, int width, int height) {
        mNeedResize = needResize;
        mWidth = width;
        mHeight = height;
        mCallback = callback;

        if (null == uri) {
            return;
        }

        mUri = uri;
        RequestCreator requestCreator = Picasso.with(getContext()).load(mUri);
        if (needResize) {
            if (width != 0 && height != 0) {
                requestCreator.resize(width, height).centerCrop();
            }
        }
        requestCreator.into(this, callback);
    }

    @Override
    public void onDetachedFromWindow() {
        Picasso.with(getContext()).cancelRequest(this);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isWidthFixMode) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (mWidth > 0) {
                height = width * mHeight / mWidth;
            }
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
