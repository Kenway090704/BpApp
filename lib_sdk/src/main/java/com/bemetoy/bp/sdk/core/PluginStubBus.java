package com.bemetoy.bp.sdk.core;

import android.content.Context;
import android.os.Bundle;

import com.bemetoy.bp.sdk.core.base.IPlugin;
import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.core.base.IPluginStub.OnActionResult;

import junit.framework.Assert;

/**
 * Created by albieliang on 16/4/7.
 */
public class PluginStubBus {

    private static final String TAG = "Core.PluginStubBus";

    /**
     *
     * @param context
     * @param pluginName
     * @param cmd
     * @param data
     * @return
     */
    public static boolean doAction(final Context context, final String pluginName, final int cmd, int flag, final Bundle data) {
        Log.i(TAG, "doAction, pluginName = %s, cmd = %s", pluginName, pluginName, cmd);
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "doAction failed, pluginStub null");
            return false;
        }
        return pluginStub.doAction(context, cmd, data, flag, null);
    }




    /**
     *
     * @param context
     * @param pluginName
     * @param cmd
     * @param data
     * @param onActionResult
     * @return
     */
    public static boolean doActionForResult(final Context context, final String pluginName, final int cmd, final Bundle data, int flag, OnActionResult onActionResult) {
        Log.i(TAG, "doActionForResult, pluginName = %s, cmd = %s", pluginName, pluginName, cmd);
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "doActionForResult failed, pluginStub null");
            return false;
        }
        return pluginStub.doAction(context, cmd, data, flag, onActionResult);
    }

    /**
     *
     * @param pluginName
     * @return
     */
    private static IPluginStub getPluginStub(final String pluginName) {
        Assert.assertNotNull("pluginName must not be null", pluginName);
        IPlugin plugin = PluginCore.getCore().getPlugin(pluginName);
        if (plugin == null) {
            Log.w(TAG, "getPluginStub return null, plugin(%s) do not exist.", pluginName);
            return null;
        }
        return plugin.getPluginStub();
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param def
     * @return
     */
    public static int getInt(final String pluginName, final String key, final int def) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "getInt, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return def;
        }
        return pluginStub.getInt(key, def);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @return
     */
    public static Bundle getBundle(final String pluginName, final String key) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "getBundle, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return null;
        }
        return pluginStub.getBundle(key);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param def
     * @return
     */
    public static String getString(final String pluginName, final String key, final String def) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "getString, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return def;
        }
        return pluginStub.getString(key, def);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param def
     * @return
     */
    public static float getFloat(final String pluginName, final String key, final float def) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "getFloat, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return def;
        }
        return pluginStub.getFloat(key, def);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param def
     * @return
     */
    public static double getDouble(final String pluginName, final String key, final double def) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "getDouble, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return def;
        }
        return pluginStub.getDouble(key, def);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param def
     * @return
     */
    public static long getLong(final String pluginName, final String key, final long def) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "getLong, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return def;
        }
        return pluginStub.getLong(key, def);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param value
     * @return
     */
    public static boolean setInt(final String pluginName, final String key, final int value) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "setInt, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return false;
        }
        return pluginStub.setInt(key, value);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param data
     * @return
     */
    public static boolean setBundle(final String pluginName, final String key, final Bundle data) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "setBundle, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return false;
        }
        return pluginStub.setBundle(key, data);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param value
     * @return
     */
    public static boolean setString(final String pluginName, final String key, final String value) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "setString, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return false;
        }
        return pluginStub.setString(key, value);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param value
     * @return
     */
    public static boolean setFloat(final String pluginName, final String key, final float value) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "setFloat, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return false;
        }
        return pluginStub.setFloat(key, value);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param value
     * @return
     */
    public static boolean setDouble(final String pluginName, final String key, final double value) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "setDouble, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return false;
        }
        return pluginStub.setDouble(key, value);
    }

    /**
     *
     * @param pluginName
     * @param key
     * @param value
     * @return
     */
    public static boolean setLong(final String pluginName, final String key, final long value) {
        IPluginStub pluginStub = getPluginStub(pluginName);
        if (pluginStub == null) {
            Log.w(TAG, "setLong, pluginStub null, pluginName = %s, key = %s", pluginName, key);
            return false;
        }
        return pluginStub.setLong(key, value);
    }

}
