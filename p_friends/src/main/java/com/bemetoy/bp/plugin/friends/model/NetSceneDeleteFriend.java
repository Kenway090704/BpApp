package com.bemetoy.bp.plugin.friends.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/6/2.
 */
public class NetSceneDeleteFriend extends NetSceneBase {

    private int mUserId;

    public NetSceneDeleteFriend(int userId, ResponseCallBack callBack) {
        super(Racecar.CmdId.DELETE_FRIEND_VALUE, callBack);
        mUserId = userId;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.DeleteAddressRequest.Builder builder = Racecar.DeleteAddressRequest.newBuilder();
        builder.setId(mUserId);
        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
