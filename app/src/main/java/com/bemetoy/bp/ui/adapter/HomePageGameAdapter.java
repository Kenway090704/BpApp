package com.bemetoy.bp.ui.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.HomePageGameItemBinding;
import com.bemetoy.bp.plugin.games.model.GameLogic;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.plugin.imageloader.ImageLoader;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.ResourceTool;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;

/**
 * Created by albieliang on 16/6/14.
 */
public class HomePageGameAdapter extends ExtRecyclerViewAdapter<Racecar.Game> {

    private static final String TAG = "HomePageGameAdapter";

    private LayoutInflater mInflater;
    private int mItemHeight;

    public HomePageGameAdapter(LayoutInflater inflater) {
        mInflater = inflater;
    }

    @Override
    public IViewHolder<Racecar.Game> onCreateViewHolder(View view, int viewType) {
        return new GameItemViewHolder(mItemHeight);
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return mInflater.inflate(R.layout.home_page_game_item, parent, false);
    }

    public void setItemHeight(int height) {
        mItemHeight = height;
        Log.d(TAG, "setItemHeight(%d)", height);
    }

    /**
     *
     */
    private static class GameItemViewHolder extends ExtRecyclerViewAdapter.DataBindingViewHolder<HomePageGameItemBinding, Racecar.Game> {

        private int mItemHeight;

        GameItemViewHolder(int height) {
            mItemHeight = height;
        }

        @Override
        public void onCreateBinding(View itemView, int viewType) {
            super.onCreateBinding(itemView, viewType);
            mBinding.gameNameTv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    /*
                     * 这里46的来源是 nameLL marginLeft 为 6
                     * gameRl paddingRight 为 36
                     * gameHotIm marginLeft为 4
                     */
                    mBinding.gameNameTv.setMaxWidth(mBinding.gameRl.getMeasuredWidth() -
                            mBinding.gameHotIm.getMeasuredWidth() - mBinding.gameIv.getMeasuredWidth() -
                            ResourceTool.dip2px(ApplicationContext.getApplication(), 46));
                }
            });
        }

        @Override
        public void onBind(Racecar.Game item, int viewType) {
            super.onBind(item, viewType);

            ViewGroup.LayoutParams params = mBinding.gameRl.getLayoutParams();
            if (mItemHeight > 0) {
                params.height = mItemHeight;
            }
            mBinding.gameRl.setLayoutParams(params);

            params = mBinding.gameIv.getLayoutParams();
            params.width = (int)(mItemHeight * 1.5);
            params.height = mItemHeight;
            mBinding.gameIv.setLayoutParams(params);

            mBinding.setGame(item);
            ImageLoader.load(GameLogic.getGameImg(item.getType()), mBinding.gameIv);
        }
    }
}
