package com.bemetoy.bp.plugin.friends.ui;

import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.events.FriendsListUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.friends.R;
import com.bemetoy.bp.plugin.friends.databinding.UiDeleteFirendsConfirmBinding;
import com.bemetoy.bp.plugin.friends.databinding.UiUserInformationBinding;
import com.bemetoy.bp.plugin.friends.model.FriendLogic;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.bp.uikit.ToastUtil;

/**
 * Created by Tom on 2016/6/1.
 */
public class FriendDetailUI extends BaseDataBindingActivity<UiUserInformationBinding> {

    private static final String TAG = "Friends.FriendDetailUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        mBinding.actionBtn.setBackgroundResource(R.drawable.friends_delete_friend_drawable);
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Racecar.AccountInfo userInfo = (Racecar.AccountInfo) getIntent().getSerializableExtra(ProtocolConstants.IntentParams.USER_INFO);
        mBinding.setUserInfo(userInfo);
        mBinding.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BpDialog<UiDeleteFirendsConfirmBinding> dialog = new BpDialog(FriendDetailUI.this, R.layout.ui_delete_firends_confirm);
                dialog.setCancelable(true);
                dialog.mBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        FriendLogic.deleteFriend(userInfo.getUserId(), new FriendLogic.DeleteFriendCallback() {
                            @Override
                            public void onGetResponse(boolean result) {
                                if(result) {
                                    Log.i(TAG, "friend id %d has been deleted",userInfo.getUserId());
                                    mBinding.actionBtn.setEnabled(false);
                                    RxEventBus.getBus().publish(new FriendsListUpdateEvent());
                                    ThreadPool.postToMainThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setResult(ProtocolConstants.ResultCode.ACTION_DONE, getIntent());
                                            finish();
                                        }
                                    }, 1000);
                                } else {
                                    ToastUtil.show(R.string.friend_delete_friend_failed);
                                }
                            }
                        });
                    }
                });
                dialog.mBinding.errorMsgTv.setText(getString(R.string.friends_name_confirm, userInfo.getName()));
                dialog.show();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_user_information;
    }
}
