package com.bemetoy.stub.network;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.stub.app.AppCore;

import java.util.ArrayList;
import java.util.List;

/**
 * 在Auth成功之后应该重发所有应该重发的请求，然后将map清空。
 *
 * 如果Auth重试三次失败之后，需要调用所用请求的responsecall 返回一个请求auth失败的结果。
 * <p/>
 * Created by Tom on 2016/3/25.
 */
public class RequestRetryManager {

    public static final String TAG = "NetWork.RequestRetryManager";

    private static final RequestRetryManager requestRetryManager = new RequestRetryManager();

    private List<IRequest> requests = new ArrayList();

    private int mCurrentAutRetryTimes = 0;

    private RequestRetryManager() {

    }

    public static RequestRetryManager getMgr(){
        return requestRetryManager;
    }

    /**
     * Add the request to the resend list.
     * @param request
     */
    public void addRetryRequest(IRequest request) {
        if(!requests.contains(request)) {
            requests.add(request);
        }
    }


    /**
     * update current auth retry times based on the auth result.
     * @param authResult whether auth successfully.
     */
    public void updateRetryTimes(boolean authResult) {
        if(authResult) {
            mCurrentAutRetryTimes = 0;
        } else {
            mCurrentAutRetryTimes++;
        }
    }


    /**
     * after auth successfully should resend the request.
     */
    public void resendAllRequest() {
        for (IRequest request : requests) {
            Log.i(TAG, "resend request :%d", request.getCmdId());
            AppCore.getCore().getDispatcher().send(request);
        }
        requests.clear();
    }

    /**
     * When auth failed after three times,should mark all request failed
     */
    public void failAllRequest(int errType) {
        for (IRequest request : requests) {
            request.getResponseCallBack().onResponse(errType,
                    Racecar.AofeiLoginResponse.ErrorCode.ACCOUNT_ERROR_VALUE, "",
                    request, null);
        }
        requests.clear();
    }

}
