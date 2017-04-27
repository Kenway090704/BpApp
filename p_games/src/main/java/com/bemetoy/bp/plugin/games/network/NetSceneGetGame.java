package com.bemetoy.bp.plugin.games.network;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/5/6.
 */
public class NetSceneGetGame extends NetSceneBase {

    private static final String TAG = "Games.NetSceneGetGame";

    private int mOffset;
    private int mCount;
    private String mProvince;
    private String mCity;
    private String mDistrict;


    public NetSceneGetGame(String province, String city, String district, int offset, int count, ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_GAME_VALUE, callBack);
        mCount = count;
        mOffset = offset;
        mProvince = province;
        mCity = city;
        mDistrict = district;
        Log.d(TAG, "province is %s, city is %s, district is %s", mProvince, mCity, mDistrict);
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetGameRequest.Builder builder = Racecar.GetGameRequest.newBuilder();
        if (!Util.isNullOrBlank(mProvince)) {
            builder.setProvince(mProvince);
        }

        if (!Util.isNullOrNil(mCity)) {
            builder.setCity(mCity);
        }

        if (!Util.isNullOrNil(mDistrict)) {
            builder.setDistrict(mDistrict);
        }

        builder.setCount(mCount);
        builder.setOffset(mOffset);
        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
