package com.bemetoy.bp.plugin.personalcenter.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bemetoy.bp.autogen.events.GPSLocationUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiGameRecordListBinding;
import com.bemetoy.bp.plugin.personalcenter.model.NetSceneGetAccountGame;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.VRecyclerView;
import com.bemetoy.stub.model.GameInfo;
import com.bemetoy.stub.network.NetSceneBase;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.ui.LoadingDialogManager;
import com.bemetoy.stub.ui.RetryRequestFragment;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by albieliang on 2016/5/4.
 */
public class PKGameListFragment extends RetryRequestFragment<UiGameRecordListBinding> implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private static final String TAG = "PersonalCenter.PKGameListFragment";

    private GamePerformanceAdapter mAdapter;
    private EventObserver eventObserver;
    private int mOffset;
    private NetSceneGetAccountGame netScene;

    @Override
    public void init() {
        mAdapter = new GamePerformanceAdapter(getActivity().getLayoutInflater(), getString(R.string.personal_center_pk_game));
        mBinding.dataRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.dataRv.setAdapter(mAdapter);
        mBinding.dataRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));

        mBinding.dataRv.setEmptyView(mBinding.emptyView);
        mBinding.emptyView.setText("您还没报名参加PK赛");
        mBinding.dataRv.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(ProtocolConstants.IntentParams.GAME_INFO, mAdapter.getItem(position));
                PluginStubBus.doAction(getActivity(), PluginConstants.Plugin.PLUGIN_NAME_P_GAMES, PluginConstants.Games.Action.CMD_START_PK_GAME_DETAIL, 0, bundle);
            }
        });
        eventObserver = new AccurateEventObserver<GPSLocationUpdateEvent>() {
            @Override
            public void onReceiveEvent(GPSLocationUpdateEvent event) {
                mAdapter.updateDistance();
            }
        };
        RxEventBus.getBus().register(GPSLocationUpdateEvent.ID, eventObserver);
        initRefreshLayout();
        updateGameList();
    }

    private void initRefreshLayout() {
        mBinding.refreshLayout.setDelegate(this);
        BGAStickinessRefreshViewHolder refreshViewHolder = new BGAStickinessRefreshViewHolder(this.getContext(), true);
        mBinding.refreshLayout.setPullDownRefreshEnable(false);
        refreshViewHolder.setStickinessColor(R.color.colorPrimary);
        refreshViewHolder.setRotateImage(R.drawable.loading_circle);
        refreshViewHolder.setLoadingMoreText("加载中...");
        mBinding.refreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    private void updateGameList() {
        netScene = new NetSceneGetAccountGame(ProtocolConstants.GameType.PK_GAME, mOffset,
                ProtocolConstants.PAGE_SIZE,new NetSceneResponseCallback<Racecar.GetAccountGameResponse>() {

            @Override
            public void onRequestFailed(IRequest request) {
                setShouldRetry(true);
                mBinding.dataRv.setStatusCorrect(false);
                mBinding.emptyView.setText(R.string.network_error_notice);
            }

            @Override
            public void onLocalNetworkIssue(IRequest request) {
                super.onLocalNetworkIssue(request);
                onRequestFailed(request);
            }

            @Override
            public void onResponse(IRequest request, Racecar.GetAccountGameResponse result) {
                mBinding.dataRv.setStatusCorrect(true);
                mBinding.emptyView.setText(R.string.personal_no_pk_game);
                if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    Log.d(TAG, "game size is %d",  result.getGameCount());
                    if(result.getGameCount() == 0) {
                        return;
                    }
                    mAdapter.appendData(GameInfo.transformList(result.getGameList()));
                    mOffset += result.getGameCount();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNetSceneEnd() {
                LoadingDialogManager.countDown();
                hideRetryDialog();
                mBinding.refreshLayout.endLoadingMore();
            }
        });
        netScene.doScene();
    }

    @Override
    public void onDestroyView() {
        RxEventBus.getBus().unregister(GPSLocationUpdateEvent.ID, eventObserver);
        super.onDestroyView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_game_record_list;
    }

    @Override
    public NetSceneBase getNetScene() {
        return netScene;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        updateGameList();
        return true;
    }
}
