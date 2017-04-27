package com.bemetoy.stub.util;

import android.app.Activity;
import android.view.View;

import com.bemetoy.bp.sdk.utils.NetWorkUtil;
import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.stub.databinding.UiNetworkIssueDialogBinding;
import com.bemetoy.bp.uikit.BpDialog;

/**
 * Created by Tom on 2016/8/20.
 */
public class AppUtil {

    /**
     * Check the network and show the notice dialog.
     * @return
     */
    public static boolean checkNetworkFirst(Activity activity) {
        if (!NetWorkUtil.isConnected(activity)) {
            final BpDialog<UiNetworkIssueDialogBinding> dialog = new BpDialog(activity, R.layout.ui_network_issue_dialog);
            dialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return false;
        }
        return true;
    }


}
