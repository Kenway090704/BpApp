package com.bemetoy.bp.plugin.ranking.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.ranking.R;
import com.bemetoy.bp.plugin.ranking.databinding.UiWeekRankingListItemBinding;
import com.bemetoy.stub.app.AppCore;

/**
 * Created by Tom on 2016/7/4.
 */
public class WeekExpRankingAdapter extends AbstractRankingListAdapter {

    public WeekExpRankingAdapter(Context context, Racecar.GetRankRequest.Type type) {
        super(context, type);
    }

    @Override
    public IViewHolder<Racecar.AccountInfo> onCreateViewHolder(View view, int viewType) {
        return new WeekExpRankingVH();
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.ui_week_ranking_list_item, parent, false);
    }

    private class WeekExpRankingVH extends DataBindingViewHolder<UiWeekRankingListItemBinding, Racecar.AccountInfo> {

        @Override
        public void onBind(Racecar.AccountInfo item, int viewType) {
            mBinding.rankingTv.setText(String.valueOf(item.getCityWeekRank()));
            mBinding.setUserInfo(item);
            if(highLightMyRanking && item.getUserId() == AppCore.getCore().getAccountManager().getUserId()) {
                mBinding.userInfoLl.setBackgroundResource(R.drawable.ranking_item_bg_press);
            } else {
                mBinding.userInfoLl.setBackgroundResource(R.drawable.ranking_item_bg);
            }
        }

        @Override
        public void onRecycle() {
            super.onRecycle();
        }
    }
}
