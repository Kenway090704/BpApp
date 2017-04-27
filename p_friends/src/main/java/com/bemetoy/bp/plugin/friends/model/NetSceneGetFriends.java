package com.bemetoy.bp.plugin.friends.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/4/27.
 */
public class NetSceneGetFriends extends NetSceneBase {

    public NetSceneGetFriends(ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_FRIEND_VALUE, callBack);
    }

    @Override
    public byte[] getRequestBody() {
        return Racecar.GetFriendRequest.newBuilder().setPrimaryReq(builderBaseRequest()).build().
                toByteArray();
    }
}
