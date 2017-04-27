package com.bemetoy.bp.network;

import com.bemetoy.bp.protocols.ProtocolConstants;

import java.util.Arrays;

/**
 * Created by Tom on 2016/3/7.
 */
public abstract class IRequest {

    private StreamRequest mStreamRequest;

    public abstract byte [] getRequestBody();

    public abstract int getCmdId();

    public abstract byte[] getAesKey();

    public abstract byte[] getSessionKey();

    public abstract ResponseCallBack getResponseCallBack();

    StreamRequest getStreamRequest() {
        return mStreamRequest;
    }

    void setStreamRequest(StreamRequest request) {
        this.mStreamRequest = request;
    }

    public final String getURL() {
        return ProtocolConstants.HOST_CGI;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if(!(o instanceof IRequest)) {
            return false;
        }

        IRequest other = (IRequest)o;

        if(getCmdId() == other.getCmdId() && Arrays.equals(other.getRequestBody(), getRequestBody())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result  + Arrays.hashCode(getRequestBody());
        result = 31 * result  + getCmdId();
        return  result;
    }
}
