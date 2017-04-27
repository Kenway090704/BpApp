package com.bemetoy.bp.uikit;

import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by Tom on 2016/3/10.
 */
public class BpBottomAlertDialog extends BpAlertDialog{


    public BpBottomAlertDialog(Context context) {
        super(context);
    }

    public void show() {
        mDialog.show();

        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.translate_enter_from_bottom);
        if (null != mContentView) {
            mContentView.startAnimation(anim);
        }
        if ((mHasSetContent) && (null != mContentView)) {
            Window window = mDialog.getWindow();
            if (null != window) {
                window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
                window.setContentView(mContentView);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            }
        }
    }

    public void dismiss() {
        if (null != mContentView) {
            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.translate_out_of_bottom);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    mDialog.dismiss();
                }
            });
            mContentView.startAnimation(anim);
        } else {
            mDialog.dismiss();
        }
    }
}

