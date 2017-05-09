package com.bemetoy.bp.plugin.games.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bemetoy.bp.autogen.events.GPSLocationUpdateEvent;
import com.bemetoy.bp.autogen.events.GameNoticeUpdateEvent;
import com.bemetoy.bp.autogen.events.GameStatusUpdateEvent;
import com.bemetoy.bp.autogen.events.LocationUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.databinding.UiGamesListBinding;
import com.bemetoy.bp.plugin.games.network.NetSceneGetGame;
import com.bemetoy.bp.plugin.games.ui.adapter.GameListAdapter;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.VRecyclerView;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.model.GameInfo;
import com.bemetoy.stub.network.NetSceneBase;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.ui.LoadingDialog;
import com.bemetoy.stub.ui.RetryRequestFragment;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by Tom on 2016/4/21.
 */
public class GameListFragment extends RetryRequestFragment<UiGamesListBinding> implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private static final String TAG = "Games.GameListFragment";
    private int mOffset;
    private GameListAdapter adapter;
    private EventObserver mGPSObserver; // when get the gps data, refresh the distance to the game address.
    private EventObserver mLocationObserver; // for the user change location. refresh the game list.
    private EventObserver mGameStatusUpdateObserver;// invoked when game status update.
    private Racecar.AccountInfo mAccountInfo;
    private NetSceneBase netSceneGetGame;
    private LoadingDialog loadingDialog;

    private String mProvince;
    private String mCity;
    private String mDistrict;

    @Override
    public void init() {
        mAccountInfo = AccountLogic.getAccountInfo();

        mProvince = mAccountInfo.getProvince();
        mCity = mAccountInfo.getCity();
        mDistrict = mAccountInfo.getDistrict();

        adapter = new GameListAdapter(this.getContext());
        mBinding.gamesListRv.setAdapter(adapter);
        mBinding.gamesListRv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mBinding.gamesListRv.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
        mBinding.gamesListRv.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                final GameInfo info = adapter.getItem(position);
                Intent  intent = new Intent(getActivity(), GameDetailUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.GAME_INFO, info);
                intent.putExtra(ProtocolConstants.IntentParams.GAME_ID, info.getId());
                intent.putExtra(ProtocolConstants.IntentParams.GAME_POSITION, position);
                startActivityForResult(intent, ProtocolConstants.RequestCode.ACTION);
            }
        });

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

        RxEventBus.getBus().register(GPSLocationUpdateEvent.ID, mGPSObserver);

        mLocationObserver = new AccurateEventObserver<LocationUpdateEvent>() {
                @Override
                public void onReceiveEvent(LocationUpdateEvent event) {
                    mOffset = 0;
                    mProvince = event.province;
                    mCity = event.city;
                    mDistrict = event.district;
                    if(ApplicationContext.getCurrentContext() != null && ApplicationContext.getCurrentContext() instanceof Activity) {
                        loadingDialog = new LoadingDialog(ApplicationContext.getCurrentContext());
                        loadingDialog.show();
                    }
                    getData(mProvince, mCity, mDistrict, true);
                }
            };

        mGameStatusUpdateObserver = new AccurateEventObserver<GameStatusUpdateEvent>() {
            @Override
            public void onReceiveEvent(GameStatusUpdateEvent event) {
                adapter.updateGameStatus(event.game);
            }
        };

        RxEventBus.getBus().register(LocationUpdateEvent.ID, mLocationObserver);
        RxEventBus.getBus().register(GameStatusUpdateEvent.ID, mGameStatusUpdateObserver);
        if(ApplicationContext.getCurrentContext() != null && ApplicationContext.getCurrentContext() instanceof Activity) {
            loadingDialog = new LoadingDialog(ApplicationContext.getCurrentContext());
            loadingDialog.show();
        }
        getData(mProvince, mCity, mDistrict, true);
        initRefreshLayout();
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

    @Override
    public void onDestroyView() {
        RxEventBus.getBus().unregister(LocationUpdateEvent.ID, mLocationObserver);
        RxEventBus.getBus().unregister(GameStatusUpdateEvent.ID, mGameStatusUpdateObserver);
        RxEventBus.getBus().unregister(GPSLocationUpdateEvent.ID, mGPSObserver);
        super.onDestroyView();
    }

    /**
     * Load games data from the server.
     * @param province
     * @param city
     * @param district
     * @param clearOld
     */
    private void getData(String province, String city, String district, final boolean clearOld) {
        netSceneGetGame = new NetSceneGetGame(province, city, district, mOffset, ProtocolConstants.PAGE_SIZE, new NetSceneResponseCallback<Racecar.GetGameResponse>() {
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
            public void onResponse(IRequest request, Racecar.GetGameResponse result) {
                mBinding.gamesListRv.setStatusCorrect(true);
                mBinding.placeHolderTv.setText(R.string.no_game_in_region);
                if (result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    updateGameList(result.getGameList(), clearOld);
                    GameNoticeUpdateEvent event = new GameNoticeUpdateEvent();
                    event.gameNotice = result.getText();
                    RxEventBus.getBus().publish(event);
                }
            }

            @Override
            public void onNetSceneEnd() {
                hideRetryDialog();
                if(loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                mBinding.refreshLayout.endLoadingMore();
            }
        });
        netSceneGetGame.doScene();//联网请求数据
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ProtocolConstants.RequestCode.ACTION && resultCode ==
                ProtocolConstants.ResultCode.ACTION_JOIN_GAME) {
            int gamePosition = data.getIntExtra(ProtocolConstants.IntentParams.GAME_POSITION, -1);
            adapter.updateGameJoinStatusByPosition(gamePosition);
        }
    }

    /**
     * Update the list to the UI.
     * @param list the data list
     * @param clearOld clear the cache or not
     */
    private void updateGameList(List<Racecar.Game> list, boolean clearOld) {
        Log.i(TAG, "games size is %d", list.size());
        List<GameInfo> gameInfoList = GameInfo.transformList(list);//将新获取的数据添加到gameInfoList

        if(clearOld) {
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
        getData(mProvince, mCity, mDistrict, false);
        return true;
    }
}
