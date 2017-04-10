package com.github.deskid.freecover;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class DisplayUtils {

    private DisplayUtils() {
    }

    //750 offer by our iOS ui sketch
    private static final int SCREEN_WIDTH_OF_UI_DESIGN = 750;

    private static DisplayMetrics sDM = Resources.getSystem().getDisplayMetrics();

    public static int getScreenWidth() {
        return sDM.widthPixels;
    }

    public static int getScreenHeight() {
        return sDM.heightPixels;
    }

    public static float getDensity() {
        return sDM.density;
    }

    public static int dpToPx(int dp) {
        return dpToPx((float) dp);
    }

    public static int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, sDM);
    }

    public static int spToPx(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, sDM);
    }

    public static int pxToDp(int px) {
        return Math.round(px / getDensity());
    }

    /**
     * 根据设计尺寸，在不同分辨率的设备上等比缩放宽和高,这里以宽为缩放标准
     *
     * @param view                  被缩放的view
     * @param widthInDesign         设计稿view宽
     * @param heightInDesign        设计稿view高
     * @param screenWidthInUIDesign 设计稿屏幕宽
     */
    public static void scaleViewByWidthRatio(View view, final int widthInDesign, final int heightInDesign, final int screenWidthInUIDesign) {
        int scaledWidth = getScaledWidthInDevice(widthInDesign, screenWidthInUIDesign);
        view.getLayoutParams().width = scaledWidth;
        view.getLayoutParams().height = Math.min(scaledWidth * heightInDesign / widthInDesign, getScreenHeight());
    }

    public static void scaleViewByWidthRatio(View view, final int widthInDesign, final int heightInDesign) {
        scaleViewByWidthRatio(view, widthInDesign, heightInDesign, SCREEN_WIDTH_OF_UI_DESIGN);
    }

    /**
     * 将UI 设计稿中的宽度根据屏幕宽度进行等比缩放
     *
     * @param widthInUIDesign       设计稿中的宽度
     * @param screenWidthInUIDesign 设计稿使用的屏幕宽度
     * @return 缩放后的宽度
     */
    public static int getScaledWidthInDevice(final int widthInUIDesign, final int screenWidthInUIDesign) {
        int screenWidth = DisplayUtils.getScreenWidth();
        return screenWidth * widthInUIDesign / screenWidthInUIDesign;
    }

    public static int getScaledWidthInDevice(final int widthInUIDesign) {
        return getScaledWidthInDevice(widthInUIDesign, SCREEN_WIDTH_OF_UI_DESIGN);
    }

}
