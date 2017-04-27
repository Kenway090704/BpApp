package com.bemetoy.bp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.UiFinishRegisterDialogBinding;
import com.bemetoy.bp.databinding.UiRegisterDetailBinding;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.personalcenter.model.NetSceneModifyAccountInfo;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.ui.auth.model.RegisterLogic;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.bp.uikit.widget.MixedWordCountTextWatcher;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.ui.LoadingDialog;

/**
 * Created by Tom on 2016/4/11.
 */

public class RegisterDetailUI extends BaseDataBindingActivity<UiRegisterDetailBinding> {

    private static final String TAG = "ui.RegisterDetailUI";

    private String mProvince;
    private String mCity;
    private String mDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mProvince = getIntent().getStringExtra(ProtocolConstants.IntentParams.USER_LOCATION_PROVINCE);
        mCity = getIntent().getStringExtra(ProtocolConstants.IntentParams.USER_LOCATION_CITY);
        mDistrict = getIntent().getStringExtra(ProtocolConstants.IntentParams.USER_LOCATION_DISTRICT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_register_detail;
    }

    @Override
    protected void init() {
        String alphaName = AccountLogic.getAccountInfo().getName();
        if(!Util.isNullOrBlank(alphaName)) {
            mBinding.nickNameEt.setText(alphaName);
        }
        mBinding.nickNameEt.addTextChangedListener(new MixedWordCountTextWatcher(mBinding.nickNameEt, null, ProtocolConstants.Register.NICKNAME_MAX_LENGTH));
        mBinding.finishDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mBinding.nickNameEt.getText().toString();
                if(Util.isNullOrNil(username)) {
                    return;
                }

                if(username.length() > ProtocolConstants.Register.NICKNAME_MAX_LENGTH) {
                    ToastUtil.show(R.string.register_nickname_hint);
                    return;
                }

                final LoadingDialog loadingDialog = new LoadingDialog(RegisterDetailUI.this);
                loadingDialog.show();

                NetSceneModifyAccountInfo modifyAccountInfo = new NetSceneModifyAccountInfo(getAccountInfo(),
                        new NetSceneResponseCallback<Racecar.ModifyAccountInfoResponse>() {
                    @Override
                    public void onResponse(IRequest request, Racecar.ModifyAccountInfoResponse result) {
                        if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                            AccountLogic.updateAccountInfo(StorageConstants.Info_Key.PROVINCE, mProvince);
                            AccountLogic.updateAccountInfo(StorageConstants.Info_Key.CITY, mCity);
                            AccountLogic.updateAccountInfo(StorageConstants.Info_Key.DISTRICT, mDistrict);
                            AccountLogic.updateAccountInfo(StorageConstants.Info_Key.NAME, username);
                            showResultDialog();
                            Log.d(TAG,"update user info success");
                        } else {
                            RegisterLogic.showErrorNotice(RegisterDetailUI.this, result.getPrimaryResp().getErrorMsg());
                        }
                    }

                    @Override
                    public void onNetSceneEnd() {
                        loadingDialog.dismiss();
                    }
                });
                modifyAccountInfo.doScene();

            }
        });

        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showResultDialog() {
        BpDialog<UiFinishRegisterDialogBinding> dialog = new BpDialog(this, R.layout.ui_finish_register_dialog);
        dialog.mBinding.setName(mBinding.nickNameEt.getText().toString());
        StringBuilder sb = new StringBuilder();
        sb.append("注册地：");
        sb.append(mProvince);
        sb.append(" | ");
        sb.append(mCity);
        if(!Util.isNullOrBlank(mDistrict)) {
            sb.append(" | ");
            sb.append(mDistrict);
        }
        dialog.mBinding.setLocationString(sb.toString());
        dialog.mBinding.btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = Intent.FLAG_ACTIVITY_CLEAR_TASK;
                flag |= Intent.FLAG_ACTIVITY_NEW_TASK;
                PluginStubBus.doAction(RegisterDetailUI.this, PluginConstants.Plugin.PLUGIN_NAME_APP,
                        PluginConstants.App.Action.CMD_START_HOME_PAGE, flag, null);
            }
        });
        dialog.show();
    }


    private Racecar.AccountInfo getAccountInfo() {
        Racecar.AccountInfo.Builder builder = Racecar.AccountInfo.newBuilder();
        builder.setCity(mCity);
        builder.setProvince(mProvince);
        builder.setDistrict(mDistrict);
        builder.setName(mBinding.nickNameEt.getText().toString());
        return builder.build();
    }
}
