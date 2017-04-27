package com.bemetoy.bp.test.testcase;

import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.test.base.BaseTestCase;

/**
 * Created by albieliang on 16/4/7.
 */
public class TestCase_PluginStubBus implements BaseTestCase {

    private static final String TAG = "Test.TestCase_PluginStubBus";

    @Override
    public void prepare() {

    }

    @Override
    public void doTest() {
        int uid = PluginStubBus.getInt(PluginConstants.Plugin.PLUGIN_NAME_APP, PluginConstants.App.DataKey.GET_USER_ID, 0);
        Log.i(TAG, "doTest PluginStubBus.getInt(uid : %d).", uid);
        PluginStubBus.setInt(PluginConstants.Plugin.PLUGIN_NAME_APP, PluginConstants.App.DataKey.SET_USER_ID, ++uid);
        uid = PluginStubBus.getInt(PluginConstants.Plugin.PLUGIN_NAME_APP, PluginConstants.App.DataKey.GET_USER_ID, 0);
        Log.i(TAG, "doTest PluginStubBus.getInt(uid : %d).", uid);
    }

    @Override
    public void doEnd() {

    }
}
