package com.bemetoy.stub.network.home;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/4/6.
 */
public class NetSceneHomeRes extends NetSceneBase {

    public NetSceneHomeRes(ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_HOME_VALUE, callBack);
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetHomeRequest.Builder builder = Racecar.GetHomeRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
