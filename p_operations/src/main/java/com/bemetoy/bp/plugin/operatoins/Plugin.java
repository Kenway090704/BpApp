package com.bemetoy.bp.plugin.operatoins;

import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.sdk.core.base.IPluginSubCore;
import com.bemetoy.bp.sdk.core.base.PluginAdapter;

/**
 * Created by Tom on 2016/4/22.
 */
public class Plugin extends PluginAdapter {

    @Override
    public IPluginStub getPluginStub() {
        return new PluginStubOperations();
    }

    @Override
    public IPluginSubCore getPluginSubCore() {
        return new PluginSubCoreOperations();
    }
}
