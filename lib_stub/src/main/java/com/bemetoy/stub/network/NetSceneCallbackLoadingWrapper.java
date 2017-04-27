package com.bemetoy.stub.network;

import android.app.Activity;
import android.support.annotation.CallSuper;

import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.ui.LoadingDialog;
import com.bemetoy.stub.ui.RetryDialog;

/**
 * Created by Tom on 2016/7/10.
 */
public abstract class NetSceneCallbackLoadingWrapper<T> extends NetSceneResponseCallback<T> {

    private static final String TAG = "Stub.NetSceneCallbackLoadingWrapper";
    private RetryDialog dialog = null;
    private LoadingDialog loadingDialog = null;
    private String title;

    public NetSceneCallbackLoadingWrapper() {
        this(null);
    }

    public NetSceneCallbackLoadingWrapper(String retryDialogTitle) {
        title = retryDialogTitle;
    }

    public final void setLoadingDialog(LoadingDialog loadingDialog) {
        this.loadingDialog = loadingDialog;
    }

    @CallSuper
    @Override
    public void onNetSceneEnd() {
        if(loadingDialog != null) {
            loadingDialog.dismiss();
        }

        if(dialog != null) {
            dialog.dismiss();
            dialog.switchView();
            dialog.setRetryBtnEnable(true);
        }
    }

    @Override
    public void onServerConnectIssue(IRequest request) {
        retry(request);
    }

    @Override
    public void onGetBadResponse(IRequest request) {
        retry(request);
    }

    private void retry(IRequest request) {
        if(ApplicationContext.getCurrentContext() == null || !(ApplicationContext.getCurrentContext() instanceof Activity)) {
            Log.w(TAG, "ApplicationContext.getCurrentContext() is null");
            //ToastUtil.show(R.string.server_network_error);
            return;
        }

        if(dialog == null) {
            dialog = new RetryDialog(ApplicationContext.getCurrentContext(), (NetSceneBase)request);
            if(request instanceof NetSceneLoadingWrapper) {
                NetSceneLoadingWrapper wrapper = (NetSceneLoadingWrapper) request;
                wrapper.setShowLoading(false);
            }
        }

        if(!Util.isNull(title)) {
            dialog.mBinding.titleTv.setText(title);
        }

        if(dialog.isLoadingView()) {
            dialog.switchView();
        }
        dialog.setRetryBtnEnable(true);
        dialog.show();
    }
}
