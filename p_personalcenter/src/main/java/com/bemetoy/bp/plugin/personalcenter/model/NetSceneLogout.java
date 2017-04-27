package com.bemetoy.bp.plugin.personalcenter.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneResponseCallback;

/**
 * Created by Tom on 2016/6/4.
 */
public class NetSceneLogout extends BaseNetScene {
    public NetSceneLogout(ResponseCallBack callBack) {
        super(Racecar.CmdId.LOGOUT_VALUE, callBack);
    }

    @Override
    protected  Racecar.LogoutRequest getRequest() {
        return Racecar.LogoutRequest.newBuilder().setPrimaryReq(builderBaseRequest()).build();
    }


}
