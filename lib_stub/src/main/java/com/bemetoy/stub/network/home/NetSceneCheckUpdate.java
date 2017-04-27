package com.bemetoy.stub.network.home;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/7/14.
 */
public class NetSceneCheckUpdate extends NetSceneBase {

    public NetSceneCheckUpdate(ResponseCallBack callBack) {
        super(Racecar.CmdId.CHECK_VERSION_VALUE, callBack);
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.CheckVersionRequest.Builder builder = Racecar.CheckVersionRequest.newBuilder();
        builder.setType(Racecar.CheckVersionRequest.Type.ANDROID);
        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
