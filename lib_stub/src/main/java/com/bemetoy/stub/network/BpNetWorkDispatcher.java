package com.bemetoy.stub.network;

import android.content.Intent;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.network.RequestQueue;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.account.AccountManager;
import com.bemetoy.stub.network.auth.NetSceneAlphaLogin;

/**
 * Created by Tom on 2016/4/9.
 */
public class BpNetWorkDispatcher implements INetWorkDispatcher {

    private static final String TAG = "network.BpNetWorkDispatcher";

    private AccountManager mAccountManager;
    private RequestQueue mRequestQueue;
    private RequestRetryManager mRequestRetryManager;


    public BpNetWorkDispatcher(AccountManager accountManager, RequestQueue requestQueue,
                               RequestRetryManager requestRetryManager) {
        this.mAccountManager = accountManager;
        this.mRequestQueue = requestQueue;
        this.mRequestRetryManager = requestRetryManager;
    }


    @Override
    public synchronized void send(IRequest request) {
        Log.i(TAG, "start send net cmd %s", request.getCmdId());

        switch (request.getCmdId()) {
            case Racecar.CmdId.REGISTER_VALUE:
            case Racecar.CmdId.CHECK_USER_VALUE:
            case Racecar.CmdId.CHECK_CAR_ID_VALUE:
            case Racecar.CmdId.RESET_PASSWD_VALUE:
            case Racecar.CmdId.GET_IMAGE_VERIFY_VALUE:
            case Racecar.CmdId.GET_PHONE_VERIFY_VALUE:
            case Racecar.CmdId.AOFEI_REGISTER_VALUE:
            case Racecar.CmdId.CHECK_VERSION_VALUE:
            case Racecar.CmdId.GET_ADDR_DATA_VALUE:
            case Racecar.CmdId.CHECK_ACCOUNT_VALUE:
                mRequestQueue.addRequest(request);
                break;
            case Racecar.CmdId.AOFEI_LOGIN_VALUE:
                if(!mRequestQueue.hasAuthRequestInQueue()) {
                    mRequestQueue.addRequest(request);
                }
                break;
            default:
                if (mAccountManager.isLogin()) {
                    mRequestQueue.addRequest(request);
                } else {
                    dealWitAutoAuth();
                    /**
                     * APP will do sync after auth, we can ignore sync request.
                     */
                    if(request.getCmdId() != Racecar.CmdId.SYNC_REQUEST_VALUE) {
                        this.mRequestRetryManager.addRetryRequest(request);
                    }
                }
        }
    }

    @Override
    public synchronized void cancel(IRequest request) {
        Log.i(TAG,"request has been canceled, cmdId is %d", request.getCmdId());
        mRequestQueue.cancelRequest(request);
    }


    private void dealWitAutoAuth() {
        NetSceneAlphaLogin netSceneAuth = NetSceneAlphaLogin.buildFromFile();
        if (Util.isNull(netSceneAuth)) {
            Log.w(TAG, "local user data is missing");
            int flag = Intent.FLAG_ACTIVITY_CLEAR_TASK;
            flag |= Intent.FLAG_ACTIVITY_NEW_TASK;
            PluginStubBus.doAction(ApplicationContext.getApplication(), PluginConstants.Plugin.PLUGIN_NAME_APP,
                    PluginConstants.App.Action.CMD_START_LAUNCHER_UI, flag, null);
        } else {
            if (!mRequestQueue.hasAuthRequestInQueue()) {
                mRequestQueue.addRequest(netSceneAuth);
            }
        }
    }

}
