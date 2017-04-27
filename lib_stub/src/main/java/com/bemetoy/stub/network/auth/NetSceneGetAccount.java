package com.bemetoy.stub.network.auth;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/6/8.
 */
public class NetSceneGetAccount extends NetSceneBase {

    public NetSceneGetAccount(ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_ACCOUNT_INFO_VALUE, callBack);
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetAccountInfoRequest.Builder builder = Racecar.GetAccountInfoRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
