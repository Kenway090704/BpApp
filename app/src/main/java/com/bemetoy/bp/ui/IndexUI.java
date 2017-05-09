package com.bemetoy.bp.ui;

import android.os.Bundle;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.bp.uikit.BpActivity;
import com.bemetoy.stub.network.auth.NetSceneAlphaLogin;
import com.bemetoy.stub.util.RegionManager;

/**
 * Created by Tom on 2016/5/25.
 */
public class IndexUI extends BpActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从文件中获取帐号和密码: 如果loginAlpha==null,本地没有密码需要登录或者注册
        NetSceneAlphaLogin loginAlpha = NetSceneAlphaLogin.buildFromFile();
        Runnable task = null;
        if (loginAlpha == null) {
            task = new Runnable() {
                @Override
                public void run() {
                    PluginStubBus.doAction(IndexUI.this, PluginConstants.Plugin.PLUGIN_NAME_APP,
                            PluginConstants.App.Action.CMD_START_LAUNCHER_UI, 0, null);
                    finish();
                }
            };
        } else {
            if (AppCore.getCore().getAccountManager().isLogin()) {
                Racecar.AccountInfo accountInfo = AccountLogic.getAccountInfo();
                if (Util.isNullOrNil(accountInfo.getProvince())) {
                    task = new Runnable() {
                        @Override
                        public void run() {
                            PluginStubBus.doAction(IndexUI.this, PluginConstants.Plugin.PLUGIN_NAME_APP,
                                    PluginConstants.App.Action.CMD_START_LOCATION_CHOOSE_UI, 0, null);


                            finish();
                        }
                    };
                    ThreadPool.postToMainThread(task, 2 * 1000);
                    return;
                }
            }

            task = new Runnable() {
                @Override
                public void run() {
                    PluginStubBus.doAction(IndexUI.this, PluginConstants.Plugin.PLUGIN_NAME_APP,
                            PluginConstants.App.Action.CMD_START_HOME_PAGE, 0, null);
                    finish();
                }
            };
        }

        ThreadPool.postToMainThread(task, 2 * 1000);
        RegionManager.getInstance().updateFileFromServer(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_index;
    }

    @Override
    protected void initView() {

    }
}
