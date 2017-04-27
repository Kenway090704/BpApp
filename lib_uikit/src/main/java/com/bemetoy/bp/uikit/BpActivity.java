package com.bemetoy.bp.uikit;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by Tom on 2016/3/10.
 */
public abstract class BpActivity extends Activity{

    private static final String TAG = "com.bemetoy.bp.uikit.BpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationContext.setCurrentContext(this);
    }

    /**
     *
     * @return the layout resource id
     */
    protected abstract int getLayoutId();

    /**
     * init the view.
     */
    protected abstract void initView();

    /**
     * register the listener to the view.
     */
    protected void initListener() {

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.e(TAG, "onTrimMemory invoked the level is", level);
    }
}
