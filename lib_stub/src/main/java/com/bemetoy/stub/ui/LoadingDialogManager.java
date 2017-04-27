package com.bemetoy.stub.ui;

import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by Tom on 2016/6/30.
 */
public class LoadingDialogManager {

    private static final String TAG = "Stub.LoadingDialogManager";

    private static LoadingDialog currentLoadingDialog;
    private static int currentSize;
    private static int totalSignalSize;

    public static void show(LoadingDialog loadingDialog, int signalSize) {
        if(currentLoadingDialog != null) {
            currentLoadingDialog.dismiss();
            currentLoadingDialog = null;
        }

        if(signalSize == 0) {
            Log.w(TAG, "signalSize is zero the load dialog request will be ignore");
            return;
        }

        currentLoadingDialog = loadingDialog;
        currentLoadingDialog.show();
        totalSignalSize = signalSize;
        currentSize = 0;
    }

    public static void countDown() {
        if(currentLoadingDialog == null) {
            Log.e(TAG, "currentLoadingDialog is null");
            return;
        }

        currentSize++;
        if(currentSize == totalSignalSize) {
            currentLoadingDialog.dismiss();
            currentLoadingDialog = null;
            currentSize = 0;
            totalSignalSize = 0;
        }
    }
}
