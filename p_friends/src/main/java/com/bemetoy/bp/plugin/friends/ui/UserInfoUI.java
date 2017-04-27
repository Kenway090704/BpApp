package com.bemetoy.bp.plugin.friends.ui;

import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.friends.R;
import com.bemetoy.bp.plugin.friends.databinding.UiUserInformationBinding;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.ui.BPDialogManager;

/**
 * Created by Tom on 2016/4/29.
 */
public class UserInfoUI extends BaseDataBindingActivity<UiUserInformationBinding> {

    private static final String TAG = "Friends.SearchFriendUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        final Racecar.AccountInfo userInfo = (Racecar.AccountInfo) getIntent().getExtras().getSerializable(ProtocolConstants.IntentParams.USER_INFO);
        if(userInfo == null) {
            return;
        }
        mBinding.setUserInfo(userInfo);
        mBinding.actionBtn.setOnClickListener(new FriendOperationListener(userInfo));
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_user_information;
    }

    private final class FriendOperationListener implements View.OnClickListener {
        private Racecar.AccountInfo user;
        public FriendOperationListener(Racecar.AccountInfo user) {
            this.user = user;
        }

        @Override
        public void onClick(View v) {
            BPDialogManager.showAddFriendsDialog(UserInfoUI.this, user.getName(), user.getUserId(),
                    AccountLogic.getAccountInfo().getName(), AccountLogic.getAccountInfo().getUserId(), new Runnable() {
                        @Override
                        public void run() {
                            ThreadPool.postToMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000);
                        }
                    });
        }
    }
}
