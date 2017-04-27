package com.bemetoy.bp.plugin.personalcenter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.events.AccountUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiPersonalCenterNewBinding;
import com.bemetoy.bp.plugin.personalcenter.model.PersonalCenterLogic;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.plugin.imageloader.ImageLoader;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.stub.account.AccountLogic;

/**
 * Created by albieliang on 16/4/17.
 */
public class PersonalCenterUI extends BaseDataBindingActivity<UiPersonalCenterNewBinding> {

    private static final String TAG = "PersonalCenter.PersonalCenterUI";
    private Racecar.AccountInfo mAccountInfo = null;
    private EventObserver eventObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_personal_center_new;
    }

    protected void init() {
        eventObserver = new AccurateEventObserver<AccountUpdateEvent>() {
            @Override
            public void onReceiveEvent(AccountUpdateEvent event) {
                refresh();
            }
        };
        RxEventBus.getBus().register(AccountUpdateEvent.ID, eventObserver);

        refresh();
        mBinding.rankPercentTv.setText((mAccountInfo.getRankPercent() / 100f) + "%");
        ImageLoader.load(AccountLogic.getAvatarImageUrl(mAccountInfo.getIcon()), mBinding.userAvatarIm);

        mBinding.nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalCenterUI.this, EditNicknameUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.USER_NAME, mAccountInfo.getName());
                startActivityForResult(intent, ProtocolConstants.RequestCode.ACTION);
            }
        });


        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.avatarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalCenterUI.this, ChooseAvatarUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.USER_AVATAR, AccountLogic.getAvatarImageUrl(mAccountInfo.getIcon()));
                intent.putExtra(ProtocolConstants.IntentParams.USER_ID, mAccountInfo.getUserId());
                startActivityForResult(intent, ProtocolConstants.RequestCode.ACTION_EDIT);
            }
        });
        mBinding.myAddressV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddressListUI.class));
            }
        });
        mBinding.myGamesV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalCenterUI.this, GamePerformanceUI.class));
            }
        });
        mBinding.myAchievementV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalCenterUI.this, AchievementUI.class));
            }
        });

        mBinding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalCenterLogic.showLogoutDialog(PersonalCenterUI.this);
            }
        });

    }

    private void refresh() {
        mAccountInfo = AccountLogic.getAccountInfo();
        mBinding.setUserInfo(mAccountInfo);
    }

    @Override
    protected void onDestroy() {
        RxEventBus.getBus().unregister(AccountUpdateEvent.ID, eventObserver);
        super.onDestroy();
    }

    /**
     * hanlde the user edit nickname result.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProtocolConstants.RequestCode.ACTION && resultCode == RESULT_OK) {
            String newName = data.getStringExtra(ProtocolConstants.IntentParams.USER_NAME);
            mAccountInfo = Racecar.AccountInfo.newBuilder().mergeFrom(mAccountInfo).setName(newName).build();
            mBinding.setUserInfo(mAccountInfo);
        }

        if (requestCode == ProtocolConstants.RequestCode.ACTION_EDIT && resultCode == RESULT_OK) {
            String userAvatar = data.getStringExtra(ProtocolConstants.IntentParams.USER_AVATAR);
            Log.i(TAG, "new user avatar is %s", userAvatar);
            ImageLoader.load(AccountLogic.getAvatarImageUrl(userAvatar), mBinding.userAvatarIm);
        }
    }
}
