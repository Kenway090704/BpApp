package com.bemetoy.bp.plugin.personalcenter.model;

import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.network.NetSceneBase;
import com.google.protobuf.GeneratedMessageLite;

import junit.framework.Assert;

/**
 * Created by albieliang on 16/5/1.
 */
public abstract class BaseNetScene extends NetSceneBase {

    private byte[] mAesKey;

    public BaseNetScene(int cmdId, ResponseCallBack callBack) {
        super(cmdId, callBack);
        this.mAesKey = Util.geneAESKey();
    }

    @Override
    public final byte[] getRequestBody() {
        GeneratedMessageLite req = getRequest();
        Assert.assertNotNull(req);
        return req.toByteArray();
    }

    protected abstract  <T extends GeneratedMessageLite> T getRequest();

    @Override
    public byte[] getAesKey() {
        return mAesKey;
    }
}
