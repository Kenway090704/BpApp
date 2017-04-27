package com.bemetoy.bp.network;

/**
 * Created by Tom on 2016/3/8.
 */
public interface ResponseCallBack<T> {
    void onResponse(int errType, int errorCodeFromSVR, String errorMsg, IRequest request, T result);
}
