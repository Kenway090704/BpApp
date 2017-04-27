package com.bemetoy.bp.plugin.games.network;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/5/10.
 */
public class NetSceneJoinGame extends NetSceneBase {

    private int mGameId;

    public NetSceneJoinGame(int gameId, ResponseCallBack callBack) {
        super(Racecar.CmdId.JOIN_GAME_VALUE, callBack);
        mGameId = gameId;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.JoinGameRequest.Builder builder = Racecar.JoinGameRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setId(mGameId);
        return builder.build().toByteArray();
    }
}
