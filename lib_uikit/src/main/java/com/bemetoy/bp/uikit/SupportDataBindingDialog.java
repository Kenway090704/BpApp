package com.bemetoy.bp.uikit;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.uikit.databinding.DialogBaseBindingContainerBinding;

/**
 * Created by Tom on 2016/4/14.
 */
public abstract class SupportDataBindingDialog extends BpAlertDialog {

    protected DialogBaseBindingContainerBinding mDataBinding;

    public SupportDataBindingDialog(Context context) {
        super(context);
        mContentView = LayoutInflater.from(context).inflate(R.layout.dialog_base_binding_container, null);
        mDataBinding = DataBindingUtil.bind(mContentView);
    }

    @Override
    public void setContentView(View view) {
        mHasSetContent = true;
        mDataBinding.rootContainer.removeAllViews();
        if (view != null) {
            mDataBinding.rootContainer.addView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

}
