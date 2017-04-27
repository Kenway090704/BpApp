package com.bemetoy.bp.plugin.friends;

import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.sdk.core.base.IPluginSubCore;
import com.bemetoy.bp.sdk.core.base.PluginAdapter;

/**
 * Created by Tom on 2016/4/27.
 */
public class Plugin extends PluginAdapter {

    @Override
    public IPluginStub getPluginStub() {
        return new PluginStubFriends();
    }

    @Override
    public IPluginSubCore getPluginSubCore() {
        return new PluginStubCoreFriends();
    }
}
