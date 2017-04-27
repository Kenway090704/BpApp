package com.bemetoy.bp.plugin.mall.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.R;
import com.bemetoy.bp.plugin.mall.databinding.UiMallBinding;
import com.bemetoy.bp.plugin.mall.ui.adapter.MallFragmentAdapter;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.ui.AccountChangeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/4/22.
 */
public class MallUI extends AccountChangeActivity<UiMallBinding> {

    private static final String TAG = "Mall.MallUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onAccountUpdate(Racecar.AccountInfo accountInfo) {
        mBinding.setUserInfo(accountInfo);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_mall;
    }

    @Override
    protected void init() {
        mBinding.setUserInfo(AccountLogic.getAccountInfo());
        List<Fragment> fragmentList = new ArrayList<>();
        Fragment fragment = RecommendPartsListFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(ProtocolConstants.IntentParams.PART_TYPE, ProtocolConstants.PART_TYPE.RECOMMEND);
        fragment.setArguments(bundle);
        fragmentList.add(fragment);

        Fragment fragment1 = AllPartsListFragment.getInstance();
        Bundle bundle1 = new Bundle();
        bundle1.putInt(ProtocolConstants.IntentParams.PART_TYPE, ProtocolConstants.PART_TYPE.CAR);
        fragment1.setArguments(bundle1);
        fragmentList.add(fragment1);

        mBinding.mallListVp.setAdapter(new MallFragmentAdapter(this.getSupportFragmentManager(), fragmentList));
        mBinding.mallTabBts.setViewPager(mBinding.mallListVp);
        mBinding.mallListVp.setOffscreenPageLimit(2);
        mBinding.myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MallUI.this, MyOrderUI.class);
                startActivity(intent);
            }
        });
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
