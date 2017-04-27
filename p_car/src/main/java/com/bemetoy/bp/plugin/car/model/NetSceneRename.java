package com.bemetoy.bp.plugin.car.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/5/29.
 */
public class NetSceneRename extends NetSceneBase {

    private String mCarName;
    private int mCarId;

    public NetSceneRename(String name, String userCarId, ResponseCallBack callBack) {
        super(Racecar.CmdId.RENAME_CAR_VALUE, callBack);
        this.mCarName = name;
        try {
            this.mCarId = Integer.valueOf(userCarId);
        } catch (Exception e) {
            this.mCarId = -1;
        }

    }

    @Override
    public byte[] getRequestBody() {
        Racecar.RenameCarRequest.Builder builder = Racecar.RenameCarRequest.newBuilder();
        builder.setName(mCarName);
        builder.setUserCarId(mCarId);
        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
