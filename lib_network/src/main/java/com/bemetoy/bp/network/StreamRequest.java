package com.bemetoy.bp.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.NetWorkUtil;
import com.bemetoy.bp.sdk.utils.Util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Tom on 2016/3/7.
 */
public class StreamRequest extends Request<com.bemetoy.bp.network.Response> implements Response.Listener<com.bemetoy.bp.network.Response> {

    private static final String TAG = "Network.StreamRequest";

    private IRequest mRequest;
    private byte [] data;

    public StreamRequest(final IRequest request) {
        /**
         * 这里的方法只能用包含body的 http 请求方式（POST,PUT,PATCH）
         * 如果使用使用其他如Get,那么在发送请求的时候重写的getBody方法将不会被调用。
         */
        super(Method.POST, request.getURL(), request.getResponseCallBack() != null ? new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int errorCode = ProtocolConstants.ErrorType.NETWORK_SVR_ERROR;
                if(error instanceof NoConnectionError) {
                   if (NetWorkUtil.isConnected(ApplicationContext.getContext())) {
                       errorCode = ProtocolConstants.ErrorType.NETWORK_SVR_ERROR;
                   } else {
                       errorCode = ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR;
                   }
                } else if (error instanceof TimeoutError || error instanceof ServerError) {
                    errorCode = ProtocolConstants.ErrorType.NETWORK_SVR_ERROR;
                } else if (error instanceof NetworkError) {
                    // for more detail about NetWorkError see <a href="http://stackoverflow.com/questions/31802105/what-exactly-does-volley-volleyerror-networkerror-mean-in-android"/>
                    errorCode = ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR;
                }
                Log.e(TAG, "VolleyError error: %s, error code is %d", error.getClass().getSimpleName(), errorCode);
                request.getResponseCallBack().onResponse(errorCode,
                        ProtocolConstants.INVALID_COMMON_HEADER_ERROR,
                        error.getMessage(), request, null);
            }
        } : null);
        mRequest = request;
        data = PacketManager.pack(request);
        setRetryPolicy(new DefaultRetryPolicy(ProtocolConstants.RetryPolicy.DEFAULT_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public final byte[] getBody() throws AuthFailureError {
        return data;
    }

    public final byte[] getData() {
        return data;
    }

    public IRequest getIRequest() {
        return mRequest;
    }

    @Override
    protected Response<com.bemetoy.bp.network.Response> parseNetworkResponse(NetworkResponse response) {
        final com.bemetoy.bp.network.Response resp = PacketManager.upPack(response.data, mRequest);
        return Response.success(resp, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(final com.bemetoy.bp.network.Response response) {
        if(isCanceled()) {
            return;
        }

        Object result = null;
        Type [] targetType = mRequest.getResponseCallBack().getClass().getGenericInterfaces();
        if(targetType.length == 0) {
            targetType = new Type[1];
            targetType[0] = mRequest.getResponseCallBack().getClass().getGenericSuperclass();
        }

        if (targetType != null && targetType.length == 1 && targetType[0] instanceof ParameterizedType) {
            Type actualType1 = ((ParameterizedType) targetType[0]).getActualTypeArguments()[0];
            if (actualType1 instanceof Class) {
                Class targetClass = (Class) actualType1;
                try {
                    if(Util.isNullOrNil(response.getRealBody())) {
                        Log.i(TAG,"the real body is null or nil");
                        if(mRequest.getResponseCallBack() != null) {
                            mRequest.getResponseCallBack().onResponse(ProtocolConstants.ErrorType.OK,
                                    response.getCommonHeader().getErrorCode(),"the real body is null or nil", mRequest, null);
                            return;
                        }
                    } else {
                        final java.lang.reflect.Method parseMethod = targetClass.getMethod("parseFrom", InputStream.class);
                        final java.lang.reflect.Method getInstanceMethod = targetClass.getMethod("getDefaultInstance");
                        final InputStream inputStream = new ByteArrayInputStream(response.getRealBody());
                        result = parseMethod.invoke(getInstanceMethod.invoke(null), new Object[]{inputStream});
                        response.setBody(result);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "ErrorType found in parsing the response :%s", e.getMessage());
                    if(mRequest.getResponseCallBack() != null) {
                        if(response.getCommonHeader() != null) {
                            mRequest.getResponseCallBack().onResponse(ProtocolConstants.ErrorType.OK,
                                    response.getCommonHeader().getErrorCode(), e.getMessage(), mRequest, null);
                        } else {
                            mRequest.getResponseCallBack().onResponse(ProtocolConstants.ErrorType.NETWORK_SVR_ERROR,
                                    -1, "", mRequest, null);
                        }
                        return;
                    }
                }

            }
        }
        this.onResponse(response);
    }

    @Override
    public Priority getPriority() {
        return mRequest.getCmdId() == Racecar.CmdId.AOFEI_LOGIN_VALUE ? Priority.IMMEDIATE: Priority.NORMAL;
    }

    @Override
    public void onResponse(com.bemetoy.bp.network.Response response) {
        if(mRequest.getResponseCallBack() != null) {
            mRequest.getResponseCallBack().onResponse(ProtocolConstants.ErrorType.OK, response.getCommonHeader().getErrorCode()
                    , "", mRequest, response.getBody());
        }
    }
}
