package com.bemetoy.bp.plugin.notice.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.notice.R;
import com.bemetoy.bp.plugin.notice.databinding.UiNoticeCenterBinding;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.ui.AccountChangeActivity;
import com.bemetoy.stub.ui.LoadingDialog;
import com.bemetoy.stub.ui.LoadingDialogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albieliang on 16/5/13.
 */
public class NoticeCenterUI extends AccountChangeActivity<UiNoticeCenterBinding> {

    private static final String TAG = "Notice.NoticeCenterUI";

    private Racecar.AccountInfo mAccountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onAccountUpdate(Racecar.AccountInfo accountInfo) {
        refresh();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_notice_center;
    }

    protected void init() {
        mBinding.headerBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Log.d(TAG,"user press the back button");
            }
        });
        mBinding.headerBar.avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginStubBus.doAction(NoticeCenterUI.this,
                        PluginConstants.Plugin.PLUGIN_NAME_P_PERSONAL_CENTER,
                        PluginConstants.PersonalCenter.Action.CMD_START_PERSONAL_CENTER_UI, 0, null);
            }
        });

        mBinding.listBts.setViewPager(mBinding.listVp);
        mBinding.listVp.setOffscreenPageLimit(2);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MyNoticeListFragment());
        fragmentList.add(new SysNoticeListFragment());
        mBinding.listVp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList));

        LoadingDialog loadingDialog = new LoadingDialog(this);
        LoadingDialogManager.show(loadingDialog, 2);
        refresh();
    }

    private void refresh() {
        mAccountInfo = AccountLogic.getAccountInfo();
        mBinding.setUserInfo(mAccountInfo);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG,"user press the hardware back button");
    }
}
