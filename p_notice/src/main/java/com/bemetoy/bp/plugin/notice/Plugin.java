package com.bemetoy.bp.plugin.notice;

import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.sdk.core.base.IPluginSubCore;
import com.bemetoy.bp.sdk.core.base.PluginAdapter;

/**
 * Created by albieliang on 16/5/13.
 */
public class Plugin extends PluginAdapter {

    @Override
    public IPluginSubCore getPluginSubCore() {
        return new PluginSubCoreNotice();
    }

    @Override
    public IPluginStub getPluginStub() {
        return new PluginStubNotice();
    }
}
