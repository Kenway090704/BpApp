package com.bemetoy.bp.plugin.games.network;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/6/14.
 */
public class NetSceneGetGameResult extends NetSceneBase {

    private int mGameId;
    private int mOffset;

    public NetSceneGetGameResult(int gameId, int offset, ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_GAME_RESULT_VALUE, callBack);
        mGameId = gameId;
        mOffset = offset;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetGameResultRequest.Builder builder = Racecar.GetGameResultRequest.newBuilder();
        builder.setGameId(mGameId);
        builder.setPrimaryReq(builderBaseRequest());
        builder.setOffset(mOffset);
        builder.setCount(ProtocolConstants.PAGE_SIZE);
        return builder.build().toByteArray();
    }
}
