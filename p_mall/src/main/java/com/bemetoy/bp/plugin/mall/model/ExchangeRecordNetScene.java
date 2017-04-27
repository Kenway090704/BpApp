package com.bemetoy.bp.plugin.mall.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by tomliu on 16-9-18.
 */
public class ExchangeRecordNetScene extends NetSceneBase {

    public ExchangeRecordNetScene(ResponseCallBack callBack) {
        super(Racecar.CmdId.ORDER_LIST_VALUE, callBack);
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.OrderListRequest.Builder builder = Racecar.OrderListRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
