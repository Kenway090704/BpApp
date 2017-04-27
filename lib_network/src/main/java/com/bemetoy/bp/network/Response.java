package com.bemetoy.bp.network;

import com.bemetoy.bp.autogen.protocol.Racecar;

/**
 * Created by Tom on 2016/3/16.
 */
class Response {

    private Racecar.CommonHeader mCommonHeader;
    private byte [] mRealBody;
    private Object mBody; // the body is the PB object of the mRealBody

    Response(){

    }

    public Racecar.CommonHeader getCommonHeader() {
        return mCommonHeader;
    }

    public void setCommonHeader(Racecar.CommonHeader commonHeader) {
        this.mCommonHeader = commonHeader;
    }

    public byte[] getRealBody() {
        return mRealBody;
    }

    public void setRealBody(byte[] realBody) {
        this.mRealBody = realBody;
    }

    public Object getBody() {
        return mBody;
    }

    public void setBody(Object mBody) {
        this.mBody = mBody;
    }
}
