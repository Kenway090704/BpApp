package com.bemetoy.bp.plugin.tasks.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;
import com.bemetoy.stub.network.NetSceneResponseCallback;

/**
 * Created by Tom on 2016/6/7.
 */
public class NetSceneGetReward extends NetSceneBase {

    private int mTaskId;

    public NetSceneGetReward(int taskId, ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_REWARD_VALUE, callBack);
        mTaskId = taskId;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetRewardRequest.Builder builder = Racecar.GetRewardRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setTaskId(mTaskId);
        return builder.build().toByteArray();
    }


}
