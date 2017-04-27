package com.bemetoy.stub.network;

import android.app.Activity;
import android.content.DialogInterface;

import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.stub.ui.LoadingDialog;

/**
 * Created by Tom on 2016/7/11.
 */
public class NetSceneLoadingWrapper extends NetSceneBase{

    public static final String TAG = "network.NetSceneLoadingWrapper";

    private LoadingDialog loadingDialog;
    private NetSceneBase actualNetScene;
    private boolean showLoading = true;

    public NetSceneLoadingWrapper(NetSceneBase netSceneBase) {
        super(netSceneBase.getCmdId(), netSceneBase.getResponseCallBack());
        actualNetScene = netSceneBase;
        if(netSceneBase.getResponseCallBack() == null) {
            throw new NullPointerException("netSceneBase.getResponseCallBack() should not be null");
        }

        if(!(netSceneBase.getResponseCallBack() instanceof NetSceneCallbackLoadingWrapper)) {
            throw new IllegalArgumentException("netSceneBase.getResponseCallBack() should be the subclass of NetSceneCallbackLoadingWrapper");
        }
    }

    public NetSceneBase getActualNetScene() {
        return actualNetScene;
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
    }

    @Override
    public byte[] getRequestBody() {
        return actualNetScene.getRequestBody();
    }

    @Override
    public int getCmdId() {
        return actualNetScene.getCmdId();
    }

    @Override
    public byte[] getAesKey() {
        return actualNetScene.getAesKey();
    }

    @Override
    public byte[] getSessionKey() {
        return actualNetScene.getSessionKey();
    }

    @Override
    public ResponseCallBack getResponseCallBack() {
        return actualNetScene.getResponseCallBack();
    }

    @Override
    public void doScene() {
        if(showLoading) {
            if(ApplicationContext.getCurrentContext() == null || !(ApplicationContext.getCurrentContext() instanceof Activity)) {
                Log.w(TAG, "ApplicationContext.getCurrentContext() is null");
                return;
            }

            if(loadingDialog == null) {
                loadingDialog = new LoadingDialog(ApplicationContext.getCurrentContext());
            }

            if(getResponseCallBack() != null && getResponseCallBack() instanceof NetSceneCallbackLoadingWrapper) {
                NetSceneCallbackLoadingWrapper wrapper = (NetSceneCallbackLoadingWrapper)getResponseCallBack();
                wrapper.setLoadingDialog(loadingDialog);
            }
            loadingDialog.setCancelable(true);
            loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel();
                }
            });

            loadingDialog.show();
        }
        super.doScene();
    }
}
