package com.bemetoy.bp.plugin.mall;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.model.Part;
import com.bemetoy.stub.app.base.BasePresent;
import com.bemetoy.stub.app.base.BaseView;

import java.util.List;

/**
 * Created by tomliu on 16-9-5.
 */
public interface MallContract {

    interface View extends BaseView {
        void loadAddParts(List<Racecar.GoodsListResponse.Item> partList);

        void showLoading(boolean showLoading);

        void showNetworkConnectionError();

        void showLocalNetworkError();
    }


    interface Present extends BasePresent {
        void loadAllPart(boolean showLoading);

        void setFilter(int filter);
    }


    interface ExchangeView extends BaseView {

        int RESULT_OK = 0;
        int RESULT_FAILED = 1;

        void showExchangeResult(int result, String message, int score);

        void showAddressChooseDialog(Racecar.Address address);
    }

    interface  ExchangePresent extends BasePresent {
        void doExchange(int addressId, int itemId);
    }


    interface ExchangeRecordView extends BaseView {
        void onExchangeRecordLoaded(List<Racecar.OrderListResponse.Order> orders);
    }

    interface ExchangeRecordPresent extends BasePresent {
        void loadExchangeRecord(boolean showLoading);
    }

}
