package com.bemetoy.bp.uikit;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by albieliang on 16/3/31.
 */
public abstract class BaseDataBindingActivity<DataBindingType extends ViewDataBinding> extends SupportDataBindingActivity {

    private static final String TAG = "UI.BaseDataBindingActivity";

    protected DataBindingType mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
    }

    protected void initDataBinding() {
        View contentView = getContentView();
        if (contentView == null) {
            Log.w(TAG, "init data binding failed, content view is null.");
            return;
        }
        mBinding = DataBindingUtil.bind(contentView);
    }
}
