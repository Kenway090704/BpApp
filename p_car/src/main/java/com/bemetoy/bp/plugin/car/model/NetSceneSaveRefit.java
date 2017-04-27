package com.bemetoy.bp.plugin.car.model;

import android.util.SparseIntArray;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/5/29.
 */
public class NetSceneSaveRefit extends NetSceneBase {

    private String mPart;
    private int mCarId;

    public NetSceneSaveRefit(String userCarId, String part, ResponseCallBack callBack) {
        super(Racecar.CmdId.SAVE_REFIT_VALUE, callBack);
        try{
            mCarId = Integer.valueOf(userCarId);
        } catch (Exception e) {

        }
        mPart = part;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.SaveRefitRequest.Builder builder = Racecar.SaveRefitRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setUserCarId(mCarId);
        builder.setPart(mPart);
        return builder.build().toByteArray();
    }
}
