package com.bemetoy.bp.plugin.games.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bemetoy.bp.autogen.events.GPSLocationUpdateEvent;
import com.bemetoy.bp.autogen.events.GameStatusUpdateEvent;
import com.bemetoy.bp.autogen.events.JoinGameEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.databinding.UiGamesListBinding;
import com.bemetoy.bp.plugin.games.network.NetSceneGetAccountGame;
import com.bemetoy.bp.plugin.games.ui.adapter.AccountGameListAdapter;
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
import com.bemetoy.stub.ui.RetryRequestFragment;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by Tom on 2016/6/2.
 */
public class AccountGameFragment extends RetryRequestFragment<UiGamesListBinding> implements BGARefreshLayout.BGARefreshLayoutDelegate{

    private static final String TAG = "Games.AccountGameFragment";

    private AccountGameListAdapter adapter;
    private int mOffset;
    private EventObserver mGPSObserver; // when get the gps data, refresh the distance to the game address.
    private EventObserver gamesUpdateStatusObserver;
    private EventObserver joinGameObserver;
    private NetSceneGetAccountGame netSceneGetGame;


    @Override
    public void onDestroyView() {
        RxEventBus.getBus().unregister(GPSLocationUpdateEvent.ID, mGPSObserver);
        RxEventBus.getBus().unregister(JoinGameEvent.ID, joinGameObserver);
        RxEventBus.getBus().unregister(GameStatusUpdateEvent.ID, gamesUpdateStatusObserver);
        super.onDestroyView();
    }

    @Override
    public void init() {
        adapter = new AccountGameListAdapter(this.getContext());
        mBinding.gamesListRv.setAdapter(adapter);
        mBinding.gamesListRv.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
        mBinding.gamesListRv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mBinding.gamesListRv.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                final GameInfo info = adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putInt(ProtocolConstants.IntentParams.GAME_ID, info.getId());
                bundle.putParcelable(ProtocolConstants.IntentParams.GAME_INFO, info);
                if(info.getType() == ProtocolConstants.GameType.PK_GAME) {
                    PluginStubBus.doAction(getContext(), PluginConstants.Plugin.PLUGIN_NAME_P_GAMES,
                            PluginConstants.Games.Action.CMD_START_PK_GAME_DETAIL, 0, bundle);
                } else  {
                    PluginStubBus.doAction(getContext(), PluginConstants.Plugin.PLUGIN_NAME_P_GAMES,
                            PluginConstants.Games.Action.CMD_START_GAME_DETAIL, 0, bundle);
                }
            }
        });

        mBinding.placeHolderTv.setText(R.string.no_account_game);
        mBinding.gamesListRv.setEmptyView(mBinding.placeHolderTv);

        mGPSObserver = new AccurateEventObserver<GPSLocationUpdateEvent>() {
            @Override
            public void onReceiveEvent(GPSLocationUpdateEvent event) {
                if(event.isValid) {
                    LatLng mine = new LatLng(event.latitude, event.longitude);
                    for (GameInfo info : adapter.getItems()) {
                        LatLng target = new LatLng(info.getGameAddress().getLatitude(), info.getGameAddress().getLongitude());
                        info.getGameAddress().setDistance((int) DistanceUtil.getDistance(mine, target));
                    }
                } else {
                    for (GameInfo info : adapter.getItems()) {
                        info.getGameAddress().setDistance(ProtocolConstants.ERROR_DISTANCE);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        };

        joinGameObserver = new AccurateEventObserver<JoinGameEvent>() {
            @Override
            public void onReceiveEvent(JoinGameEvent event) {
                mOffset = 0;
                getData(true);
            }
        };

        gamesUpdateStatusObserver = new AccurateEventObserver<GameStatusUpdateEvent>() {
            @Override
            public void onReceiveEvent(GameStatusUpdateEvent event) {
                adapter.updateGameStatus(event.game);
            }
        };

        RxEventBus.getBus().register(GPSLocationUpdateEvent.ID, mGPSObserver);
        RxEventBus.getBus().register(JoinGameEvent.ID, joinGameObserver);
        RxEventBus.getBus().register(GameStatusUpdateEvent.ID, gamesUpdateStatusObserver);
        initRefreshLayout();
        getData(true);
    }

    private void initRefreshLayout() {
        mBinding.refreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGAStickinessRefreshViewHolder refreshViewHolder = new BGAStickinessRefreshViewHolder(this.getContext(), true);
        mBinding.refreshLayout.setPullDownRefreshEnable(false);
        refreshViewHolder.setStickinessColor(R.color.colorPrimary);
        refreshViewHolder.setRotateImage(R.drawable.loading_circle);
        // 设置下拉刷新和上拉加载更多的风格
        refreshViewHolder.setLoadingMoreText("加载中...");
        mBinding.refreshLayout.setRefreshViewHolder(refreshViewHolder);
    }


    /**
     * load data from server
     */
    private void getData(final boolean clearData) {
        netSceneGetGame = new NetSceneGetAccountGame(0, mOffset, ProtocolConstants.PAGE_SIZE, new NetSceneResponseCallback<Racecar.GetAccountGameResponse>() {

            @Override
            public void onRequestFailed(IRequest request) {
                setShouldRetry(true);
                mBinding.gamesListRv.setStatusCorrect(false);
                mBinding.placeHolderTv.setText(R.string.network_error_notice);
            }

            @Override
            public void onLocalNetworkIssue(IRequest request) {
                super.onLocalNetworkIssue(request);
                mBinding.gamesListRv.setStatusCorrect(false);
                mBinding.placeHolderTv.setText(R.string.network_error_notice);
            }

            @Override
            public void onResponse(IRequest request, Racecar.GetAccountGameResponse result) {
                mBinding.gamesListRv.setStatusCorrect(true);
                mBinding.placeHolderTv.setText(R.string.no_account_game);
                if (result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    updateGameList(result.getGameList(), clearData);
                }
            }

            @Override
            public void onNetSceneEnd() {
                hideRetryDialog();
                mBinding.refreshLayout.endLoadingMore();
            }
        });
        netSceneGetGame.doScene();
    }

    private void updateGameList(List<Racecar.Game> list, boolean clearData) {
        Log.v(TAG, "games size is %d", list.size());
        List<GameInfo> gameInfoList = GameInfo.transformList(list);
        if(clearData) {
            adapter.clearData();
            adapter.notifyDataSetChanged();
        }

        if (!gameInfoList.isEmpty()) {
            adapter.appendData(gameInfoList);
            mOffset += gameInfoList.size();
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.ui_games_list;
    }

    @Override
    public NetSceneBase getNetScene() {
        return netSceneGetGame;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        getData(false);
        return true;
    }
}
