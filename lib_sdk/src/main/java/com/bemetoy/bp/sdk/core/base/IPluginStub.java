package com.bemetoy.bp.sdk.core.base;

import android.content.Context;
import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by albieliang on 16/4/7.
 */
public interface IPluginStub {

    boolean doAction(Context context, int cmd, Bundle data, int flag, OnActionResult onActionResult);

    int getInt(String key, int def);

    long getLong(String key, long def);

    String getString(String key, String def);

    float getFloat(String key, float def);

    double getDouble(String key, double def);

    Bundle getBundle(String key);

    boolean setInt(String key, int value);

    boolean setLong(String key, long value);

    boolean setString(String key, String value);

    boolean setFloat(String key, float value);

    boolean setDouble(String key, double value);

    boolean setBundle(String key, Bundle data);

    interface OnActionResult extends Serializable{
        int ACTION_RESULT_OK = -1;
        int ACTION_RESULT_CANCEL = 0;
        int ACTION_RESULT_ = 1;
        void onActionResult(Context context, int cmd, int resultCode, Bundle data);
    }
}
