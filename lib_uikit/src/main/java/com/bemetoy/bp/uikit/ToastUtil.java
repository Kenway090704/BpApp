package com.bemetoy.bp.uikit;

import android.content.Context;
import android.widget.Toast;

import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by Tom on 2016/4/13.
 */
public class ToastUtil {

    private static final String TAG = "widget.ToastUtil";

    private static Toast mToast;

    public static void init(Context context) {
        mToast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
    }

    /**
     * Show the toast to the user.
     * @param strId
     */
    public static void show(int strId) {
        show(strId, Toast.LENGTH_SHORT);
    }

    public static void show(String txt) {
        if (mToast == null) {
            Log.e(TAG, "ToastUtil has not been init.");
            throw new IllegalStateException("ToastUtil has not been init.");
        }
        mToast.setText(txt);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * Show the toast to the user.
     * @param strId
     * @param duration
     */
    public static void show(int strId, int duration) {
        if (mToast == null) {
           Log.e(TAG, "ToastUtil has not been init.");
            throw new IllegalStateException("ToastUtil has not been init.");
        }
        mToast.setText(strId);
        mToast.setDuration(duration);
        mToast.show();
    }
}
