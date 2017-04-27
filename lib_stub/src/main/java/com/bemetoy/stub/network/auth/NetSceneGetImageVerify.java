package com.bemetoy.stub.network.auth;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.network.NetSceneBase;
import com.google.protobuf.ByteString;

/**
 * Created by Tom on 2016/5/23.
 */
public class NetSceneGetImageVerify extends NetSceneBase {


    private byte[] mTemKey;
    public NetSceneGetImageVerify(ResponseCallBack callBack) {
        super(Racecar.CmdId.GET_IMAGE_VERIFY_VALUE, callBack);
        mTemKey = Util.geneAESKey();
    }

    @Override
    public byte[] getRequestBody() {
        Racecar.GetImageVerifyRequest.Builder builder = Racecar.GetImageVerifyRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());
        builder.setTempkey(ByteString.copyFrom(mTemKey));
        return builder.build().toByteArray();
    }

    @Override
    public byte[] getAesKey() {
        return mTemKey;
    }
}
