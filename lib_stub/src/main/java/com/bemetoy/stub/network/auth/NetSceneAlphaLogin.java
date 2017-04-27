package com.bemetoy.stub.network.auth;

import android.content.Intent;

import com.bemetoy.bp.autogen.events.AuthResultEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.account.AccountManager;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.network.NetSceneBase;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.network.RequestRetryManager;
import com.bemetoy.stub.network.sync.SyncManager;
import com.google.protobuf.ByteString;

import java.io.File;

/**
 * Created by Tom on 2016/5/20.
 */
public class NetSceneAlphaLogin extends NetSceneBase {

    private static final String TAG = "Stub.NetSceneAlphaLogin";

    private String mUsername;
    private String mPassword;
    private byte[] mTempKey;
    private String mPhoneCode;
    private boolean isAutoAuth = false;

    /**
     * @param username
     * @param password
     * @param phoneCode is used for quickly login.
     */
    public NetSceneAlphaLogin(String username, String password, String phoneCode) {
        super(Racecar.CmdId.AOFEI_LOGIN_VALUE, new AlphaAuthResponseCallBack());
        this.mUsername = username;
        this.mPassword = password;
        this.mTempKey = Util.geneAESKey();
        this.mPhoneCode = phoneCode;
    }

    public int getLoginType() {
        return Util.isNullOrNil(mPassword) ? ProtocolConstants.LOGIN_METHOD.PHONE_LOGIN :
                ProtocolConstants.LOGIN_METHOD.ACCOUNT_LOGIN;
    }

    private String getUsername() {
        return mUsername;
    }

    private String getPassword() {
        return mPassword;
    }


    @Override
    public byte[] getRequestBody() {
        Racecar.AofeiLoginRequest.Builder builder = Racecar.AofeiLoginRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setAccount(mUsername);
        builder.setTempkey(ByteString.copyFrom(mTempKey));

        if (!Util.isNullOrNil(mPassword)) {
            builder.setPasswd(mPassword);
        }

        if (!Util.isNullOrNil(mPhoneCode)) {
            builder.setPhoneVerify(mPhoneCode);
        }

        return builder.build().toByteArray();
    }


    @Override
    public byte[] getAesKey() {
        return mTempKey;
    }

    /**
     * Create the auth request from the local file.by default it is
     * auto auth.
     *
     * @return
     */
    public static NetSceneAlphaLogin buildFromFile() {
        CfgFs cfgFs = new CfgFs(StorageConstants.ACCOUNT_DATA_PATH);
        String username = cfgFs.getString(StorageConstants.Info_Key.USER_NAME, null);
        String password = cfgFs.getString(StorageConstants.Info_Key.MD5_PASSWORD, null);
        if (username == null || password == null) {
            Log.e(TAG, "username or password is null");
            return null;
        }
        NetSceneAlphaLogin login = new NetSceneAlphaLogin(username, password, null);
        login.setAutoAuth();

        return login;
    }

    public boolean isAutoAuth() {
        return isAutoAuth;
    }

    public void setAutoAuth() {
        isAutoAuth = true;
    }

    private static void doOnAuthSuccess(final NetSceneAlphaLogin request, final Racecar.AofeiLoginResponse response) {
        AccountManager accMgr = AppCore.getCore().getAccountManager();

        int userId = response.getUserId();
        accMgr.reset();
        accMgr.setUserId(userId);
        accMgr.getAccountInfo().setSessionKey(response.getSessionKey().toByteArray());
        accMgr.initialize();
        Log.d(TAG, response.getSessionKey().toStringUtf8());
        if(response.hasName()) {
            AccountLogic.updateAccountInfo(StorageConstants.Info_Key.NAME, response.getName());
        }
        if (request.getLoginType() == ProtocolConstants.LOGIN_METHOD.ACCOUNT_LOGIN) {
            ThreadPool.post(new Runnable() {
                @Override
                public void run() {
                    FileUtils.createFileIfNeed(new File(StorageConstants.ACCOUNT_DATA_PATH));
                    CfgFs cfgFs = new CfgFs(StorageConstants.ACCOUNT_DATA_PATH);
                    cfgFs.setString(StorageConstants.Info_Key.USER_NAME, request.getUsername());
                    cfgFs.setString(StorageConstants.Info_Key.MD5_PASSWORD, request.getPassword());
                    Log.d(TAG, "write user account data success");
                }
            });
        }

        RequestRetryManager.getMgr().resendAllRequest();

        SyncManager.getInstance().doSync(Racecar.SyncRequest.Selector.JSON, null);
        AccountLogic.updateAccountFromSVR();
    }

    private static void onAutoAuthFail(NetSceneAlphaLogin request) {
        if (request.isAutoAuth()) {
            int flag = Intent.FLAG_ACTIVITY_NEW_TASK;
            flag |= Intent.FLAG_ACTIVITY_CLEAR_TASK;
            PluginStubBus.doAction(ApplicationContext.getApplication(), PluginConstants.Plugin.PLUGIN_NAME_APP,
                    PluginConstants.App.Action.CMD_START_LAUNCHER_UI, flag, null);

        }
    }


    public static final class AlphaAuthResponseCallBack extends NetSceneResponseCallback<Racecar.AofeiLoginResponse> {

        AuthResultEvent event = new AuthResultEvent();

        @Override
        public void onLocalNetworkIssue(IRequest request) {
            RequestRetryManager.getMgr().failAllRequest(ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR);
            event.result.errorCode = ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR;
            RxEventBus.getBus().publish(event);
            onAutoAuthFail((NetSceneAlphaLogin)request);
        }

        @Override
        public void onServerConnectIssue(IRequest request) {
            RequestRetryManager.getMgr().failAllRequest(ProtocolConstants.ErrorType.NETWORK_SVR_ERROR);
            event.result.errorCode = ProtocolConstants.ErrorType.NETWORK_SVR_ERROR;
            RxEventBus.getBus().publish(event);
            onAutoAuthFail((NetSceneAlphaLogin)request);
        }

        @Override
        public void onGetBadResponse(IRequest request) {
            RequestRetryManager.getMgr().failAllRequest(ProtocolConstants.ErrorType.NETWORK_SVR_ERROR);
            event.result.errorCode = ProtocolConstants.ErrorType.NETWORK_SVR_ERROR;
            RxEventBus.getBus().publish(event);
            onAutoAuthFail((NetSceneAlphaLogin)request);
        }

        @Override
        public void onResponse(IRequest request, Racecar.AofeiLoginResponse result) {
            NetSceneAlphaLogin loginRequest = (NetSceneAlphaLogin) request;
            switch (result.getPrimaryResp().getResult()) {
                case Racecar.ErrorCode.ERROR_OK_VALUE:
                    doOnAuthSuccess(loginRequest, result);
                    Log.d(TAG, "Auth successfully, user id is %s", result.getUserId());
                    if (loginRequest.isAutoAuth() && result.getNeedFill()) {
                        Log.d(TAG, "user profile is empty");
                        int flag = Intent.FLAG_ACTIVITY_CLEAR_TASK;
                        flag |= Intent.FLAG_ACTIVITY_NEW_TASK;
                        PluginStubBus.doAction(ApplicationContext.getContext(), PluginConstants.Plugin.PLUGIN_NAME_APP,
                                PluginConstants.App.Action.CMD_START_LOCATION_CHOOSE_UI, flag, null);
                        return;
                    }
                    break;
                case Racecar.AofeiLoginResponse.ErrorCode.DISALLOW_LOGIN_VALUE:
                case Racecar.AofeiLoginResponse.ErrorCode.PASSWD_ERROR_VALUE:
                    if (loginRequest.isAutoAuth()) {
                        int flag = Intent.FLAG_ACTIVITY_CLEAR_TASK;
                        flag |= Intent.FLAG_ACTIVITY_NEW_TASK;
                        PluginStubBus.doAction(ApplicationContext.getContext(), PluginConstants.Plugin.PLUGIN_NAME_APP,
                                PluginConstants.App.Action.CMD_START_LAUNCHER_UI, flag, null);
                        return;
                    }
            }

            event.result.errorMessage = result.getPrimaryResp().getErrorMsg();
            event.result.errorCode = result.getPrimaryResp().getResult();
            event.result.needFill = result.getNeedFill();
            if(result.hasProvince()) {
                event.result.province = result.getProvince();
            }

            if(result.hasCity()) {
                event.result.city = result.getCity();
            }

            if(result.hasDistrict()) {
                event.result.district = result.getDistrict();
            }

            RxEventBus.getBus().publish(event);
        }
    }
}
