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

@SuppressLint("AppCompatCustomView")
public class WebImageView extends ImageView {

    public WebImageView(Context context) {
        this(context, null);
    }

    public WebImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageRes(@DrawableRes final int res) {
        super.setImageDrawable(getResources().getDrawable(res));
    }

    public void setImageUrl(String url) {
        setImageUrl(url, null);
    }

    public void setImageUrl(String url, Callback callback) {
        setImageUrl(url, 0, 0, callback);
    }

    public void setImageUrl(String url, int width, int height, Callback callback) {
        try {
            setImageUri(Uri.parse(url), width, height, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageUri(Uri uri, int width, int height, Callback callback) {
        if (null == uri) {
            return;
        }

        RequestCreator requestCreator = Picasso.with(getContext()).load(uri);
        if (width != 0 && height != 0) {
            requestCreator.resize(width, height).centerCrop();
        }
        requestCreator.into(this, callback);
    }

    @Override
    public void onDetachedFromWindow() {
        Picasso.with(getContext()).cancelRequest(this);
        super.onDetachedFromWindow();
    }
}
