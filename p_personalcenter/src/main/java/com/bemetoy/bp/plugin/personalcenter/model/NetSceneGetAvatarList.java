package com.bemetoy.bp.plugin.personalcenter.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;

/**
 * Created by Tom on 2016/6/6.
 */
public class NetSceneGetAvatarList extends BaseNetScene {


    public NetSceneGetAvatarList(ResponseCallBack callBack) {
        super(Racecar.CmdId.AOFEI_GET_ICONS_VALUE, callBack);
    }

    @Override
    protected Racecar.AofeiGetIconsRequest getRequest() {
        return Racecar.AofeiGetIconsRequest.newBuilder().setPrimaryReq(builderBaseRequest()).build();
    }
}
