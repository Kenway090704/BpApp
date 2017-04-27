package com.bemetoy.bp.uikit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tom on 2016/3/11.
 */
public abstract class BpFragment extends Fragment {

    protected View contentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getLayoutId() != -1) {
            contentView = inflater.inflate(getLayoutId(),container,false);
        }
        if(contentView != null) {
            initView(contentView);
        }
        return contentView;
    }

    public abstract int getLayoutId();

    public void initView(View view){

    }

    public View getContentView(){
        return contentView;
    }

}
