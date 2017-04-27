package com.bemetoy.bp.plugin.car.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/5/22.
 */
public class NetSceneActivation extends NetSceneBase {

    private String mCdKey;

    public NetSceneActivation(String cdKey, ResponseCallBack callBack) {
        super(Racecar.CmdId.ACTIVATE_CDKEY_VALUE, callBack);
        mCdKey = cdKey;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.ActivateCdkeyRequest.Builder builder = Racecar.ActivateCdkeyRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setCdkey(mCdKey);
        return builder.build().toByteArray();
    }
}
