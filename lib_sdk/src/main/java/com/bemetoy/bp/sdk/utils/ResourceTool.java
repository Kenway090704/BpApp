package com.bemetoy.bp.sdk.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * Created by Tom on 2016/3/10.
 */
public final class ResourceTool {

    private ResourceTool() {

    }

    /**
     * if res is an xml, use getColorStateList
     *
     * @param context
     * @param resId
     * @return
     */
    public static int getColor(Context context, int resId) {
        return context.getResources().getColor(resId);
    }

    public static ColorStateList getColorStateList(Context context, int resId) {
        return context.getResources().getColorStateList(resId);
    }

    public static Drawable getDrawable(Context context, int resId) {
        return context.getResources().getDrawable(resId);
    }

    public static int getDimensionPixelSize(Context context, int resId) {
        return context.getResources().getDimensionPixelSize(resId);
    }

    public static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Get the char drawble by the char
     * @param context
     * @param c
     * @return
     */
    public static int getCharDrawable(Context context, String c) {
        Resources res = context.getResources();
        final String packageName = context.getPackageName();
        int imageResId = res.getIdentifier(c, "drawable", packageName);
        return imageResId;
    }
}