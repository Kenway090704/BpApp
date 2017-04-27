package com.bemetoy.bp.uikit;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Created by Tom on 2016/8/8.
 */
public class MyBindingDialog<BindingView extends ViewDataBinding> extends  MySupportBindingDialog{

    public BindingView mBinding;

    public MyBindingDialog(Context context, @LayoutRes int layoutId) {
        super(context);
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        mBinding = DataBindingUtil.bind(view);
        setDialogContent(view);
    }

    @Override
    public void onStart() {
        // This MUST be called first! Otherwise the view tweaking will not be present in the displayed Dialog (most likely overriden)
        super.onStart();
//        mBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                forceWrapContent(mBinding.getRoot());
//            }
//        });
    }

    protected void forceWrapContent(View v) {
        // Start with the provided view
        View current = v;

        // Travel up the tree until fail, modifying the LayoutParams
        do {
            // Get the parent
            ViewParent parent = current.getParent();

            // Check if the parent exists
            if (parent != null) {
                // Get the view
                try {
                    current = (View) parent;
                } catch (ClassCastException e) {
                    // This will happen when at the top view, it cannot be cast to a View
                    break;
                }

                // Modify the layout
                if(current.getLayoutParams() != null) {
                    current.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }
        } while (current.getParent() != null);

        // Request a layout to be re-done
        current.requestLayout();
    }
}
