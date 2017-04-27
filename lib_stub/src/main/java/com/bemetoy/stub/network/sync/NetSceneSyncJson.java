package com.bemetoy.stub.network.sync;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/4/26.
 */
public class NetSceneSyncJson extends NetSceneBase {

    private String mJsonStr;

    public NetSceneSyncJson(String json, ResponseCallBack callBack) {
        super(Racecar.CmdId.SYNC_JSON_VALUE, callBack);
        mJsonStr = json;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.SyncJsonRequest.Builder builder = Racecar.SyncJsonRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setData(mJsonStr);
        return builder.build().toByteArray();
    }
}
