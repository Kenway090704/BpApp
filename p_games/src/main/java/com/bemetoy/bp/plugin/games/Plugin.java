package com.bemetoy.bp.plugin.games;

import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.sdk.core.base.IPluginSubCore;
import com.bemetoy.bp.sdk.core.base.PluginAdapter;

/**
 * Created by Tom on 2016/4/21.
 */
public class Plugin extends PluginAdapter {

    @Override
    public IPluginStub getPluginStub() {
        return new PluginStubGames();
    }

    @Override
    public IPluginSubCore getPluginSubCore() {
        return new PluginStubCoreGames();
    }
}
