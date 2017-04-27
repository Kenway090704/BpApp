package com.bemetoy.bp.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.baidu.platform.comapi.map.A;
import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by Tom on 2016/5/12.
 */
public class KeyBoardUtil {

    private static final String TAG = "Sdk.KeyBoardUtil";

    /**
     * Show the keyboard
     * @param context
     * @param view the view receive the input event.
     */
    public static void show(Context context, View view) {
        if(context == null || view == null) {
            Log.e(TAG, "context or view is null");
            return;
        }

        if(context instanceof Activity) {
            Activity activity = (Activity)context;
            if(activity.isFinishing()) {
                return;
            }
        }

        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Deprecated
    /**
     * the view
     * @param context
     * @param view
     */
    public static void hide(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(view == null || view.getWindowToken() == null) {
            Log.e(TAG, "view or window token is null");
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static void hideSoftKeyboard(View view) {
        if (view == null)
            return;
        View focusView = null;
        if (view instanceof EditText)
            focusView = view;
        Context context = view.getContext();
        if (context != null && context instanceof Activity) {
            Activity activity = ((Activity) context);
            focusView = activity.getCurrentFocus();
            if(activity.isFinishing()) {
                return;
            }
        }

        if (focusView != null) {
            if (focusView.isFocused()) {
                focusView.clearFocus();
            }

            if(focusView.getWindowToken() == null) {
                return;
            }

            InputMethodManager manager = (InputMethodManager) focusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            manager.hideSoftInputFromInputMethod(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
