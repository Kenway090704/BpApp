package com.bemetoy.bp.sdk.core.base;

/**
 * Created by albieliang on 16/4/7.
 */
public class PluginAdapter implements IPlugin {

    private IPluginSubCore mPluginSubCore;
    private IPluginStub mPluginStub;

    public PluginAdapter() {
    }

    public PluginAdapter(IPluginStub pluginStub) {
        mPluginStub = pluginStub;
    }

    public PluginAdapter(IPluginSubCore pluginSubCore) {
        mPluginSubCore = pluginSubCore;
    }

    public PluginAdapter(IPluginSubCore pluginSubCore, IPluginStub pluginStub) {
        mPluginSubCore = pluginSubCore;
        mPluginStub = pluginStub;
    }

    @Override
    public IPluginSubCore getPluginSubCore() {
        return mPluginSubCore;
    }

    @Override
    public IPluginStub getPluginStub() {
        return mPluginStub;
    }
}
