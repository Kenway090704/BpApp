package com.bemetoy.bp.plugin.ranking.ui;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.ranking.ui.adapter.AbstractRankingListAdapter;
import com.bemetoy.bp.plugin.ranking.ui.adapter.RankingListAdapter;

/**
 * Created by Tom on 2016/4/18.
 */
public class NationalRankingListFragment extends AbstractRankingFragment{

    @Override
    public AbstractRankingListAdapter getAdapter() {
        return new RankingListAdapter(getContext(), Racecar.GetRankRequest.Type.NATIONAL);
    }

}