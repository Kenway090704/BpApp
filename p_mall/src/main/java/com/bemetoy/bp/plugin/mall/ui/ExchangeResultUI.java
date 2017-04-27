package com.bemetoy.bp.plugin.mall.ui;

import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.R;
import com.bemetoy.bp.plugin.mall.databinding.UiExchangeResultBinding;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;

/**
 * Created by tomliu on 16-9-7.
 */
public class ExchangeResultUI extends BaseDataBindingActivity<UiExchangeResultBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        Racecar.GoodsListResponse.Item item = (Racecar.GoodsListResponse.Item)
                getIntent().getSerializableExtra(ProtocolConstants.IntentParams.PART_ITEM);
        mBinding.setPart(item);
        mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_exchange_result;
    }
}
