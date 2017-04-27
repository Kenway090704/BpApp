package com.bemetoy.bp.plugin.personalcenter.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;

/**
 * Created by albieliang on 2016/4/30.
 */
public class NetSceneGetAccountGame extends BaseNetScene {

    public static final String TAG = "PersonalCenter.NetSceneGetAccountGame";
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
    protected Racecar.GetAccountGameRequest getRequest() {
        Racecar.GetAccountGameRequest.Builder builder = Racecar.GetAccountGameRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setType(mType);
        builder.setOffset(mOffset);
        builder.setCount(mCount);
        return builder.build();
    }

}
