package com.bemetoy.bp.plugin.ranking.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.ranking.R;
import com.bemetoy.bp.plugin.ranking.databinding.UiRankingListItemBinding;
import com.bemetoy.stub.app.AppCore;

/**
 * Created by Tom on 2016/4/20.
 */
public class RankingListAdapter extends AbstractRankingListAdapter  {

    private static final String TAG = "Ranking.RankingListAdapter";

    public RankingListAdapter(Context context, Racecar.GetRankRequest.Type type) {
        super(context, type);
    }

    @Override
    public IViewHolder<Racecar.AccountInfo> onCreateViewHolder(View view, int viewType) {
        return new RankingItemVH();
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.ui_ranking_list_item, parent, false);
    }

    private class RankingItemVH extends DataBindingViewHolder<UiRankingListItemBinding, Racecar.AccountInfo> {

        @Override
        public void onBind(Racecar.AccountInfo item, int viewType) {
            String rankStr = "";
            switch (type) {
                case NATIONAL:
                    rankStr = String.valueOf(item.getNationalRank());
                    break;
                case PROVINCE:
                    rankStr = String.valueOf(item.getProvinceRank());
                    break;
                case CITY:
                    rankStr = String.valueOf(item.getCityRank());
                    break;
            }

            mBinding.rankingTv.setText(rankStr);
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
