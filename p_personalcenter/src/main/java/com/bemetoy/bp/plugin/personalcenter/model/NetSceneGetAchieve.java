package com.bemetoy.bp.plugin.personalcenter.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;

/**
 * Created by Tom on 2016/6/14.
 */
public class NetSceneGetAchieve extends BaseNetScene{


    public NetSceneGetAchieve(ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_ACHIEVE_VALUE, callBack);
    }

    @Override
    protected Racecar.GetAchieveRequest  getRequest() {
        return Racecar.GetAchieveRequest.newBuilder().setPrimaryReq(builderBaseRequest()).build();
    }
}
