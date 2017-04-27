package com.bemetoy.bp.plugin.tasks.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/6/7.
 */
public class NetSceneGetTask extends NetSceneBase {

    private int mType;

    public NetSceneGetTask(int type, ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_TASK_VALUE, callBack);
        mType = type;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetTaskRequest.Builder builder = Racecar.GetTaskRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setType(mType);
        return builder.build().toByteArray();
    }
}
