package com.bemetoy.bp.plugin.personalcenter.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.google.protobuf.GeneratedMessageLite;

/**
 * Created by Tom on 2016/5/30.
 */
public class NetSceneDeleteAddress extends BaseNetScene {

    private int mAddressId;

    public NetSceneDeleteAddress(int addressId, ResponseCallBack callBack) {
        super(Racecar.CmdId.DELETE_ADDRESS_VALUE, callBack);
        mAddressId = addressId;
    }

    public int getAddressId() {
        return mAddressId;
    }


    @Override
    protected Racecar.DeleteAddressRequest getRequest() {
        Racecar.DeleteAddressRequest.Builder builder = Racecar.DeleteAddressRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setId(mAddressId);
        return builder.build();
    }
}
