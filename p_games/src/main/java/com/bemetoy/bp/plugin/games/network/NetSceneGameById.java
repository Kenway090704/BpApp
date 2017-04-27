package com.bemetoy.bp.plugin.games.network;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/6/13.
 */
public class NetSceneGameById extends NetSceneBase {

    private int mGameId;

    public NetSceneGameById(int id, ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_GAME_BY_ID_VALUE, callBack);
        mGameId = id;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetGameByIdRequest.Builder builder = Racecar.GetGameByIdRequest.newBuilder();
        builder.setGameId(mGameId);
        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
