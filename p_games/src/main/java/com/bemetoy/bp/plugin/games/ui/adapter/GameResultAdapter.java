package com.bemetoy.bp.plugin.games.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.stub.databinding.UiGameResultItemBinding;
import com.bemetoy.bp.stub.databinding.UiPkGameResultItemBinding;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;

/**
 * Created by Tom on 2016/5/5.
 */
public class GameResultAdapter extends ExtRecyclerViewAdapter<Racecar.GetGameResultResponse.Result>{

    private Context mContext;
    private boolean isPKGame;

    public GameResultAdapter(Context context, boolean isPkGame){
        this.mContext = context;
        this.isPKGame = isPkGame;
    }


    @Override
    public IViewHolder<Racecar.GetGameResultResponse.Result> onCreateViewHolder(View view, int viewType) {
        if(isPKGame) {
            return new PKGameResultVH();
        } else {
            return new GameResultVH();
        }
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        if(isPKGame) {
            return LayoutInflater.from(mContext).inflate(R.layout.ui_pk_game_result_item, parent, false);
        } else {
            return LayoutInflater.from(mContext).inflate(R.layout.ui_game_result_item, parent, false);
        }
    }

    private class GameResultVH extends DataBindingViewHolder<UiGameResultItemBinding, Racecar.GetGameResultResponse.Result> {
        @Override
        public void onBind(Racecar.GetGameResultResponse.Result item, int viewType) {
            super.onBind(item, viewType);
            int position = getItems().indexOf(item);
            mBinding.setPosition(position);
            mBinding.setResult(item);
        }
    }

    private class PKGameResultVH extends DataBindingViewHolder<UiPkGameResultItemBinding, Racecar.GetGameResultResponse.Result> {
        @Override
        public void onBind(Racecar.GetGameResultResponse.Result item, int viewType) {
            super.onBind(item, viewType);
            int position = getItems().indexOf(item);
            mBinding.setPosition(position + 1);
            mBinding.setResult(item);
        }
    }

}
