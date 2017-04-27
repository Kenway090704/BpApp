package com.bemetoy.bp.uikit;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Tom on 2016/4/14.
 */
public class BindingDialog<ViewType extends ViewDataBinding> extends SupportDataBindingDialog {

    public ViewType mBinding;

    public BindingDialog(Context context, int resId) {
        super(context);
        View view = LayoutInflater.from(context).inflate(resId, null);
        mBinding = DataBindingUtil.bind(view);
        setContentView(view);
    }

}
