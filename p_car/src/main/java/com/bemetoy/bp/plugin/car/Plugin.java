package com.bemetoy.bp.plugin.car;

import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.sdk.core.base.PluginAdapter;

/**
 * Created by Tom on 2016/8/29.
 */
public class Plugin extends PluginAdapter {

    @Override
    public IPluginStub getPluginStub() {
        return new PluginStubMyCar();
    }
}
