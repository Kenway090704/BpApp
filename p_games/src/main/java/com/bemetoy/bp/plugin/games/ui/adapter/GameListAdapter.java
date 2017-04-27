package com.bemetoy.bp.plugin.games.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bemetoy.bp.autogen.events.JoinGameEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.model.GameLogic;
import com.bemetoy.bp.plugin.games.network.NetSceneJoinGame;
import com.bemetoy.bp.plugin.games.ui.GameDetailUI;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.stub.databinding.UiGameListItemBinding;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.model.GameInfo;
import com.bemetoy.stub.network.NetSceneLoadingWrapper;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.ui.BPDialogManager;

import java.util.List;

/**
 * Created by Tom on 2016/4/21.
 */
public class GameListAdapter extends ExtRecyclerViewAdapter<GameInfo> {

    private static final String TAG = "Games.GameListAdapter";

    private Context mContext;

    public GameListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public IViewHolder<GameInfo> onCreateViewHolder(View view, int viewType) {
        return new GameVH();
    }

    /**
     * Update game time status.
     * @param newInfo
     */
    public void updateGameStatus(GameInfo newInfo) {
        if (newInfo == null) {
            return;
        }

        for (GameInfo gameInfo : getItems()) {
            if (gameInfo.getId() == newInfo.getId()) {
                gameInfo.updateStatus(newInfo.getCurrentState());
                notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * Update the game join status.
     * @param id
     */
    public void updateGameJoinStatusByGameId(int id) {
        for(GameInfo info : getItems()) {
            if(info.getId() == id) {
                info.setJoined(true);
                notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * Update the game join status.
     * @param position
     */
    public void updateGameJoinStatusByPosition(int position) {
        if(position < 0 || position > getItemCount()) {
            return;
        }
        getItems().get(position).setJoined(true);
        notifyDataSetChanged();
    }


    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.ui_game_list_item, parent, false);
    }

    private class GameVH extends DataBindingViewHolder<UiGameListItemBinding, GameInfo> {

        @Override
        public void onBind(final GameInfo item, int viewType) {
            mBinding.setGameInfo(item);
            int imgId = 0;
            switch (item.getCurrentState()) {
                case ProtocolConstants.GameStatus.COMING_SOON:
                    imgId = R.drawable.game_status_icon_coming;
                    break;
                case ProtocolConstants.GameStatus.ON_GOING:
                    imgId = R.drawable.game_status_icon_on_going;
                    break;
                default:
                    imgId = R.drawable.game_status_icon_finished;
            }
            mBinding.gameHotIm.setImageResource(imgId);
            if (item.isJoined() || item.getCurrentState() != ProtocolConstants.GameStatus.COMING_SOON) {
                mBinding.joinBtn.setOnClickListener(new QueryDetailListener(item));
                mBinding.joinBtn.setBackgroundResource(R.drawable.game_query_game_detail_drawable);
            } else {
                mBinding.joinBtn.setOnClickListener(new JoinGameListener(item, mBinding));
                mBinding.joinBtn.setBackgroundResource(R.drawable.game_join_game_drawable);
            }
        }
    }

    private class JoinGameListener implements View.OnClickListener {

        private GameInfo gameInfo;
        private UiGameListItemBinding itemBinding;

        JoinGameListener(GameInfo gameInfo, UiGameListItemBinding binding) {
            this.gameInfo = gameInfo;
            this.itemBinding = binding;
        }

        @Override
        public void onClick(final View v) {
            final Button clickBtn = (Button)v;
            NetSceneJoinGame netSceneJoinGame = new NetSceneJoinGame(gameInfo.getId(),
                    new NetSceneCallbackLoadingWrapper<Racecar.JoinGameResponse>(v.getContext().getString(R.string.join_failed)) {
                @Override
                public void onResponse(IRequest request, Racecar.JoinGameResponse result) {
                    if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                        String content = mContext.getString(R.string.join_game_successful, gameInfo.getTitle());
                        BPDialogManager.showCongratulationDialog(mContext, content);
                        Log.i(TAG, "user %s join game %s successfully", AccountLogic.getAccountInfo().getUserId(),  gameInfo.getId());
                        clickBtn.setOnClickListener(new QueryDetailListener(gameInfo));
                        clickBtn.setBackgroundResource(R.drawable.game_query_game_detail_drawable);
                        JoinGameEvent joinGameEvent = new JoinGameEvent();
                        joinGameEvent.gameId = gameInfo.getId();
                        gameInfo.setJoined(true);
                        gameInfo.updateParticipants();
                        itemBinding.setGameInfo(gameInfo);
                        RxEventBus.getBus().publish(joinGameEvent);
                        Intent intent = new Intent();
                        intent.setAction(ProtocolConstants.BroadCastAction.TASK_UPDATE_ACTION);
                        v.getContext().sendBroadcast(intent);
                    } else {
                        if(result.getPrimaryResp().hasErrorMsg()) {
                            GameLogic.showJoinGameResultNotice(mContext, false, result.getPrimaryResp().getErrorMsg());
                        } else {
                            ToastUtil.show(R.string.join_failed);
                        }
                    }
                }
            });
            new NetSceneLoadingWrapper(netSceneJoinGame).doScene();
        }
    }

    private class QueryDetailListener implements View.OnClickListener {

        private GameInfo mGameInfo;

        public QueryDetailListener(GameInfo info) {
            this.mGameInfo = info;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), GameDetailUI.class);
            intent.putExtra(ProtocolConstants.IntentParams.GAME_INFO, mGameInfo);
            intent.putExtra(ProtocolConstants.IntentParams.GAME_ID, mGameInfo.getId());
            v.getContext().startActivity(intent);
        }
    }
}
