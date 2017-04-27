package com.bemetoy.bp.plugin.tasks.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bemetoy.bp.autogen.events.JoinGameEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.tasks.R;
import com.bemetoy.bp.plugin.tasks.TaskContract;
import com.bemetoy.bp.plugin.tasks.TaskPresent;
import com.bemetoy.bp.plugin.tasks.databinding.TasksListBinding;
import com.bemetoy.bp.plugin.tasks.model.TaskCenterDataSourceRemote;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.VRecyclerView;
import com.bemetoy.stub.app.base.BaseViewFragmentImpl;

import java.util.List;

/**
 * Created by albieliang on 2016/5/10.
 */
public class OfflineTasksListFragment extends BaseViewFragmentImpl<TasksListBinding> implements TaskContract.View, TasksCenterUI.TaskRefreshCallback {

    private static final String TAG = "TaskCenter.OnlineTasksListFragment";

    private TasksListAdapter mAdapter;
    private EventObserver eventObserver;
    private TaskContract.Present present;
    private boolean isFirstLoad = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        present = new TaskPresent(new TaskCenterDataSourceRemote(ProtocolConstants.TaskType.OFFLINE), this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isFirstLoad && isVisibleToUser) {
            isFirstLoad = false;
            present.loadTask(true);
        }
    }

    @Override
    public void init() {
        mAdapter = new TasksListAdapter(getActivity().getLayoutInflater(), getString(R.string.tasks_center_online));
        mBinding.dataRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.dataRv.setEmptyView(mBinding.placeHolderTv);
        mBinding.dataRv.setAdapter(mAdapter);

        mBinding.dataRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
        mBinding.dataRv.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), TaskDetailUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.TASK_INFO, mAdapter.getItem(position));
                startActivity(intent);
            }
        });
        eventObserver = new AccurateEventObserver<JoinGameEvent>() {
            @Override
            public void onReceiveEvent(JoinGameEvent event) {
                present.start();
            }
        };
        RxEventBus.getBus().register(JoinGameEvent.ID, eventObserver);
    }

    @Override
    public void onDestroyView() {
        RxEventBus.getBus().unregister(JoinGameEvent.ID, eventObserver);
        mAdapter.reset();
        super.onDestroyView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.tasks_list;
    }

    @Override
    public void showLocalNetworkError() {
        super.showLocalNetworkError();
        mBinding.placeHolderTv.setVisibility(View.VISIBLE);
        mBinding.placeHolderTv.setText(R.string.network_error_notice);
    }

    @Override
    public void showNetworkConnectionError() {
        super.showNetworkConnectionError();
        mBinding.placeHolderTv.setVisibility(View.VISIBLE);
        mBinding.placeHolderTv.setText(R.string.network_error_notice);
    }

    @Override
    protected Runnable getRetryAction() {
        return new Runnable() {
            @Override
            public void run() {
                if(present != null) {
                    Log.e(TAG, "present is null");
                    present.start();
                }
            }
        };
    }

    @Override
    public void onTaskLoaded(List<Racecar.Task> tasks) {
        mBinding.placeHolderTv.setText(R.string.tasks_center_no_task);
        mAdapter.setData(tasks);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskRefresh() {
        if (present != null) {
            present.loadTask(false);
        }
    }
}
