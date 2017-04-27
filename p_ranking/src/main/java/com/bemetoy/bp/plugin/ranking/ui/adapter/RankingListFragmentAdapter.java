package com.bemetoy.bp.plugin.ranking.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/4/20.
 */
public class RankingListFragmentAdapter extends FragmentPagerAdapter {

    private static final String TAG  = "ranking.RankingListFragmentAdapter";

    private List<? extends Fragment> fragments = new ArrayList<>();

    public RankingListFragmentAdapter(FragmentManager fm, List<? extends Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if(position < 0 || position > fragments.size() - 1) {
            return null;
        }
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
