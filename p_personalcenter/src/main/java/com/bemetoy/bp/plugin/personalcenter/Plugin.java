package com.bemetoy.bp.plugin.personalcenter;

import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.sdk.core.base.IPluginSubCore;
import com.bemetoy.bp.sdk.core.base.PluginAdapter;

/**
 * Created by albieliang on 16/4/16.
 */
public class Plugin extends PluginAdapter {

    @Override
    public IPluginSubCore getPluginSubCore() {
        return new PluginSubCorePersionalCenter();
    }

    @Override
    public IPluginStub getPluginStub() {
        return new PluginStubPersionalCenter();
    }
}
