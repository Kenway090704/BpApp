package com.bemetoy.bp.sdk.core;

import com.bemetoy.bp.sdk.core.base.IPlugin;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by albieliang on 16/4/7.
 */
public class PluginCore {

    private static final String TAG = "Core.PluginCore";

    private static PluginCore sCore;

    private Map<String, IPlugin> mPlugins;

    public static PluginCore getCore() {
        if (sCore == null) {
            synchronized (PluginCore.class) {
                if (sCore == null) {
                    sCore = new PluginCore();
                }
            }
        }
        return sCore;
    }

    private PluginCore() {
        mPlugins = new HashMap<>();
    }

    /**
     * Add a new plugin into PluginCore.
     *
     * @param pluginName
     * @param plugin
     * @return
     */
    public boolean addPlugin(String pluginName, IPlugin plugin) {
        if (Util.isNullOrNil(pluginName)) {
            Log.w(TAG, "add plugin failed, plugin name is null or nil.");
            return false;
        }
        if (plugin == null) {
            Log.w(TAG, "add plugin failed, plugin is null.");
            return false;
        }
        IPlugin oldPlugin = mPlugins.put(pluginName, plugin);
        Log.i(TAG, "add plugin(%s) successfully, old plugin is %s.", pluginName, oldPlugin);
        return true;
    }

    /**
     * Remove the plugin from the PluginCore with the given plugin name.
     *
     * @param pluginName
     * @return
     */
    public IPlugin removePlugin(String pluginName) {
        if (Util.isNullOrNil(pluginName)) {
            Log.w(TAG, "remove plugin failed, plugin name is null or nil.");
            return null;
        }
        return mPlugins.remove(pluginName);
    }

    /**
     * Get the plugin object with the given plugin name.
     *
     * @param pluginName
     * @return
     */
    public IPlugin getPlugin(String pluginName) {
        if (Util.isNullOrNil(pluginName)) {
            Log.w(TAG, "get plugin failed, plugin name is null or nil.");
            return null;
        }
        return mPlugins.get(pluginName);
    }
}
