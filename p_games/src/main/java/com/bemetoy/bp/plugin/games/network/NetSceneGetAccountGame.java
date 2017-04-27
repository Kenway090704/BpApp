package com.bemetoy.bp.plugin.games.network;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

public class NetSceneGetAccountGame extends NetSceneBase {

    public static final String TAG = "Games.NetSceneGetAccountGame";
    private int mType;
    private int mOffset;
    private int mCount;

    public NetSceneGetAccountGame(int type, int offset, int count, ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_ACCOUNT_GAME_VALUE, callBack);
        mType = type;
        mOffset = offset;
        mCount = count;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetAccountGameRequest.Builder builder = Racecar.GetAccountGameRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setType(mType);
        builder.setOffset(mOffset);
        builder.setCount(mCount);
        return builder.build().toByteArray();
    }


}