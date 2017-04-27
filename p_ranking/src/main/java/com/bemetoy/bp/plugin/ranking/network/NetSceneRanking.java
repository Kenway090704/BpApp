package com.bemetoy.bp.plugin.ranking.network;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

public class NetSceneRanking extends NetSceneBase
{
    private static final String TAG = "Ranking.NetSceneRanking";
    private int mCount;
    private boolean mIsForward;
    private int mOffset;
    private Racecar.GetRankRequest.Type mType;

    public NetSceneRanking(Racecar.GetRankRequest.Type paramType, int paramInt1, boolean paramBoolean, int paramInt2, ResponseCallBack paramResponseCallBack)
    {
        super(1018, paramResponseCallBack);
        this.mType = paramType;
        this.mCount = paramInt1;
        this.mIsForward = paramBoolean;
        this.mOffset = paramInt2;
    }

    public NetSceneRanking(Racecar.GetRankRequest.Type paramType, boolean paramBoolean, int paramInt, ResponseCallBack paramResponseCallBack)
    {
        this(paramType, 20, paramBoolean, paramInt, paramResponseCallBack);
    }

    public NetSceneRanking(ResponseCallBack paramResponseCallBack)
    {
        this(Racecar.GetRankRequest.Type.NATIONAL, 20, false, 0, paramResponseCallBack);
    }

    public int getCount()
    {
        return this.mCount;
    }

    public int getOffset()
    {
        return this.mOffset;
    }

    public byte[] getRequestBody()
    {
        Racecar.GetRankRequest.Builder localBuilder = Racecar.GetRankRequest.newBuilder();
        localBuilder.setPrimaryReq(builderBaseRequest());
        localBuilder.setCount(this.mCount);
        localBuilder.setUp(this.mIsForward);
        localBuilder.setOffset(this.mOffset);
        localBuilder.setType(this.mType);
        return localBuilder.build().toByteArray();
    }

    public Racecar.GetRankRequest.Type getType()
    {
        return this.mType;
    }

    public boolean isForward()
    {
        return this.mIsForward;
    }
}