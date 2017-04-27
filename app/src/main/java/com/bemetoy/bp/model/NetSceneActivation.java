package com.bemetoy.bp.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/5/18.
 */
public class NetSceneActivation extends NetSceneBase {

    public String mKey;

    public NetSceneActivation(String key, ResponseCallBack callBack) {
        super(Racecar.CmdId.ACTIVATE_CDKEY_VALUE, callBack);
        this.mKey = key;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.ActivateCdkeyRequest.Builder builder = Racecar.ActivateCdkeyRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setCdkey(mKey);
        return builder.build().toByteArray();
    }
}
