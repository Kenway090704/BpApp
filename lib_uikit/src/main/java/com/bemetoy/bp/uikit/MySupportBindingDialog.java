package com.bemetoy.bp.uikit;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.uikit.databinding.DialogBaseBindingContainerBinding;

/**
 * Created by Tom on 2016/6/3.
 */
public  abstract class MySupportBindingDialog extends Dialog {

    protected DialogBaseBindingContainerBinding mDataBinding;
    private View containerView;
    private View contentView;

    public MySupportBindingDialog(Context context) {
        super(context, R.style.BpDialogStyle);
        containerView = LayoutInflater.from(context).inflate(R.layout.dialog_base_binding_container, null);
        mDataBinding = DataBindingUtil.bind(containerView);
    }

    public void setDialogContent(View contentView) {
        this.contentView = contentView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding.rootContainer.removeAllViews();
        if (contentView != null) {
            mDataBinding.rootContainer.addView(contentView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        this.setContentView(mDataBinding.rootContainer);
        getWindow().setBackgroundDrawableResource(R.color.translucent);
    }
}
