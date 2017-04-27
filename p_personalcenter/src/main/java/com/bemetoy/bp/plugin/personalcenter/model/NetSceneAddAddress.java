package com.bemetoy.bp.plugin.personalcenter.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;

/**
 * Created by Tom on 2016/5/30.
 */
public class NetSceneAddAddress extends BaseNetScene {


    private Racecar.Address mAddress;

    public NetSceneAddAddress(Racecar.Address address, ResponseCallBack callBack) {
        super(Racecar.CmdId.ADD_ADDRESS_VALUE, callBack);
        mAddress = address;
    }

    @Override
    protected Racecar.AddAddressRequest getRequest() {
        Racecar.AddAddressRequest.Builder builder = Racecar.AddAddressRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setAddress(mAddress);
        return builder.build();
    }
}
