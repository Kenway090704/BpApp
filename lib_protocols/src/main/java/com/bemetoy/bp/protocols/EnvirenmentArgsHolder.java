package com.bemetoy.bp.protocols;

import android.content.Context;

/**
 * Created by albieliang on 16/4/1.
 */
public class EnvirenmentArgsHolder {

    private static Context sContext;

    public static void setContext(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }
}
