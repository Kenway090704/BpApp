package com.bemetoy.stub.ui;

import android.databinding.ViewDataBinding;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BaseDataBindingFragment;
import com.bemetoy.stub.network.NetSceneBase;

/**
 * Created by Tom on 2016/7/10.
 */
public abstract class RetryRequestFragment<ViewData extends ViewDataBinding> extends BaseDataBindingFragment<ViewData> {

    private static final String TAG = "Stub.RetryRequestFragment";

    private boolean shouldRetry = false;
    private RetryDialog dialog = null;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && shouldRetry) {
            retryRequest();
        }
    }

    /**
     * if the request failed , then should invoke this method ,set shouldRetry = true.
     * 重新请求
     * @param retry
     */
    protected void setShouldRetry(boolean retry) {
        this.shouldRetry = retry;
        Log.d(TAG, "fragment name : %s, isAdd() is %b, getUserVisibleHint() is %b, retry is %b",
                this.toString(), isAdded(), getUserVisibleHint(), retry);
        if(isAdded() && getUserVisibleHint() && retry) {
            retryRequest();
        }
    }

    /**
     * When the request finished, the method should be invoked.
     */
    public void hideRetryDialog() {
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * Show the retry dialog.
     */
    protected void retryRequest() {
        if(getActivity() == null) {
            return;
        }

        if(dialog == null) {
            dialog = new RetryDialog(getActivity(), getNetScene());
        }
        dialog.dismiss(); // dismiss the last one.
        if(dialog.isLoadingView()) {
            dialog.switchView();
            dialog.setRetryBtnEnable(true);
        }
        dialog.show();
        setShouldRetry(false);
    }

    @Override
    public void onDetach() {
        setShouldRetry(false);
        super.onDetach();
    }

    public abstract NetSceneBase getNetScene();
}
