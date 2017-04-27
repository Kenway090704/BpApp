package com.bemetoy.bp.plugin.personalcenter.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.stub.databinding.UiGameListItemBinding;
import com.bemetoy.bp.stub.databinding.UiPkGameItemBinding;
import com.bemetoy.bp.uikit.widget.recyclerview.BaseRecyclerViewAdapter;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;
import com.bemetoy.stub.model.GameInfo;
import com.bemetoy.stub.model.LocationLogic;

import java.util.Date;

/**
 * Created by albieliang on 16/5/4.
 */
public class GamePerformanceAdapter extends ExtRecyclerViewAdapter<GameInfo> {

    public static final int VIEW_TYPE_PK = 100;
    public static final int VIEW_TYPE_NORMAL = 101;

    private LayoutInflater mInflater;
    private String mCategory;

    public GamePerformanceAdapter(LayoutInflater inflater, String category) {
        mInflater = inflater;
        mCategory = category;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItems().get(position).getType() == ProtocolConstants.GameType.PK_GAME) {
            return VIEW_TYPE_PK;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public void updateGameStatus(GameInfo newInfo) {
        if(newInfo == null) {
            return;
        }

        for(GameInfo gameInfo : getItems()) {
            if(gameInfo.getId() == newInfo.getId()) {
                gameInfo.updateStatus(newInfo.getCurrentState());
                notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public BaseRecyclerViewAdapter.IViewHolder<GameInfo> onCreateViewHolder(View view, int viewType) {
        if(viewType == VIEW_TYPE_NORMAL) {
            return new GameRecordViewHolder(mCategory);
        } else {
            return new PKGameRecordViewHolder();
        }
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_NORMAL) {
            return mInflater.inflate(R.layout.ui_game_list_item, parent, false);
        } else {
            return mInflater.inflate(R.layout.ui_pk_game_item, parent, false);
        }
    }

    public void updateDistance() {
        for(GameInfo info : getItems()) {
            if(LocationLogic.getLastLocation() == null) {
                info.getGameAddress().setDistance(ProtocolConstants.ERROR_DISTANCE);
            } else {
                int distance = (int) DistanceUtil.getDistance(new LatLng(info.getGameAddress().getLatitude(), info.getGameAddress().getLongitude())
                        ,new LatLng(LocationLogic.getLastLocation().mLastLatitude,  LocationLogic.getLastLocation().mLastLongitude));
                info.getGameAddress().setDistance(distance);
            }
        }
        notifyDataSetChanged();
    }

    /**
     *
     */
    private static class GameRecordViewHolder extends ExtRecyclerViewAdapter.DataBindingViewHolder<UiGameListItemBinding, GameInfo> {

        private String mCategory;

        GameRecordViewHolder(String category) {
            mCategory = category;
        }

        @Override
        public void onBind(final GameInfo item, int viewType) {
            super.onBind(item, viewType);
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
            mBinding.joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(ProtocolConstants.IntentParams.GAME_ID, item.getId());
                    bundle.putParcelable(ProtocolConstants.IntentParams.GAME_INFO, item);
                    PluginStubBus.doAction(v.getContext(), PluginConstants.Plugin.PLUGIN_NAME_P_GAMES, PluginConstants.Games.Action.CMD_START_GAME_DETAIL, 0, bundle);
                }
            });
        }
    }

    private static class PKGameRecordViewHolder extends ExtRecyclerViewAdapter.DataBindingViewHolder<UiPkGameItemBinding, GameInfo> {

        @Override
        public void onBind(final GameInfo item, int viewType) {
            super.onBind(item, viewType);
            mBinding.setGameInfo(item);
            Date date = Util.getDate("yyyy-MM-dd hh:mm:ss", item.getBeginDate());
            mBinding.gameTimeTv.setText(Util.getDateFormat("MM月dd日 hh:ss",date.getTime()));
            mBinding.joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ProtocolConstants.IntentParams.GAME_INFO, item);
                    bundle.putInt(ProtocolConstants.IntentParams.GAME_ID, item.getId());
                    PluginStubBus.doAction(v.getContext(), PluginConstants.Plugin.PLUGIN_NAME_P_GAMES, PluginConstants.Games.Action.CMD_START_PK_GAME_DETAIL, 0, bundle);
                }
            });
        }
    }

}