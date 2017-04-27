package com.bemetoy.bp.plugin.mall.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/4/25.
 */
public class NetSceneParts extends NetSceneBase {

    public NetSceneParts(ResponseCallBack callBack) {
        super(Racecar.CmdId.GOODS_LIST_VALUE, callBack);
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.ScoreStoreRequest.Builder builder = Racecar.ScoreStoreRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
