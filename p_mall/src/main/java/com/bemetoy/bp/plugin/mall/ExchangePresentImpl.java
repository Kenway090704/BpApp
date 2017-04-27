package com.bemetoy.bp.plugin.mall;

import com.bemetoy.bp.plugin.mall.model.MallDataSource;
import com.bemetoy.bp.protocols.ProtocolConstants;

/**
 * Created by tomliu on 16-9-7.
 */
public class ExchangePresentImpl implements MallContract.ExchangePresent {


    private MallDataSource dataSource;
    private MallContract.ExchangeView view;

    public ExchangePresentImpl(MallDataSource dataSource, MallContract.ExchangeView view) {
        this.dataSource = dataSource;
        this.view = view;
    }

    @Override
    public void doExchange(int addressId, int itemId) {
        if(view.isActive()) {
            view.showLoading(true);
        }

        dataSource.doExchange(addressId, itemId, new MallDataSource.DoExchangeCallback() {
            @Override
            public void onExchangeSuccessful(int currentScore) {
                if(view.isActive()) {
                    view.showExchangeResult(MallContract.ExchangeView.RESULT_OK, null, currentScore);
                }
                view.showLoading(false);
            }

            @Override
            public void onExchangeFailed(int errorCode, String errorMsg) {
                view.showLoading(false);
                if(!view.isActive()) {
                    return;
                }
                switch (errorCode) {
                    case ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR:
                        view.showLocalNetworkError();
                        break;
                    case ProtocolConstants.ErrorType.NETWORK_SVR_ERROR:
                        view.showNetworkConnectionError();
                        break;
                    case MallContract.ExchangeView.RESULT_FAILED:
                        view.showExchangeResult(errorCode, errorMsg, 0);
                        break;
                }
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
