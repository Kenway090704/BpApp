package com.bemetoy.stub.network;

import com.bemetoy.bp.network.IRequest;

/**
 * Created by Tom on 2016/4/9.
 */
public interface INetWorkDispatcher {

    /**
     * send the request.
     * @param request
     */
    void send(IRequest request);

    /**
     * cancel the request.
     * @param request
     */
    void cancel(IRequest request);
}
