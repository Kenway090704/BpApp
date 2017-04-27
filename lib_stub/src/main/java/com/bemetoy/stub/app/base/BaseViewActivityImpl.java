package com.bemetoy.stub.app.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;

import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.ui.LoadingDialog;
import com.bemetoy.stub.ui.RetryDialog2;

/**
 * Created by tomliu on 16-9-7.
 */
public abstract class BaseViewActivityImpl<ViewData extends ViewDataBinding> extends BaseDataBindingActivity<ViewData>
        implements BaseView {

    private LoadingDialog loadingDialog = null;
    private boolean mDestroyed = true;
    private RetryDialog2 dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDestroyed = false;
    }

    @Override
    public boolean isActive() {
        return !mDestroyed;
    }

    @Override
    protected void onDestroy() {
        mDestroyed = true;
        super.onDestroy();
    }

    @Override
    public void showLoading(boolean showLoading) {
        if (showLoading) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(this);

            }
            loadingDialog.show();
        } else {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }

            if(dialog2 != null) {
                dialog2.dismiss();
            }
        }
    }

    @Override
    public void showNetworkConnectionError() {
        if(dialog2 == null) {
            dialog2 = new RetryDialog2(this, getRetryAction());
        }
        dialog2.dismiss(); // dismiss the last one.
        if(dialog2.isLoadingView()) {
            dialog2.switchView();
            dialog2.setRetryBtnEnable(true);
        }
        dialog2.show();
    }

    @Override
    public void showLocalNetworkError() {
        ToastUtil.show(R.string.local_network_error);
    }

    protected abstract Runnable getRetryAction();
}
