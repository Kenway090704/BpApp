package com.bemetoy.bp.uikit;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.uikit.databinding.ActivityBaseDatabindingContainerBinding;

/**
 * Created by albieliang on 16/3/31.
 */
public abstract class SupportDataBindingActivity extends FragmentActivity {

    private static final String TAG = "Uikit.SupportDataBindingActivity";

    private ActivityBaseDatabindingContainerBinding mBinding;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_base_databinding_container,
                (ViewGroup) getWindow().getDecorView().getRootView(), false);
        super.setContentView(view);

        mBinding = DataBindingUtil.bind(view);

        int layoutId = getLayoutId();
        if (layoutId != 0) {
            setContentView(layoutId);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationContext.setCurrentContext(this);
    }

    /**
     *
     */
    protected void init() {
        // do nothing
    }

    /**
     *
     * @return
     */
    protected abstract int getLayoutId();

    protected View getContentView() {
        return mContentView;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(LayoutInflater.from(this).inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentView = view;
        mBinding.rootContainer.removeAllViews();
        if (mContentView != null) {
            mBinding.rootContainer.addView(mContentView, params);
        }
    }
}
