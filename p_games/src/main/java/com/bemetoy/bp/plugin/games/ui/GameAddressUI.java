package com.bemetoy.bp.plugin.games.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bemetoy.bp.autogen.events.GameStatusUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.databinding.UiGameAddressBinding;
import com.bemetoy.bp.plugin.games.model.GameLogic;
import com.bemetoy.bp.plugin.games.network.NetSceneGameByPlace;
import com.bemetoy.bp.plugin.games.ui.adapter.GameListAdapter;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.LoadMoreRecyclerView;
import com.bemetoy.bp.uikit.widget.recyclerview.VRecyclerView;
import com.bemetoy.stub.app.DefaultFragmentAdapter;
import com.bemetoy.stub.model.GameAddress;
import com.bemetoy.stub.model.GameInfo;
import com.bemetoy.stub.model.LocationLogic;
import com.bemetoy.stub.network.NetSceneResponseCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/5/5.
 */
public class GameAddressUI extends BaseDataBindingActivity<UiGameAddressBinding> {

    private GameAddress mGameAddress;

    private int mOffset;
    private GameListAdapter mAdapter;
    private EventObserver gameStatusObserver;
    private long distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        mGameAddress = getIntent().getExtras().getParcelable(ProtocolConstants.IntentParams.ADDRESS_INFO);
        Bundle args = new Bundle();
        args.putString(ProtocolConstants.IntentParams.GAME_IMAGE, mGameAddress.getImage1());
        GameImageFragment fragment1 = new GameImageFragment();
        fragment1.setArguments(args);

        args = new Bundle();
        args.putString(ProtocolConstants.IntentParams.GAME_IMAGE, mGameAddress.getImage2());
        GameImageFragment fragment2 = new GameImageFragment();
        fragment2.setArguments(args);

        List<Fragment> fragments = new ArrayList();
        fragments.add(fragment1);
        fragments.add(fragment2);

        DefaultFragmentAdapter fragmentAdapter = new DefaultFragmentAdapter(getSupportFragmentManager(), fragments);
        mBinding.gameBgVw.setAdapter(fragmentAdapter);
        mBinding.gameBgVw.setOffscreenPageLimit(2);

        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.queryDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putDouble(ProtocolConstants.IntentParams.ADDRESS_LATITUDE, mGameAddress.getLatitude());
                bundle.putDouble(ProtocolConstants.IntentParams.ADDRESS_LONGITUDE, mGameAddress.getLongitude());
                bundle.putParcelable(ProtocolConstants.IntentParams.ADDRESS_INFO, mGameAddress);
                PluginStubBus.doAction(GameAddressUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_GAMES,
                        PluginConstants.Games.Action.CMD_START_MAP_UI, 0, bundle);
            }
        });
        mBinding.setAddressInfo(mGameAddress);
        mBinding.speedwayTv.setText(getSpeedWayString(mGameAddress.getSpeedwayType()));
        mBinding.gamesListRv.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Intent intent = new Intent(GameAddressUI.this, GameDetailUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.GAME_ID, mAdapter.getItem(position).getId());
                intent.putExtra(ProtocolConstants.IntentParams.GAME_INFO, mAdapter.getItem(position));
                startActivity(intent);
            }
        });

        if (LocationLogic.getLastLocation() != null) {
            distance = (int) DistanceUtil.getDistance(new LatLng(mGameAddress.getLatitude(), mGameAddress.getLongitude())
                    , new LatLng(LocationLogic.getLastLocation().mLastLatitude, LocationLogic.getLastLocation().mLastLongitude));
            mBinding.distanceTv.setText(GameLogic.getDistanceString(distance));
        } else {
            distance = mGameAddress.getDistance();
        }
        mBinding.distanceTv.setText(GameLogic.getDistanceString(distance));

        final  GetGameByPlaceCB cb = new GetGameByPlaceCB();
        NetSceneGameByPlace netSceneGameByPlace = new NetSceneGameByPlace(mGameAddress.getId(), mOffset,
                ProtocolConstants.PAGE_SIZE, cb);
        netSceneGameByPlace.doScene();

        mAdapter = new GameListAdapter(this);
        mBinding.gamesListRv.setAdapter(mAdapter);
        mBinding.gamesListRv.setFooterLoadingView(R.layout.ui_loading_more);
        mBinding.gamesListRv.showLoading(true);
        mBinding.gamesListRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
        mBinding.gamesListRv.setOnLoadingStateChangedListener(new LoadMoreRecyclerView.IOnLoadingStateChangedListener() {
            @Override
            public void onLoadMore(LoadMoreRecyclerView parent, RecyclerView.Adapter adapter, boolean isForward) {
                mBinding.gamesListRv.enableScroll(false);
                mBinding.gamesListRv.hideLoadingView(false);
                NetSceneGameByPlace netSceneGameByPlace = new NetSceneGameByPlace(mGameAddress.getId(), mOffset,
                        ProtocolConstants.PAGE_SIZE, cb);
                netSceneGameByPlace.doScene();
            }
        });


        mBinding.gameBgVw.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mBinding.gameImage1Rb.setChecked(true);
                } else {
                    mBinding.gameImage2Rb.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        gameStatusObserver = new AccurateEventObserver<GameStatusUpdateEvent>() {
            @Override
            public void onReceiveEvent(GameStatusUpdateEvent event) {
                mAdapter.updateGameStatus(event.game);
            }
        };
        RxEventBus.getBus().register(GameStatusUpdateEvent.ID, gameStatusObserver);

    }

    private class GetGameByPlaceCB extends NetSceneResponseCallback<Racecar.GetGameByPlaceResponse> {
        @Override
        public void onResponse(IRequest request, Racecar.GetGameByPlaceResponse result) {
            List<GameInfo> gameInfos = new ArrayList<>();
            for (Racecar.Game game : result.getGameList()) {
                GameInfo info = new GameInfo(game);
                info.getGameAddress().setDistance(distance);
                gameInfos.add(info);
            }
            mOffset += result.getGameCount();
            mAdapter.appendData(gameInfos);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNetSceneEnd() {
            mBinding.gamesListRv.enableScroll(true);
            mBinding.gamesListRv.hideLoadingView(true);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_game_address;
    }

    @Override
    protected void onDestroy() {
        RxEventBus.getBus().unregister(GameStatusUpdateEvent.ID, gameStatusObserver);
        super.onDestroy();
    }

    private int getSpeedWayString(int type) {
        switch (type) {
            case ProtocolConstants.SpeedWayType.Horizontal:
                return R.string.horizontal_speed_way;
            case ProtocolConstants.SpeedWayType.EIGHT_SHAPE:
                return R.string.eight_speed_way;
            case ProtocolConstants.SpeedWayType.SPIRAL:
                return R.string.spiral_speed_way;
            default:
                return R.string.vertical_speed_way;
        }
    }

}
