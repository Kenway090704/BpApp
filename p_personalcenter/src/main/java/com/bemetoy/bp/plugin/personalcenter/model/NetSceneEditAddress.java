package com.bemetoy.bp.plugin.personalcenter.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;

/**
 * Created by Tom on 2016/5/31.
 */
public class NetSceneEditAddress extends BaseNetScene {


    private Racecar.Address mAddress;

    public NetSceneEditAddress(Racecar.Address address, ResponseCallBack callBack) {
        super(Racecar.CmdId.MODIFY_ADDRESS_VALUE, callBack);
        mAddress = address;
    }

    @Override
    protected Racecar.ModifyAddressRequest getRequest() {
        Racecar.ModifyAddressRequest.Builder builder = Racecar.ModifyAddressRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setAddress(mAddress);
        return builder.build();
    }
}
