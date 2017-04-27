package com.bemetoy.bp.plugin.ranking.network;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

public class NetSceneRankingRule extends NetSceneBase
{
    private static final String TAG = "Ranking.NetSceneRankingRule";

    public NetSceneRankingRule(ResponseCallBack paramResponseCallBack)
    {
        super(1017, paramResponseCallBack);
    }

    public byte[] getRequestBody()
    {
        return Racecar.GetRankRuleRequest.newBuilder().setPrimaryReq(builderBaseRequest()).build().toByteArray();
    }
}