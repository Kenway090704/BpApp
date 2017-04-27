package com.bemetoy.bp.app;

import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.bemetoy.bp.BuildConfig;
import com.bemetoy.bp.Plugin;
import com.bemetoy.bp.network.RequestQueue;
import com.bemetoy.bp.protocols.EnvirenmentArgsHolder;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.core.PluginCore;
import com.bemetoy.bp.test.TestCenter;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.account.AccountManager;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.db.StorageManager;
import com.bemetoy.stub.network.BpNetWorkDispatcher;
import com.bemetoy.stub.network.RequestRetryManager;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by albieliang on 16/3/6.
 */
public class BpApplication extends MultiDexApplication {

    private static final String TAG = "App.BpApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext.setApplication(this);
        EnvirenmentArgsHolder.setContext(this);

        CrashReport.initCrashReport(getApplicationContext());

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

        AppCore.getCore().addAccountStateListener(new AccountManager.AccountStateListener() {
            @Override
            public void onReady(AccountManager accMgr) {
                Log.i(TAG, "on account ready.");
            }

            @Override
            public void onInitDatabase(AccountManager accMgr) {
                Log.i(TAG, "on initialize database.");
                StorageManager.getMgr().initialize(accMgr);
            }

            @Override
            public void onReset(AccountManager accMgr) {
                Log.i(TAG, "on reset account.");
                StorageManager.getMgr().reset(accMgr);
            }
        });

        AppCore.getCore().setOnAppCoreInitListener(new AppCore.OnAppCoreInitListener() {
            @Override
            public void beforeInit() {
                // Optimize later
                PluginCore.getCore().addPlugin(PluginConstants.Plugin.PLUGIN_NAME_APP, new Plugin());
                PluginCore.getCore().addPlugin(PluginConstants.Plugin.PLUGIN_NAME_P_PERSONAL_CENTER, new com.bemetoy.bp.plugin.personalcenter.Plugin());
                PluginCore.getCore().addPlugin(PluginConstants.Plugin.PLUGIN_NAME_P_RANKING, new com.bemetoy.bp.plugin.ranking.Plugin());
                PluginCore.getCore().addPlugin(PluginConstants.Plugin.PLUGIN_NAME_P_GAMES, new com.bemetoy.bp.plugin.games.Plugin());
                PluginCore.getCore().addPlugin(PluginConstants.Plugin.PLUGIN_NAME_P_MALL, new com.bemetoy.bp.plugin.mall.Plugin());
                PluginCore.getCore().addPlugin(PluginConstants.Plugin.PLUGIN_NAME_P_FRIENDS, new com.bemetoy.bp.plugin.friends.Plugin());
                PluginCore.getCore().addPlugin(PluginConstants.Plugin.PLUGIN_NAME_P_TASKS_CENTER, new com.bemetoy.bp.plugin.tasks.Plugin());
                PluginCore.getCore().addPlugin(PluginConstants.Plugin.PLUGIN_NAME_P_NOTICE, new com.bemetoy.bp.plugin.notice.Plugin());
                PluginCore.getCore().addPlugin(PluginConstants.Plugin.PLUGIN_NAME_P_OPERATIONS, new com.bemetoy.bp.plugin.operatoins.Plugin());
                PluginCore.getCore().addPlugin(PluginConstants.Plugin.PLUGIN_NAME_P_MY_CAR, new com.bemetoy.bp.plugin.car.Plugin());
            }

            @Override
            public void afterInit() {

            }
        });
        // Optimize
        AppCore.getCore().initialize();

        AccountManager accountManager = AppCore.getCore().getAccountManager();
        RequestQueue requestQueue = AppCore.getCore().getRequestQueue();

        AppCore.getCore().setDispatcher(new BpNetWorkDispatcher(accountManager,requestQueue,
                RequestRetryManager.getMgr()));

        // For debug mode test.
        TestCenter.getImpl().initialize();
//        TestCenter.getImpl().start();
        ToastUtil.init(this);

        //for BaiduMap SDK.
        SDKInitializer.initialize(ApplicationContext.getApplication());
    }
}
