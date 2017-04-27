package com.bemetoy.bp.uikit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by Tom on 2016/3/11.
 */
public class BpAlertDialog {

    private static final String TAG = "uikit.BpAlertDialog";

    protected Context mContext;
    protected AlertDialog mDialog;
    protected View mContentView;
    protected boolean mHasSetContent;
    private boolean mIsShown;

    public BpAlertDialog(Context context) {
        mContext = context;
        mHasSetContent = false;
        initView();
    }

    public void setContentView(int layoutID) {
        View view = LayoutInflater.from(mContext).inflate(layoutID, null);
        setContentView(view);
    }

    public void setContentView(View view) {
        mContentView = view;
        mHasSetContent = true;
    }

    public View getContentView() {
        return mContentView;
    }


    protected void initView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mDialog.getWindow().setBackgroundDrawableResource(R.color.translucent);
    }

    public boolean isShown() {
        return mIsShown;
    }

    public void show() {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (activity.isFinishing()) {
                return;
            }
        }

        mDialog.show();
        mIsShown = true;
        if ((mHasSetContent) && (null != mContentView)) {
            Window window = mDialog.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            if (null != window) {
                window.setGravity(Gravity.CENTER);
                window.setContentView(mContentView);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                TypedValue outValue = new TypedValue();
                mContext.getResources().getValue(R.dimen.dialog_width_rate, outValue, true);
                window.setLayout((int) (displayMetrics.widthPixels * outValue.getFloat()), WindowManager.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    public View findViewById(int viewId) {
        if (getContentView() == null) {
            return null;
        }
        return getContentView().findViewById(viewId);
    }

    public final Context getContext() {
        return mContext;
    }

    public void dismiss() {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (activity.isFinishing()) {
                return;
            }
        }

        if (mDialog.isShowing()) {
            mDialog.dismiss();
            mIsShown = false;
        }
    }

    public void setCancelable(boolean cancelable) {
        mDialog.setCancelable(cancelable);
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        mDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener cancelListener) {
        if (cancelListener != null) {
            mDialog.setOnCancelListener(cancelListener);
        }
    }


    public void setDismissCallback(DialogInterface.OnCancelListener listener) {
        if (listener != null) {
            mDialog.setOnCancelListener(listener);
        }

    }

}
