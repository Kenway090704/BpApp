package com.bemetoy.stub.network;

import android.content.Intent;
import android.support.annotation.CallSuper;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.account.AccountManager;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.network.auth.NetSceneAlphaLogin;

/**
 * Created by Tom on 2016/4/7.
 * Handle the request callback
 */
public abstract class NetSceneResponseCallback<T> implements ResponseCallBack<T> {

    private static final String TAG = "net.NetSceneResponseCallback";

    @Deprecated
    @Override
    @CallSuper
    public void onResponse(int errType, int errorCodeFromSVR, String errorMsg, IRequest request, T result) {
        Log.i(TAG, "Request id = %s, errType = %s, errorCodeFromSVR = %s, errorMsg = %s", request.getCmdId(), errType, errorCodeFromSVR, errorMsg);
        if(errorCodeFromSVR != Racecar.CommonHeaderErrorCode.Common_Header_Error_Code_Session_Key_Time_Out_VALUE) {
            onNetSceneEnd(); //when the request get the session time out. it will retry again, so it should not call onNetSceneEnd.
        }

        if (errType == ProtocolConstants.ErrorType.NETWORK_SVR_ERROR) {
            onServerConnectIssue(request);
            return;
        }

        if (errType == ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR) {
            onLocalNetworkIssue(request);
            return;
        }

        if (errorCodeFromSVR != Racecar.CommonHeaderErrorCode.Common_Header_Error_Code_OK_VALUE) {
            Log.e(TAG, "errType = %s, errorCodeFromSVR = %s, errorMsg = %s", errType, errorCodeFromSVR, errorMsg);
            if (errorCodeFromSVR == Racecar.CommonHeaderErrorCode.Common_Header_Error_Code_Session_Key_Time_Out_VALUE) {
                OnSessionTimeOut(request);
                return;
            }
        }

        if(result == null) {
            onGetBadResponse(request);
            return;
        }

        onResponse(request, result);
    }

    /**
     * Handle the response.
     *
     * @param request
     * @param result
     */
    public void onResponse(IRequest request, T result) {

    }

    /**
     * Handle the sever connect issue.
     */
    public void onServerConnectIssue(IRequest request) {
        onRequestFailed(request);
    }

    /**
     * Handle the server logic error.
     *
     * @param request the
     */
    public void onGetBadResponse(IRequest request) {
        onRequestFailed(request);
    }


    public void onRequestFailed(IRequest request) {
        //ToastUtil.show(R.string.server_network_error);
    }

    /**
     * Handle the local network issue.
     *
     * @param request the request
     */
    public void onLocalNetworkIssue(IRequest request) {
        ToastUtil.show(R.string.local_network_error);
    }


    //TODO handle the session key time out.
    public void OnSessionTimeOut(IRequest request) {
        Log.i(TAG, "session time out, request cmd is %d", request.getCmdId());
        AccountManager accountManager = AppCore.getCore().getAccountManager();
        accountManager.getAccountInfo().reset();

        NetSceneAlphaLogin netSceneAuth = NetSceneAlphaLogin.buildFromFile();
        if (netSceneAuth == null) {
            int flag = Intent.FLAG_ACTIVITY_CLEAR_TASK;
            flag |= Intent.FLAG_ACTIVITY_NEW_TASK;
            PluginStubBus.doAction(ApplicationContext.getContext(), PluginConstants.Plugin.PLUGIN_NAME_APP,
                    PluginConstants.App.Action.CMD_START_LAUNCHER_UI, flag, null);
            return;
        }
        netSceneAuth.doScene();
        RequestRetryManager.getMgr().addRetryRequest(request);
    }

    /**
     * handle some logic when the request is finished.
     */
    public void onNetSceneEnd() {
        
    }
}
