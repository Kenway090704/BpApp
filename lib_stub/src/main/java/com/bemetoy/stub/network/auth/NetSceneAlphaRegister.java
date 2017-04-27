package com.bemetoy.stub.network.auth;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.network.NetSceneBase;
import com.google.protobuf.ByteString;

/**
 * Created by Tom on 2016/5/20.
 */
public class NetSceneAlphaRegister extends NetSceneBase {

    private String mUsername;
    private String mPassword;
    private byte [] mTempKey;
    private String verifyCode;

    public NetSceneAlphaRegister(String username, String password, String verifyCode, ResponseCallBack callBack) {
        super(Racecar.CmdId.AOFEI_REGISTER_VALUE, callBack);
        this.mUsername = username;
        this.mPassword = password;
        mTempKey = Util.geneAESKey();
        this.verifyCode = verifyCode;
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.AofeiRegistRequest.Builder builder = Racecar.AofeiRegistRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setAccount(mUsername);
        builder.setPasswd(mPassword);
        builder.setTempkey(ByteString.copyFrom(mTempKey));
        if(!Util.isNullOrNil(verifyCode)) {
            builder.setPhoneVerify(verifyCode);
        }
        return builder.build().toByteArray();
    }

    @Override
    public byte[] getAesKey() {
        return mTempKey;
    }
}
