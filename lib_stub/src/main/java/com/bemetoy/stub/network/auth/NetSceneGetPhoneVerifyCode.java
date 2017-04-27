package com.bemetoy.stub.network.auth;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.network.NetSceneBase;
import com.google.protobuf.ByteString;

/**
 * Created by Tom on 2016/5/20.
 */
public class NetSceneGetPhoneVerifyCode extends NetSceneBase {


    private String mPhone;
    private byte [] mTempKey;
    private int mType;

    public NetSceneGetPhoneVerifyCode(String phone, int type, ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_PHONE_VERIFY_VALUE, callBack);
        this.mPhone = phone;
        this.mType = type;
        mTempKey = Util.geneAESKey();
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetPhoneVerifyRequest.Builder builder = Racecar.GetPhoneVerifyRequest.newBuilder();
        builder.setPhone(mPhone);
        builder.setTempkey(ByteString.copyFrom(mTempKey));
        builder.setPrimaryReq(builderBaseRequest());
        builder.setType(mType);
        return builder.build().toByteArray();
    }

    @Override
    public byte[] getAesKey() {
        return mTempKey;
    }
}
