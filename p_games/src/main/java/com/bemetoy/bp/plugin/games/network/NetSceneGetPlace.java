package com.bemetoy.bp.plugin.games.network;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/5/10.
 */
public class NetSceneGetPlace extends NetSceneBase {

    private static final String TAG = "Games.NetSceneGetPlace";

    private String mProvince;
    private String mCity;
    private String mDistrict;

    public NetSceneGetPlace(String province, String city, String district, ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_PLACE_VALUE, callBack);
        mProvince = province;
        mCity = city;
        mDistrict = district;
        Log.d(TAG, "province is %s, mCity is %s, mDistrict is %s", mProvince, mCity, mDistrict);
    }

    public String getProvince() {
        return mProvince;
    }

    public String getCity() {
        return mCity;
    }

    public String getDistrict() {
        return mDistrict;
    }

    @Override
    public byte[] getRequestBody() {

        Racecar.GetPlaceRequest.Builder builder = Racecar.GetPlaceRequest.newBuilder();
        if(!Util.isNullOrNil(mProvince)) {
            builder.setProvince(mProvince);
        }

        if(!Util.isNullOrNil(mCity)) {
            builder.setCity(mCity);
        }

        if(!Util.isNullOrNil(mDistrict)) {
            builder.setDistrict(mDistrict);
        }

        builder.setPrimaryReq(builderBaseRequest());
        return builder.build().toByteArray();
    }
}
