package com.bemetoy.bp.plugin.ranking.ui.adapter;

import android.content.Context;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;

/**
 * Created by Tom on 2016/7/4.
 */
public abstract class AbstractRankingListAdapter extends ExtRecyclerViewAdapter<Racecar.AccountInfo> {

    Context mContext;
    Racecar.GetRankRequest.Type type;
    boolean highLightMyRanking;

    public AbstractRankingListAdapter(Context context, Racecar.GetRankRequest.Type type) {
        this.mContext = context;
        this.type = type;
    }

    public void highLightMyRanking(boolean highLight) {
        this.highLightMyRanking = highLight;
    }

    public Racecar.GetRankRequest.Type getType() {
        return type;
    }
}
