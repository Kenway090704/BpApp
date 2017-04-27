package com.bemetoy.bp.plugin.ranking.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bemetoy.bp.autogen.events.GoToMyRankEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.ranking.R;
import com.bemetoy.bp.plugin.ranking.databinding.UiAddFriendsBinding;
import com.bemetoy.bp.plugin.ranking.databinding.UiRankingListBinding;
import com.bemetoy.bp.plugin.ranking.network.NetSceneRanking;
import com.bemetoy.bp.plugin.ranking.ui.adapter.AbstractRankingListAdapter;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.VRecyclerView;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.network.NetSceneBase;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.network.sync.NetSceneSyncJson;
import com.bemetoy.stub.ui.BPDialogManager;
import com.bemetoy.stub.ui.LoadingDialogManager;
import com.bemetoy.stub.ui.RetryRequestFragment;
import com.bemetoy.stub.util.JsonManager;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by Tom on 2016/7/4.
 */
public abstract class
AbstractRankingFragment extends RetryRequestFragment<UiRankingListBinding> implements BGARefreshLayout.BGARefreshLayoutDelegate {

    final static String TAG = "Ranking.AbstractRankingFragment";

    int mFirst = 0;
    int mEnd = 0;
    AbstractRankingListAdapter rankingAdapter;
    EventObserver mObserver;
    Racecar.AccountInfo accountInfo = AccountLogic.getAccountInfo();
    NetSceneRanking netSceneRanking;


    @Override
    public void init() {
        rankingAdapter = getAdapter();
        final RankingListResponseCallback callback = new RankingListResponseCallback();
        netSceneRanking = new NetSceneRanking(rankingAdapter.getType(), ProtocolConstants.PAGE_SIZE, false, mFirst, callback);
        netSceneRanking.doScene();

        mBinding.rankingLv.setAdapter(rankingAdapter);
        mBinding.rankingLv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mBinding.rankingLv.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
        mBinding.rankingLv.setEmptyView(mBinding.placeHolderTv);


        mObserver = new AccurateEventObserver<GoToMyRankEvent>() {
            @Override
            public void onReceiveEvent(GoToMyRankEvent event) {
                int rankOffset = 0;
                if (event.isMyRanking) {
                    switch (rankingAdapter.getType()) {
                        case NATIONAL:
                            rankOffset = accountInfo.getNationalRank();
                            break;
                        case PROVINCE:
                            rankOffset = accountInfo.getProvinceRank();
                            break;
                        case CITY:
                            rankOffset = accountInfo.getCityRank();
                            break;
                        case CITY_WEEK:
                            rankOffset = accountInfo.getCityWeekRank();
                            break;
                    }
                    rankOffset -=4; // 让我的排名显示在正中间
                    if(rankOffset < 0) {
                        rankOffset = 0;
                    }
                    rankingAdapter.highLightMyRanking(true);
                } else {
                    rankOffset = 0;
                    rankingAdapter.highLightMyRanking(false);
                }

                Log.d(TAG, "user check MyRanking: %b, current type is %s and ranking is %d", event.isMyRanking, rankingAdapter.getType(), rankOffset);
                netSceneRanking = new NetSceneRanking(rankingAdapter.getType(), ProtocolConstants.PAGE_SIZE,
                        false, rankOffset, new RankingListResponseCallback(true));
                netSceneRanking.doScene();
            }
        };

        RxEventBus.getBus().register(GoToMyRankEvent.ID, mObserver);

        mBinding.rankingLv.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, final int position, long id) {
                final BpDialog<UiAddFriendsBinding> dialog = new BpDialog(getActivity(), R.layout.ui_add_friends);
                final Racecar.AccountInfo userAccount = rankingAdapter.getItem(position);
                // whe the user click himself/ herself the add friend button should be hide.
                if (userAccount.getUserId() == accountInfo.getUserId()) {
                    dialog.mBinding.addFriendBtn.setVisibility(View.INVISIBLE);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(ProtocolConstants.IntentParams.USER_ID, userAccount.getUserId());
                    PluginStubBus.doActionForResult(null, PluginConstants.Plugin.PLUGIN_NAME_P_FRIENDS, PluginConstants.FRIENDS.Action.CMD_QUERY_RELATIONSHIP,
                            bundle, 0, new IPluginStub.OnActionResult() {
                                @Override
                                public void onActionResult(Context context, int cmd, int resultCode, Bundle data) {
                                    if (resultCode == ACTION_RESULT_OK && data != null) {
                                        boolean isFriend = data.getBoolean(ProtocolConstants.IntentParams.IS_FRIEND);
                                        if (isFriend) {
                                            dialog.mBinding.addFriendBtn.setVisibility(View.INVISIBLE);
                                            dialog.mBinding.isFriendTv.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            });
                }

                dialog.mBinding.addFriendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                       BPDialogManager.showAddFriendsDialog(getActivity(), userAccount.getName(),
                               userAccount.getUserId(), accountInfo.getName(), accountInfo.getUserId(), null);
                    }
                });
                dialog.mBinding.setAccInfo(userAccount);
                dialog.mBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.setCancelable(true);
                dialog.show();
            }
        });

        initRefreshLayout();
    }

    private void initRefreshLayout() {
        mBinding.refreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGAStickinessRefreshViewHolder refreshViewHolder = new BGAStickinessRefreshViewHolder(this.getContext(), true);
        refreshViewHolder.setStickinessColor(R.color.colorPrimary);
        refreshViewHolder.setRotateImage(R.drawable.loading_circle);
        // 设置下拉刷新和上拉加载更多的风格
        refreshViewHolder.setLoadingMoreText("加载中...");
        refreshViewHolder.setSpringDistanceScale(0.01f);
        mBinding.refreshLayout.setRefreshViewHolder(refreshViewHolder);
    }


    @Override
    public NetSceneBase getNetScene() {
        return netSceneRanking;
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_ranking_list;
    }

    public abstract AbstractRankingListAdapter getAdapter();


    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        NetSceneRanking netSceneRanking = new NetSceneRanking(rankingAdapter.getType(), ProtocolConstants.PAGE_SIZE,
                false, mEnd, new RankingListResponseCallback());
        netSceneRanking.doScene();
        mBinding.refreshLayout.setEnabled(false);
        return true;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if(mFirst == 0) {
            ThreadPool.postToMainThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.refreshLayout.endRefreshing();
                }
            }, 1000);
            return;
        }
        NetSceneRanking netSceneRanking = new NetSceneRanking(rankingAdapter.getType(), ProtocolConstants.PAGE_SIZE,
                true, mFirst, new RankingListResponseCallback());
        netSceneRanking.doScene();
    }

    class RankingListResponseCallback extends NetSceneResponseCallback<Racecar.GetRankResponse> {

        private boolean clearCurrentData = false;

        public RankingListResponseCallback(boolean clearCurrentData) {
            this.clearCurrentData = clearCurrentData;
        }

        public RankingListResponseCallback() {

        }

        @Override
        public void onRequestFailed(IRequest request) {
            setShouldRetry(true);
            mBinding.rankingLv.setStatusCorrect(false);
            mBinding.placeHolderTv.setText(R.string.network_error_notice);
        }

        @Override
        public void onLocalNetworkIssue(IRequest request) {
            super.onLocalNetworkIssue(request);
            onRequestFailed(request);
        }

        @Override
        public void onResponse(IRequest request, Racecar.GetRankResponse result) {
            mBinding.rankingLv.setStatusCorrect(true);
            mBinding.placeHolderTv.setText(R.string.no_ranking_info);
            if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                final NetSceneRanking netSceneRanking = (NetSceneRanking) request;
                Log.d(TAG, "ranking item size is %s" + result.getAccountInfoCount());
                if (clearCurrentData) {
                    rankingAdapter.clearData();
                    rankingAdapter.appendData(result.getAccountInfoList());
                    rankingAdapter.notifyDataSetChanged();
                    mFirst = netSceneRanking.getOffset();
                    mEnd = mFirst + result.getAccountInfoCount();
                    mBinding.rankingLv.scrollToPosition(0);
                    return;
                }

                if (netSceneRanking.isForward()) {
                    rankingAdapter.insertData(result.getAccountInfoList());
                    mFirst -= result.getAccountInfoCount();
                } else {
                    rankingAdapter.appendData(result.getAccountInfoList());
                    mEnd += result.getAccountInfoCount();
                }
                rankingAdapter.notifyDataSetChanged();
            } else {
                ToastUtil.show(R.string.load_ranking_fail);
            }
        }

        @Override
        public void onNetSceneEnd() {
            LoadingDialogManager.countDown();
            hideRetryDialog();
            mBinding.refreshLayout.endLoadingMore();
            mBinding.refreshLayout.endRefreshing();
        }
    }
}
