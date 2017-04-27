package com.bemetoy.bp.uikit;

import android.content.Context;
import android.databinding.ViewDataBinding;

/**
 * Created by Tom on 2016/4/14.
 */
public class BpDialog<T extends ViewDataBinding>extends BindingDialog<T> {
    public BpDialog(Context context, int resId) {
        super(context, resId);
    }
}
