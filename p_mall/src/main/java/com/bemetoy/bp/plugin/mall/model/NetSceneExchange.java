package com.bemetoy.bp.plugin.mall.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by tomliu on 16-9-7.
 */
public class NetSceneExchange extends NetSceneBase {

    private int addressId;
    private int itemId;

    public NetSceneExchange(int addressId, int itemId, ResponseCallBack callBack) {
        super(Racecar.CmdId.GOODS_EXCHANGE_VALUE, callBack);
        this.addressId = addressId;
        this.itemId = itemId;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GoodsExchangeRequest.Builder builder = Racecar.GoodsExchangeRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setAddressId(addressId);
        builder.setGoodsId(itemId);
        return builder.build().toByteArray();
    }
}
