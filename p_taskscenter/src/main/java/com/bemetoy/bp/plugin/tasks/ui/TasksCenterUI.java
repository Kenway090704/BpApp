package com.bemetoy.bp.plugin.tasks.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.tasks.R;
import com.bemetoy.bp.plugin.tasks.databinding.UiTasksCenterBinding;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.ui.AccountChangeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albieliang on 16/5/10.
 */
public class TasksCenterUI extends AccountChangeActivity<UiTasksCenterBinding> {

    private Racecar.AccountInfo mAccountInfo;
    private List<TaskRefreshCallback> list = new ArrayList();
    private TaskUpdateBroadCastReceive receiver = new TaskUpdateBroadCastReceive(list);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onAccountUpdate(Racecar.AccountInfo accountInfo) {
        refresh();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_tasks_center;
    }

    protected void init() {
        mBinding.headerBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.headerBar.avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginStubBus.doAction(TasksCenterUI.this,
                        PluginConstants.Plugin.PLUGIN_NAME_P_PERSONAL_CENTER,
                        PluginConstants.PersonalCenter.Action.CMD_START_PERSONAL_CENTER_UI, 0, null);
            }
        });

        mBinding.listBts.setViewPager(mBinding.listVp);

        mBinding.listVp.setOffscreenPageLimit(2);

        List<Fragment> fragmentList = new ArrayList<>();
        OnlineTasksListFragment onlineTasksListFragment = new OnlineTasksListFragment();
        OfflineTasksListFragment offlineTasksListFragment = new OfflineTasksListFragment();
        fragmentList.add(onlineTasksListFragment);
        fragmentList.add(offlineTasksListFragment);
        list.add(onlineTasksListFragment);
        list.add(offlineTasksListFragment);
        mBinding.listVp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ProtocolConstants.BroadCastAction.TASK_UPDATE_ACTION);
        this.registerReceiver(receiver, intentFilter);
        refresh();
    }

    private void refresh() {
        mAccountInfo = AccountLogic.getAccountInfo();
        mBinding.setUserInfo(mAccountInfo);
    }

    @Override
    protected void onDestroy() {
        list.clear();
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    interface TaskRefreshCallback {
        void onTaskRefresh();
    }

    static class TaskUpdateBroadCastReceive extends BroadcastReceiver {
        List<TaskRefreshCallback> callbacks = new ArrayList();
        public TaskUpdateBroadCastReceive(List<TaskRefreshCallback> callbacks) {
            this.callbacks = callbacks;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            for(TaskRefreshCallback callback : callbacks) {
                callback.onTaskRefresh();
            }
        }
    }

}
