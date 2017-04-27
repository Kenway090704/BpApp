package com.bemetoy.bp.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tom on 2016/3/7.
 */
public class RequestQueueImpl implements RequestQueue {

    private static final String TAG = "Network.RequestQueueImpl";

    public static RequestQueueImpl instance = null;
    private com.android.volley.RequestQueue queue;
    private static byte[] lock = new byte[0];
    private volatile boolean hasAuthRequest = false;

    private volatile List<StreamRequest> requestList = new ArrayList<>();


    private RequestQueueImpl(Context context) {
        queue = Volley.newRequestQueue(context, new HurlStack() {
            // override this method fix EOFException.
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection connection = super.createConnection(url);
                connection.setRequestProperty("Accept-Encoding", "");
                return connection;
            }
        });

        queue.addRequestFinishedListener(new com.android.volley.RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                if (request instanceof StreamRequest) {
                    StreamRequest resp = (StreamRequest) request;
                    requestList.remove(request);
                    Log.i(TAG, "request which cmdId is %d is out of the queue", resp.getIRequest().getCmdId());
                    synchronized(lock) {
                        if (resp.getIRequest().getCmdId() == Racecar.CmdId.AOFEI_LOGIN_VALUE) {
                            hasAuthRequest = false;
                        }
                    }
                }
            }
        });

        queue.start();
    }

    public static RequestQueueImpl getNetSceneQueue(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = new RequestQueueImpl(context);
            }
        }
        return instance;
    }

    @Override
    public void addRequest(IRequest request) {

        if (Util.isNull(request)) {
            Log.e(TAG, "request is null");
            return;
        }

        StreamRequest streamRequest = new StreamRequest(request);
        if (!Util.isNull(queue) && !Util.isNullOrNil(streamRequest.getData())) {
            request.setStreamRequest(streamRequest);
            synchronized (lock) {
                if (request.getCmdId() == Racecar.CmdId.AOFEI_LOGIN_VALUE) {
                    hasAuthRequest = true;
                }
            }
            requestList.add(streamRequest);
            queue.add(streamRequest);
            Log.i(TAG, "request which cmdId is %d enter the queue", request.getCmdId());
        } else if (Util.isNull(queue)) {
            Log.e(TAG, "request queue is null");
        } else if (Util.isNullOrNil(streamRequest.getData())) {
            Log.e(TAG, "the data of the request is null");
        }
    }

    @Override
    public void cancelRequest(IRequest request) {
        if (Util.isNull(request) || Util.isNull(request.getStreamRequest())) {
            Log.e(TAG, "request or streamRequest is null ");
            return;
        }

        if (request.getStreamRequest().isCanceled()) {
            Log.w(TAG, "the Request %s has been canceled.", request.getCmdId());
            return;
        }

        request.getStreamRequest().cancel();
    }

    @Override
    public void cancelAllRequest() {
        if(requestList == null) {
            return;
        }

        Iterator<StreamRequest> iterator = requestList.iterator();
        while(iterator.hasNext()) {
            StreamRequest streamRequest = iterator.next();
            streamRequest.cancel();
            Log.w(TAG, "streamRequest id %s has been canceled", streamRequest.getIRequest().getCmdId());
        }
    }

    @Override
    public boolean hasAuthRequestInQueue() {
        return hasAuthRequest;
    }

}
