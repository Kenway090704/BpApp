package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;

import com.bemetoy.bp.uikit.R;

/**
 * Created by Tom on 2016/6/23.
 */
class BitmapUtil {


    private static android.support.v4.util.LruCache<String, Bitmap> numberMaps = null;

    static {
        int maxSize = (int)(Runtime.getRuntime().maxMemory() / 12);
        numberMaps = new android.support.v4.util.LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }


    /**
     * Convert the text to the tet drawable.
     * @param context
     * @param key
     * @return
     */
    public static Bitmap getBitmap(Context context, String key) {
        Bitmap bitmap = numberMaps.get(key);
        if(bitmap == null) {
            Bitmap b = BitmapFactory.decodeResource(context.getResources(), getTextDrawable(key));
            numberMaps.put(key, b);
            return b;
        } else {
            return bitmap;
        }
    }


    private static @DrawableRes int getTextDrawable(String text) {
        switch (text) {
            case "1":
                return R.drawable.text_1;
            case "2":
                return R.drawable.text_2;
            case "3":
                return R.drawable.text_3;
            case "4":
                return R.drawable.text_4;
            case "5":
                return R.drawable.text_5;
            case "6":
                return R.drawable.text_6;
            case "7":
                return R.drawable.text_7;
            case "8":
                return R.drawable.text_8;
            case "9":
                return R.drawable.text_9;
            default:
                return R.drawable.text_0;
        }
    }
}
