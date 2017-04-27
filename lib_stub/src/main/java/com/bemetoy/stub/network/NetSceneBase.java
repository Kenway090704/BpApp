package com.bemetoy.stub.network;

import android.os.Build;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.utils.DeviceUtils;
import com.bemetoy.stub.app.AppCore;

import static com.bemetoy.bp.protocols.ProtocolConstants.CLIENT_VERSION;

/**
 * Created by Tom on 2016/3/8.
 */
public abstract class NetSceneBase extends IRequest {

    private static final String TAG = "app.Network.NetSceneBase";

    private int cmdID;
    private ResponseCallBack callBack;

    public NetSceneBase(int cmdId, ResponseCallBack callBack) {
        this.cmdID = cmdId;
        this.callBack = callBack;
    }


    public void doScene() {
        AppCore.getCore().getDispatcher().send(this);
    }

    public void cancel() {
        AppCore.getCore().getDispatcher().cancel(this);
    }

    /**
     * generate the basic request.
     *
     * @return
     */
    protected Racecar.BaseRequest builderBaseRequest() {
        Racecar.BaseRequest.Builder builder = Racecar.BaseRequest.newBuilder();
        builder.setDeviceId(DeviceUtils.getDeviceId(ApplicationContext.getContext()));
        builder.setDeviceType(Build.MANUFACTURER + " " + Build.MODEL+ " " + Build.VERSION.RELEASE);
        builder.setClientVersion(CLIENT_VERSION);
        builder.setUserId(AppCore.getCore().getAccountManager().getUserId());
        return builder.build();
    }

    public int getCmdId() {
        return cmdID;
    }

    @Override
    public byte[] getAesKey() {
        return new byte[0];
    }

    @Override
    public byte[] getSessionKey() {
        return AppCore.getCore().getAccountManager().getAccountInfo().getSessionKey();
    }

    @Override
    public ResponseCallBack getResponseCallBack() {
        return callBack;
    }
}
