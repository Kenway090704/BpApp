package com.bemetoy.bp.plugin.games.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;

import com.bemetoy.bp.autogen.events.GameStatusUpdateEvent;
import com.bemetoy.bp.autogen.events.JoinGameEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.databinding.UiGameDetailBinding;
import com.bemetoy.bp.plugin.games.model.GameLogic;
import com.bemetoy.bp.plugin.games.network.NetSceneGameById;
import com.bemetoy.bp.plugin.games.network.NetSceneGetGameResult;
import com.bemetoy.bp.plugin.games.network.NetSceneJoinGame;
import com.bemetoy.bp.plugin.games.ui.adapter.GameResultAdapter;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.LoadMoreRecyclerView;
import com.bemetoy.stub.app.DefaultFragmentAdapter;
import com.bemetoy.stub.model.GameInfo;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.network.NetSceneLoadingWrapper;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.ui.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/5/4.
 */
public class GameDetailUI  extends BaseDataBindingActivity<UiGameDetailBinding>{

    private static final String TAG = "Games.GameDetailUI";

    private GameInfo mGameInfo;
    private int mOffset;
    private GameResultAdapter resultAdapter;
    private EventObserver joinGameEventObserver;
    private boolean isDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        if(getIntent().getExtras() != null) {
            mGameInfo = getIntent().getExtras().getParcelable(ProtocolConstants.IntentParams.GAME_INFO);
        }
        /**
         * 如果 mGameInfo 为空那么，应该去拿
         * 如果赛事为不空，赛事状态为即将开始或者进行中应该尝试重新获取赛事状态，如果为结束状态，那么赛事的状态不会再变化。
         */
        if(mGameInfo == null || (mGameInfo != null && mGameInfo.getCurrentState() != ProtocolConstants.GameStatus.FINISHED)) {
            int id = getIntent().getIntExtra(ProtocolConstants.IntentParams.GAME_ID, 0);
            final LoadingDialog dialog = new LoadingDialog(this);
            NetSceneGameById gameById = new NetSceneGameById(id, new NetSceneResponseCallback<Racecar.GetGameByIdResponse>() {
                @Override
                public void onResponse(IRequest request, Racecar.GetGameByIdResponse result) {
                    if (result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                        GameInfo gameInfo = new GameInfo(result.getGame());
                        if(mGameInfo != null) {
                            gameInfo.getGameAddress().setDistance(mGameInfo.getGameAddress().getDistance());
                        }
                        updateGameUI(gameInfo);
                        GameStatusUpdateEvent gameStatusUpdateEvent = new GameStatusUpdateEvent();
                        gameStatusUpdateEvent.game = gameInfo;
                        RxEventBus.getBus().publish(gameStatusUpdateEvent);
                    } else {
                        ToastUtil.show(R.string.get_game_failed);
                    }
                }

                @Override
                public void onNetSceneEnd() {
                    dialog.dismiss();
                }
            });
            gameById.doScene();
            dialog.show();
        }
        updateGameUI(mGameInfo);

        mBinding.gameDetailSv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.gameDetailSv.fullScroll(ScrollView.FOCUS_UP);
            }
        },10);

        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetSceneJoinGame joinGame = new NetSceneJoinGame(mGameInfo.getId(), new NetSceneCallbackLoadingWrapper<Racecar.JoinGameResponse>() {
                    @Override
                    public void onResponse(IRequest request, Racecar.JoinGameResponse result) {
                        if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                            Log.d(TAG, "join game %s successfully", mGameInfo.getId());
                            String content = getString(R.string.join_game_successful, mGameInfo.getTitle());
                            GameLogic.showJoinGameResultNotice(GameDetailUI.this, true, content);
                            mGameInfo.updateParticipants();
                            mBinding.setGame(mGameInfo);
                            setResult(ProtocolConstants.ResultCode.ACTION_JOIN_GAME, getIntent());
                            mBinding.joinBtn.setBackgroundResource(R.drawable.game_game_detail_joined);
                            mBinding.joinBtn.setEnabled(false);
                            Intent intent = new Intent();
                            intent.setAction(ProtocolConstants.BroadCastAction.TASK_UPDATE_ACTION);
                            sendBroadcast(intent);
                        } else {
                            if(result.getPrimaryResp().hasErrorMsg()) {
                                GameLogic.showJoinGameResultNotice(GameDetailUI.this, false, result.getPrimaryResp().getErrorMsg());
                            } else {
                                ToastUtil.show(R.string.join_failed);
                            }
                        }
                    }
                });
                new NetSceneLoadingWrapper(joinGame).doScene();
            }
        });

        mBinding.queryDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(ProtocolConstants.IntentParams.ADDRESS_INFO, mGameInfo.getGameAddress());
                bundle.putDouble(ProtocolConstants.IntentParams.ADDRESS_LATITUDE, mGameInfo.getGameAddress().getLatitude());
                bundle.putDouble(ProtocolConstants.IntentParams.ADDRESS_LONGITUDE, mGameInfo.getGameAddress().getLongitude());
                PluginStubBus.doAction(GameDetailUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_GAMES,
                        PluginConstants.Games.Action.CMD_START_MAP_UI, 0, bundle);
            }
        });

        joinGameEventObserver = new AccurateEventObserver<JoinGameEvent>() {

            @Override
            public void onReceiveEvent(JoinGameEvent event) {
                if(mGameInfo != null && event.gameId == event.gameId) {
                    mBinding.joinBtn.setEnabled(false);
                    mBinding.joinBtn.setBackgroundResource(R.drawable.game_game_detail_joined);
                }
            }
        };
        RxEventBus.getBus().register(JoinGameEvent.ID, joinGameEventObserver);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_game_detail;
    }

    @Override
    protected void onDestroy() {
        RxEventBus.getBus().unregister(JoinGameEvent.ID, joinGameEventObserver);
        isDestroyed = true;
        super.onDestroy();
    }

    /**
     * Init the UI with the game info.
     * @param gameInfo
     */
    private void updateGameUI(final GameInfo gameInfo) {
        if(isDestroyed) {
            Log.d(TAG, "activity has been destroyed.");
            return;
        }

        if(gameInfo != null) {
            mGameInfo = gameInfo;
            mBinding.setGame(gameInfo);
            mBinding.joinBtn.setEnabled(true);
            mBinding.queryDetailBtn.setEnabled(true);

            if(gameInfo.isJoined() && gameInfo.getCurrentState() == ProtocolConstants.GameStatus.COMING_SOON) {
                mBinding.joinBtn.setEnabled(false);
                mBinding.joinBtn.setBackgroundResource(R.drawable.game_game_detail_joined);
            }

            if(gameInfo.getCurrentState() == ProtocolConstants.GameStatus.ON_GOING) {
                mBinding.joinBtn.setEnabled(false);
                mBinding.joinBtn.setBackgroundResource(R.drawable.game_game_detail_on_going);
            }

            mBinding.gameDistanceTv.setText(GameLogic.getDistanceString(mGameInfo.getGameAddress().getDistance()));
            mBinding.gameHotRg.setRating(gameInfo.getParticipants() / (float) gameInfo.getTotal() * 5);

            if (mGameInfo.getCurrentState() == ProtocolConstants.GameStatus.FINISHED) {
                mBinding.gameResultVr.setVisibility(View.VISIBLE);
                mBinding.listTitleTv.setVisibility(View.VISIBLE);
                mBinding.gameResultTitle.setVisibility(View.VISIBLE);
                mBinding.line.setVisibility(View.VISIBLE);
                mBinding.joinBtn.setBackgroundResource(R.drawable.game_game_detail_finished);
                mBinding.joinBtn.setEnabled(false);
                resultAdapter = new GameResultAdapter(this, false);
                mBinding.gameResultVr.setAdapter(resultAdapter);
                mBinding.gameResultVr.addItemDecoration(new DividerItemDecoration(this,
                        DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
                mBinding.gameResultVr.setFooterLoadingView(R.layout.ui_loading_more);
                mBinding.gameResultVr.showLoading(true);
                mBinding.gameResultVr.setOnLoadingStateChangedListener(new LoadMoreRecyclerView.IOnLoadingStateChangedListener() {
                    @Override
                    public void onLoadMore(LoadMoreRecyclerView parent, RecyclerView.Adapter adapter, boolean isForward) {
                        mBinding.gameResultVr.enableScroll(false);
                        mBinding.gameResultVr.hideLoadingView(false);
                        getGameResult();
                    }
                });
                getGameResult();
            }

            Bundle args = new Bundle();
            args.putString(ProtocolConstants.IntentParams.GAME_IMAGE, mGameInfo.getImage1());
            GameImageFragment fragment1 = new GameImageFragment();
            fragment1.setArguments(args);

            args = new Bundle();
            args.putString(ProtocolConstants.IntentParams.GAME_IMAGE, mGameInfo.getImage2());
            GameImageFragment fragment2 = new GameImageFragment();
            fragment2.setArguments(args);

            List<Fragment> fragments = new ArrayList();
            fragments.add(fragment1);
            fragments.add(fragment2);

            DefaultFragmentAdapter fragmentAdapter = new DefaultFragmentAdapter(this.getSupportFragmentManager(), fragments);
            mBinding.gameBgVw.setAdapter(fragmentAdapter);
            mBinding.gameBgVw.setOffscreenPageLimit(2);
            mBinding.gameBgVw.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if(position == 0) {
                        mBinding.gameImage1Rb.setChecked(true);
                    } else {
                        mBinding.gameImage2Rb.setChecked(true);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    /**
     * load result list from SVR
     */
    private void getGameResult() {
        NetSceneGetGameResult getGameResult = new NetSceneGetGameResult(mGameInfo.getId(), mOffset,
                new NetSceneResponseCallback<Racecar.GetGameResultResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.GetGameResultResponse result) {
                if(result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    if(result.getResultCount() == 0) {
                        return;
                    }
                    resultAdapter.appendData(result.getResultList());
                    mOffset += result.getResultCount();
                    resultAdapter.notifyDataSetChanged();
                }
            }

                    @Override
                    public void onNetSceneEnd() {
                        mBinding.gameResultVr.enableScroll(true);
                        mBinding.gameResultVr.hideLoadingView(true);
                    }
                });

        getGameResult.doScene();
    }

}
