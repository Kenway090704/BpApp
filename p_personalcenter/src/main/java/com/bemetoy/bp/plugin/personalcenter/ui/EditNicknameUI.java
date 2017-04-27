package com.bemetoy.bp.plugin.personalcenter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.events.NicknameChangeEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiEditNicknameBinding;
import com.bemetoy.bp.plugin.personalcenter.model.NetSceneModifyAccountInfo;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.bp.uikit.widget.MixedWordCountTextWatcher;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.network.NetSceneBase;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.ui.BPDialogManager;
import com.bemetoy.stub.ui.LoadingDialog;

/**
 * Created by Tom on 2016/5/31.
 */
public class EditNicknameUI extends BaseDataBindingActivity<UiEditNicknameBinding> {

    private NetSceneBase netSceneModifyAccountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        final String username = getIntent().getStringExtra(ProtocolConstants.IntentParams.USER_NAME);
        mBinding.setContent(username);
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.usernameEd.addTextChangedListener(new MixedWordCountTextWatcher(mBinding.usernameEd, null, ProtocolConstants.Register.NICKNAME_MAX_LENGTH));
        mBinding.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nickname = mBinding.usernameEd.getText().toString().trim();
                // if the username do not change, finish directly
                if(nickname.equals(username)) {
                    finish();
                }

                if(!validateNickname(nickname)) {
                    return;
                }
                final LoadingDialog loadingDialog = new LoadingDialog(EditNicknameUI.this);
                loadingDialog.show();
                final Racecar.AccountInfo accountInfo = Racecar.AccountInfo.newBuilder().setName(nickname).build();
                netSceneModifyAccountInfo = new NetSceneModifyAccountInfo(accountInfo, new NetSceneCallbackLoadingWrapper<Racecar.ModifyAccountInfoResponse>() {
                    @Override
                    public void onResponse(IRequest request, Racecar.ModifyAccountInfoResponse result) {
                        if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                            AccountLogic.updateAccountInfo(StorageConstants.Info_Key.NAME, nickname);
                            Intent intent = new Intent();
                            intent.putExtra(ProtocolConstants.IntentParams.USER_NAME, nickname);
                            setResult(RESULT_OK, intent);
                            finish();
                            RxEventBus.getBus().publish(new NicknameChangeEvent());
                        } else {
                            BPDialogManager.showSorryDialog(EditNicknameUI.this, result.getPrimaryResp().getErrorMsg());
                        }
                    }

                    @Override
                    public void onNetSceneEnd() {
                        loadingDialog.dismiss();
                        super.onNetSceneEnd();
                    }
                });
              netSceneModifyAccountInfo.doScene();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(netSceneModifyAccountInfo != null) {
            netSceneModifyAccountInfo.cancel();
        }
        super.onDestroy();
    }

    private boolean validateNickname(String nickname) {
        if(Util.isNullOrNil(nickname)) {
            BPDialogManager.showSorryDialog(this, getString(R.string.personal_center_edit_name_empty));
            return false;
        }

        if(nickname.length() > ProtocolConstants.Register.NICKNAME_MAX_LENGTH) {
            ToastUtil.show(R.string.register_nickname_hint);
            return false;
        }

        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_edit_nickname;
    }
}
