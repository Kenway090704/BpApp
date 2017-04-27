package com.bemetoy.bp.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/6/17.
 */
public class DefaultFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList();

    public DefaultFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.size() == 0 ? null : fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
