package com.bemetoy.bp.plugin.ranking.ui;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.ranking.ui.adapter.AbstractRankingListAdapter;
import com.bemetoy.bp.plugin.ranking.ui.adapter.RankingListAdapter;

/**
 * Created by Tom on 2016/7/4.
 */
public class ProvinceRankingListFragment extends AbstractRankingFragment{

    private static final String TAG = "Ranking.ProvinceRankingListFragment";

    @Override
    public AbstractRankingListAdapter getAdapter() {
        return new RankingListAdapter(getContext(), Racecar.GetRankRequest.Type.PROVINCE);
    }
}
