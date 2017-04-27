package com.bemetoy.bp.plugin.friends.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/4/28.
 */
public class NetSceneSearchFriends extends NetSceneBase {

    private String mKeyword;

    public NetSceneSearchFriends(String keyword, ResponseCallBack callBack) {
        super(Racecar.CmdId.FIND_FRIEND_VALUE, callBack);
        mKeyword = keyword;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.FindFriendRequest.Builder builder = Racecar.FindFriendRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setName(mKeyword);
        return builder.build().toByteArray();
    }
}
