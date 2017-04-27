package com.bemetoy.bp.plugin.mall.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.mall.MallContract;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.stub.network.NetSceneResponseCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomliu on 16-9-5.
 */
public class MallDataSourceRemote implements  MallDataSource {

    private static MallDataSourceRemote INSTANCE = null;
    private static final String TAG = "Mall.MallDataSourceRemote";
    private volatile boolean isCacheAvailable = false;
    private Map<String, Racecar.GoodsListResponse.Item> mData = new HashMap();

    public  static MallDataSourceRemote getInstance() {
        synchronized(MallDataSourceRemote.class) {
            if (INSTANCE == null) {
                INSTANCE = new MallDataSourceRemote();
            }
        }
        return INSTANCE;
    }

    private MallDataSourceRemote(){

    }

    @Override
    public void loadAllParts(final LoadPartsCallback callback) {

        if(isCacheAvailable) {
            callback.onPartsLoaded(new ArrayList(mData.values()));
            return;
        }

        NetSceneParts netSceneParts = new NetSceneParts(new NetSceneResponseCallback<Racecar.GoodsListResponse>() {
                    @Override
                    public void onResponse(IRequest request, Racecar.GoodsListResponse result) {
                        Log.d(TAG,"part list size is %d", result.getItemCount());
                        for(Racecar.GoodsListResponse.Item item : result.getItemList()) {
                            mData.put(String.valueOf(item.getGoodsId()), item);
                        }
                        callback.onPartsLoaded(result.getItemList());
                        isCacheAvailable = true;
                    }

                    @Override
                    public void onServerConnectIssue(IRequest request) {
                        callback.onLoadFailed(ProtocolConstants.ErrorType.NETWORK_SVR_ERROR);
                    }

                    @Override
                    public void onLocalNetworkIssue(IRequest request) {
                        callback.onLoadFailed(ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR);
                    }

                    @Override
                    public void onGetBadResponse(IRequest request) {
                        callback.onLoadFailed(ProtocolConstants.ErrorType.NETWORK_SVR_ERROR);
                    }
                });
        netSceneParts.doScene();
    }

    @Override
    public void doExchange(int addressId, int itemId, final DoExchangeCallback callback) {
        NetSceneExchange exchange = new NetSceneExchange(addressId, itemId, new NetSceneResponseCallback<Racecar.GoodsExchangeResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.GoodsExchangeResponse result) {
                if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    callback.onExchangeSuccessful(result.getUserScore());
                } else {
                    callback.onExchangeFailed(MallContract.ExchangeView.RESULT_FAILED, result.getPrimaryResp().getErrorMsg());
                }
            }

            @Override
            public void onRequestFailed(IRequest request) {
                callback.onExchangeFailed(ProtocolConstants.ErrorType.NETWORK_SVR_ERROR, "");
            }

            @Override
            public void onLocalNetworkIssue(IRequest request) {
                callback.onExchangeFailed(ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR, "");
            }
        });
        exchange.doScene();
    }

    @Override
    public void loadExchangeRecord(final LoadExchangeRecordCallback callback) {
        ExchangeRecordNetScene exchange = new ExchangeRecordNetScene(new NetSceneResponseCallback<Racecar.OrderListResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.OrderListResponse result) {
                if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    callback.onLoadExchangeSuccessful(result.getOrderList());
                }
            }

            @Override
            public void onRequestFailed(IRequest request) {
                callback.onLoadExchangeFailed(ProtocolConstants.ErrorType.NETWORK_SVR_ERROR);
            }

            @Override
            public void onLocalNetworkIssue(IRequest request) {
                callback.onLoadExchangeFailed(ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR);
            }
        });
        exchange.doScene();
    }


    @Override
    public void clean() {
        isCacheAvailable = false;
        mData.clear();
    }
}
