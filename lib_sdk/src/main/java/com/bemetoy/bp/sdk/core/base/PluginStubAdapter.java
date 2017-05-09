package com.bemetoy.bp.sdk.core.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by albieliang on 16/4/7.
 */
public class PluginStubAdapter implements IPluginStub {

    private static final String TAG = "Core.PluginStubAdapter";

    @Override
    public boolean doAction(Context context, int cmd, Bundle data, int flag, OnActionResult onActionResult) {

        return false;
    }

    @Override
    public int getInt(String key, int def) {
        return def;
    }

    @Override
    public long getLong(String key, long def) {
        return def;
    }

    @Override
    public String getString(String key, String def) {
        return def;
    }

    @Override
    public float getFloat(String key, float def) {
        return def;
    }

    @Override
    public double getDouble(String key, double def) {
        return def;
    }

    @Override
    public Bundle getBundle(String key) {
        return null;
    }

    @Override
    public boolean setInt(String key, int value) {
        return false;
    }

    @Override
    public boolean setLong(String key, long value) {
        return false;
    }

    @Override
    public boolean setString(String key, String value) {
        return false;
    }

    @Override
    public boolean setFloat(String key, float value) {
        return false;
    }

    @Override
    public boolean setDouble(String key, double value) {
        return false;
    }

    @Override
    public boolean setBundle(String key, Bundle data) {
        return false;
    }

    /**
     *
     * @param packageContext
     * @param cls
     * @param data
     * @return
     */
    protected Intent buildIntent(Context packageContext, Class<?> cls, int flag, Bundle data) {
        Intent intent = new Intent(packageContext, cls);
        if (data != null) {
            intent.putExtras(data);
        }
        intent.addFlags(flag);
        return intent;
    }

    /**
     *
     * @param packageContext
     * @param cls
     * @param data
     */
    protected void startActivity(Context packageContext, Class<?> cls, int flag, Bundle data) {
        if (packageContext == null) {
            Log.w(TAG, "package context is null.");
            return;
        }
        packageContext.startActivity(buildIntent(packageContext, cls, flag, data));
    }

    protected void startActivity(Context packageContext, Intent intent) {
        if(intent != null) {
            packageContext.startActivity(intent);
        }
    }

}
