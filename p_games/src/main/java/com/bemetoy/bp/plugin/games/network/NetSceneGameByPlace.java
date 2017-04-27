package com.bemetoy.bp.plugin.games.network;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/5/14.
 */
public class NetSceneGameByPlace extends NetSceneBase {


    private int mPlaceId;
    private int mOffset;
    private int mCount;

    public NetSceneGameByPlace(int placeId, int offset, int count, ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_GAME_BY_PLACE_VALUE, callBack);
        this.mCount = count;
        this.mOffset = offset;
        this.mPlaceId = placeId;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetGameByPlaceRequest.Builder builder = Racecar.GetGameByPlaceRequest.newBuilder();
        builder.setOffset(mOffset);
        builder.setCount(mCount);
        builder.setPlaceId(mPlaceId);
        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
