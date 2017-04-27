package com.bemetoy.bp.uikit;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.databinding.FragmentBaseDatabindingContainerBinding;

/**
 * Created by Tom on 2016/4/18.
 */
public abstract class BaseDataBindingFragment<ViewData extends ViewDataBinding> extends Fragment {

    private static final String TAG = "UI.BaseDataBindingFragment";

    protected View contentView;
    public ViewData mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_base_databinding_container,container,false);
        if(getLayoutId() != -1) {
            contentView = inflater.inflate(getLayoutId(),null);
        }
        if(contentView != null) {
            mBinding = DataBindingUtil.bind(contentView);
            FragmentBaseDatabindingContainerBinding rootViewBinding = DataBindingUtil.bind(rootView);
            rootViewBinding.rootContainer.removeAllViews();
            rootViewBinding.rootContainer.addView(contentView,new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            init();
        } else {
            Log.w(TAG, "init data binding failed, content view is null.");
        }
        return rootView;
    }

    public abstract int getLayoutId();

    public void init(){

    }

    public View getContentView(){
        return contentView;
    }

}
