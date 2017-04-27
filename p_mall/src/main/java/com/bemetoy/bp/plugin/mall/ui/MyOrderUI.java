package com.bemetoy.bp.plugin.mall.ui;

import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.MallContract;
import com.bemetoy.bp.plugin.mall.R;
import com.bemetoy.bp.plugin.mall.databinding.UiMyOrderBinding;
import com.bemetoy.bp.plugin.mall.model.MallDataSource;
import com.bemetoy.bp.plugin.mall.model.MallDataSourceRemote;
import com.bemetoy.bp.plugin.mall.model.OrderItem;
import com.bemetoy.bp.plugin.mall.ui.adapter.MyOrderListAdapter;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerGridItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.stub.app.base.BaseViewActivityImpl;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/5/11.
 */
public class MyOrderUI extends BaseViewActivityImpl<UiMyOrderBinding> implements MallContract.ExchangeRecordView {

    private MyOrderPresent present;
    private MyOrderListAdapter myOrderListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected Runnable getRetryAction() {
        return new Runnable() {
            @Override
            public void run() {
                if(present != null) {
                    present.loadExchangeRecord(false);
                }
            }
        };
    }

    @Override
    protected void init() {
        present = new MyOrderPresent(this);
        myOrderListAdapter = new MyOrderListAdapter(this);
        mBinding.myOrdersVr.setAdapter(myOrderListAdapter);
        mBinding.myOrdersVr.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
        present.start();
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_my_order;
    }

    @Override
    public void showNetworkConnectionError() {
        super.showNetworkConnectionError();
        mBinding.placeHolderTv.setVisibility(View.VISIBLE);
        mBinding.placeHolderTv.setText(R.string.network_error_notice);
    }

    @Override
    public void showLocalNetworkError() {
        super.showLocalNetworkError();
        mBinding.placeHolderTv.setVisibility(View.VISIBLE);
        mBinding.placeHolderTv.setText(R.string.network_error_notice);
    }

    @Override
    public void onExchangeRecordLoaded(List<Racecar.OrderListResponse.Order> orders) {
        if(orders.isEmpty()) {
            mBinding.placeHolderTv.setVisibility(View.VISIBLE);
            mBinding.placeHolderTv.setText(R.string.mall_no_exchange_record);
        } else {
            mBinding.placeHolderTv.setVisibility(View.INVISIBLE);
        }
        myOrderListAdapter.setData(orders);
        myOrderListAdapter.notifyDataSetChanged();
    }


    private static final class MyOrderPresent implements MallContract.ExchangeRecordPresent {

        private MallContract.ExchangeRecordView view;
        private MallDataSource dataSource;

        public MyOrderPresent(MallContract.ExchangeRecordView view) {
            Assert.assertNotNull(view);
            this.view = view;
            this.dataSource = MallDataSourceRemote.getInstance();
        }

        @Override
        public void loadExchangeRecord(boolean showLoading) {
            if(view.isActive() && showLoading) {
                view.showLoading(true);
            }

            dataSource.loadExchangeRecord(new MallDataSource.LoadExchangeRecordCallback() {
                @Override
                public void onLoadExchangeSuccessful(List<Racecar.OrderListResponse.Order> orders) {
                    view.showLoading(false);
                    if(view.isActive()) {
                        view.onExchangeRecordLoaded(orders);
                    }
                }

                @Override
                public void onLoadExchangeFailed(int errorCode) {
                    if(!view.isActive()) {
                        return;
                    }
                    view.showLoading(false);
                    if(errorCode == ProtocolConstants.ErrorType.NETWORK_SVR_ERROR) {
                        view.showNetworkConnectionError();
                    } else if(errorCode == ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR) {
                        view.showLocalNetworkError();
                    }
                }
            });

        }

        @Override
        public void start() {
            this.loadExchangeRecord(true);
        }

        @Override
        public void stop() {

        }
    }
}
