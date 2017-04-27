package com.bemetoy.bp.plugin.personalcenter.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;

/**
 * Created by albieliang on 2016/4/30.
 */
public class NetSceneModifyAccountInfo extends BaseNetScene {

    public static final String TAG = "network.NetSceneModifyAccounInfo";
    private Racecar.AccountInfo mAccountInfo;

    public NetSceneModifyAccountInfo(Racecar.AccountInfo accountInfo, ResponseCallBack callBack) {
        super(Racecar.CmdId.MODIFY_ACCOUNT_INFO_VALUE, callBack);
        mAccountInfo = accountInfo;
    }

    @Override
    protected Racecar.ModifyAccountInfoRequest getRequest() {
        Racecar.ModifyAccountInfoRequest.Builder builder = Racecar.ModifyAccountInfoRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        if(mAccountInfo.hasName()) {
            builder.setName(mAccountInfo.getName());
        }

        if(mAccountInfo.hasProvince()) {
            builder.setProvince(mAccountInfo.getProvince());
        }

        if(mAccountInfo.hasCity()) {
            builder.setCity(mAccountInfo.getCity());
        }

        if(mAccountInfo.hasIcon()) {
            builder.setIcon(mAccountInfo.getIcon());
        }

        if(mAccountInfo.hasDistrict()) {
            builder.setDistrict(mAccountInfo.getDistrict());
        }

        return builder.build();
    }

}
