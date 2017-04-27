package com.bemetoy.bp.sdk.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.bemetoy.bp.sdk.tools.Log;

import java.util.Hashtable;

/**
 * Created by Tom on 2016/5/12.
 *
 * Fixed customer font memory leak issue.
 * see https://code.google.com/p/android/issues/detail?id=9904 for more detail.
 */
public class TypefaceUtil {

    private static final String TAG = "Typefaces";
    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface get(Context c, String assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),
                            assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    Log.e(TAG, "Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }
}
