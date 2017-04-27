package com.bemetoy.stub.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.stub.databinding.UiRetryDialogBinding;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.stub.network.NetSceneBase;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by Tom on 2016/7/10.
 */
public class RetryDialog2 extends BpDialog<UiRetryDialogBinding>{

    private ObjectAnimator mAnimatorRotate;
    private boolean isLoadingView;

    public RetryDialog2(Context context, final Runnable netScene) {
        super(context, R.layout.ui_retry_dialog);
        this.mBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCancelable(true);

        this.mBinding.retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(netScene != null) {
                    netScene.run();
                    switchView();
                    setRetryBtnEnable(false);
                }
            }
        });

        mAnimatorRotate = ObjectAnimator.ofFloat(mBinding.loadingIm, "rotation", 0.0f,359.0f);
        mAnimatorRotate.setRepeatMode(Animation.RESTART);
        mAnimatorRotate.setInterpolator(new LinearInterpolator());
        mAnimatorRotate.setRepeatCount(-1);
        mAnimatorRotate.setDuration(1200);
    }

    public void switchView() {
        if(mBinding.loadingView.getVisibility() == View.INVISIBLE) {
            mBinding.loadingView.setVisibility(View.VISIBLE);
            mBinding.retryView.setVisibility(View.INVISIBLE);
            isLoadingView = true;
            mAnimatorRotate.start();
        } else {
            mBinding.loadingView.setVisibility(View.INVISIBLE);
            mBinding.retryView.setVisibility(View.VISIBLE);
            isLoadingView = false;
            mAnimatorRotate.cancel();
        }
    }

    public void setRetryBtnEnable(boolean enable) {
        mBinding.retryBtn.setEnabled(enable);
    }

    public boolean isLoadingView() {
        return isLoadingView;
    }

}
