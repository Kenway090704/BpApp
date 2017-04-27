package com.bemetoy.stub.ui;

import android.content.Context;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.stub.R;
import com.bemetoy.bp.stub.databinding.UiAddFriendsConfirmBinding;
import com.bemetoy.bp.stub.databinding.UiCongratulationDialogBinding;
import com.bemetoy.bp.stub.databinding.UiNoticeDialogBinding;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.network.NetSceneLoadingWrapper;
import com.bemetoy.stub.network.sync.NetSceneSyncJson;
import com.bemetoy.stub.util.JsonManager;

/**
 * Created by Tom on 2016/7/4.
 */
public class BPDialogManager {

    private static final String TAG = "Stub.BPDialogManager";

    /**
     * Show the Congratulation to the user.
     *
     * @param context
     * @param text
     * @param actionListener
     */
    public static void showCongratulationDialog(final Context context, final String text, final View.OnClickListener actionListener) {
        final BpDialog<UiCongratulationDialogBinding> dialogBindingBpDialog = new BpDialog(context, R.layout.ui_congratulation_dialog);
        dialogBindingBpDialog.mBinding.contentTv.setText(text);
        dialogBindingBpDialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBindingBpDialog.dismiss();
                if (actionListener != null) {
                    actionListener.onClick(v);
                }
            }
        });
        dialogBindingBpDialog.show();
    }

    /**
     * @see #showCongratulationDialog
     */
    public static void showCongratulationDialog(Context context, String text) {
        showCongratulationDialog(context, text, null);
    }

    public static void showNoticeDialog(Context context, String title, String content) {
        showNoticeDialog(context, title, content, null);
    }

    public static void showNoticeDialog(Context context, String title, String content, final Runnable callback) {

        if (context == null) {
            Log.e(TAG, "context is null");
            return;
        }

        final BpDialog<UiNoticeDialogBinding> dialogBindingBpDialog = new BpDialog(context, R.layout.ui_notice_dialog);
        dialogBindingBpDialog.mBinding.titleTv.setText(title);
        dialogBindingBpDialog.mBinding.contentTv.setText(content);
        dialogBindingBpDialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBindingBpDialog.dismiss();
                if(callback != null) {
                    callback.run();
                }
            }
        });
        dialogBindingBpDialog.setCancelable(true);
        dialogBindingBpDialog.show();
    }



    public static void showSorryDialog(Context context, String content) {
        showNoticeDialog(context, context.getString(R.string.sorry), content);
    }

    /**
     * show the add user dialog.
     * @param context
     * @param username
     * @param userId
     * @param myName
     * @param myId
     * @param callback the call after send the request successfully.
     */
    public static void showAddFriendsDialog(final Context context, final String username, final int userId, final String myName, final int myId, final Runnable callback) {

        if (context == null) {
            Log.e(TAG, "context is null");
            return;
        }

        final BpDialog<UiAddFriendsConfirmBinding> confirmDialog = new BpDialog(context, R.layout.ui_add_friends_confirm);
        confirmDialog.mBinding.setUsername(username);
        confirmDialog.mBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });

        confirmDialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
                String addFriendJson = JsonManager.createAddFriendJson(myId, myName, userId, "");
                NetSceneSyncJson sceneSyncJson = new NetSceneSyncJson(addFriendJson, new NetSceneCallbackLoadingWrapper<Racecar.SyncJsonResponse>() {
                    @Override
                    public void onResponse(IRequest request, Racecar.SyncJsonResponse result) {
                        if (result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                            String content = context.getString(R.string.friends_add_response_notice, username);
                            BPDialogManager.showNoticeDialog(context, context.getString(R.string.add_friend_title), content, callback);
                        } else {
                            ToastUtil.show(R.string.friend_add_request_failed);
                        }
                    }
                });
                new NetSceneLoadingWrapper(sceneSyncJson).doScene();
            }
        });
        confirmDialog.show();
    }
}
