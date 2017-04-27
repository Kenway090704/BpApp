package com.bemetoy.stub.app.base;

import android.databinding.ViewDataBinding;

import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.uikit.BaseDataBindingFragment;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.ui.LoadingDialog;
import com.bemetoy.stub.ui.RetryDialog2;

/**
 * Created by tomliu on 16-9-7.
 */
public abstract class BaseViewFragmentImpl<ViewData extends ViewDataBinding> extends BaseDataBindingFragment<ViewData> implements BaseView{

    private LoadingDialog loadingDialog;
    private RetryDialog2 dialog2;

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showLoading(boolean showLoading) {
        if (showLoading) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(getActivity());
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
            dialog2 = new RetryDialog2(getActivity(), getRetryAction());
        }
        dialog2.dismiss(); // dismiss the last one.
        if(dialog2.isLoadingView()) {
            dialog2.switchView();
            dialog2.setRetryBtnEnable(true);
        }
        dialog2.show();
    }

    protected abstract Runnable getRetryAction();

    @Override
    public void showLocalNetworkError() {
        ToastUtil.show(R.string.local_network_error);
    }

}
