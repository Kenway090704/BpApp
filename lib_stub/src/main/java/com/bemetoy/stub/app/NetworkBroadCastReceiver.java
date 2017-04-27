package com.bemetoy.stub.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.utils.NetWorkUtil;
import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.uikit.ToastUtil;

/**
 * Created by Tom on 2016/6/22.
 */
public class NetworkBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if(ApplicationContext.getCurrentContext() == null || !(ApplicationContext.getCurrentContext() instanceof Activity)) {
//            ToastUtil.show(R.string.local_network_error);
//            return;
//        }
//        NetworkIssueDialog dialog = new NetworkIssueDialog(ApplicationContext.getCurrentContext());
//        dialog.show();

        if(!NetWorkUtil.isConnected(ApplicationContext.getApplication())) {
            ToastUtil.show(R.string.local_network_error);
        }
    }
}
