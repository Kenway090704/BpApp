package com.bemetoy.bp.plugin.mall.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.model.Part;

import java.util.List;

/**
 * Created by tomliu on 16-9-5.
 */
public interface MallDataSource {

    interface LoadPartsCallback {
        void onPartsLoaded(List<Racecar.GoodsListResponse.Item> tasks);
        void onLoadFailed(int errorCode);
    }

    interface DoExchangeCallback {
        void onExchangeSuccessful(int currentScore);
        void onExchangeFailed(int errorCode, String errorMsg);
    }

    interface LoadExchangeRecordCallback {
        void onLoadExchangeSuccessful(List<Racecar.OrderListResponse.Order> orders);
        void onLoadExchangeFailed(int errorCode);
    }


    void loadAllParts(LoadPartsCallback callback);

    void doExchange(int addressId, int itemId, DoExchangeCallback callback);

    void loadExchangeRecord(LoadExchangeRecordCallback callback);

    void clean();
}
