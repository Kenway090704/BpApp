package com.bemetoy.bp.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.UiAccountRegisterBinding;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.arithmetic.MD5;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.bp.sdk.utils.KeyBoardUtil;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.ui.auth.model.RegisterLogic;
import com.bemetoy.bp.uikit.BaseDataBindingFragment;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.network.NetSceneBase;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.network.auth.NetSceneAlphaLogin;
import com.bemetoy.stub.network.auth.NetSceneAlphaRegister;
import com.bemetoy.stub.ui.BPDialogManager;
import com.bemetoy.stub.ui.LoadingDialog;

import java.io.File;

/**
 * Created by Tom on 2016/6/17.
 */
public class AccountRegisterFragment extends BaseDataBindingFragment<UiAccountRegisterBinding> {

    private static final String TAG = "App.AccountRegisterFragment";

    private String mCurrentCode;

    @Override
    public void init() {

        mBinding.clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.accountEd.setText("");
            }
        });

        mBinding.accountEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!"".equals(s.toString())) {
                    mBinding.clearBtn.setVisibility(View.VISIBLE);
                } else {
                    mBinding.clearBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.accountEd.addOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateAccount(mBinding.accountEd.getText().toString());
                }
            }
        });


        mBinding.accountPwdTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBinding.passwordEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mBinding.passwordEd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        mBinding.registerCodeIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterLogic.loadImageVerifyCode(new RegisterLogic.OnLoadImageVerifyCode() {
                    @Override
                    public void onGetImageCode(String imgUrl, String code) {
                        mBinding.registerCodeIm.setImageURI(Uri.parse(imgUrl));
                        mCurrentCode = code;
                    }
                });
            }
        });

        mBinding.registerCodeIm.performClick();

        mBinding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doNormalRegister();
                KeyBoardUtil.hideSoftKeyboard(v);
            }
        });
    }

    private void validateAccount(String account) {
        if (!Util.isNullOrBlank(account)) {
            CheckAccountNetScene netScene = new CheckAccountNetScene(account, new NetSceneResponseCallback<Racecar.CheckAccountResponse>() {
                @Override
                public void onResponse(IRequest request, Racecar.CheckAccountResponse result) {
                    if (result.getPrimaryResp().getResult() != Racecar.ErrorCode.ERROR_OK_VALUE && isAdded() && getUserVisibleHint()) {
                        RegisterLogic.showErrorNotice(getActivity(), result.getPrimaryResp().getErrorMsg());
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(
                                InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                }

                @Override
                public void onLocalNetworkIssue(IRequest request) {

                }

                @Override
                public void onRequestFailed(IRequest request) {

                }
            });
            netScene.doScene();
        }
    }

    private void doNormalRegister() {
        final String username = mBinding.accountEd.getText().toString();
        final String password = mBinding.passwordEd.getText().toString();

        if (!RegisterLogic.validateAccount(ProtocolConstants.LOGIN_METHOD.ACCOUNT_LOGIN, username)) {
            return;
        }

        String verifyCode = mBinding.codeEd.getText().toString().toUpperCase();

        if (Util.isNullOrNil(verifyCode)) {
            ToastUtil.show("请输入页面验证码");
            return;
        }

        if (mCurrentCode == null) {
            ToastUtil.show(R.string.refresh_verify_code);
            return;
        }


        if (!verifyCode.equals(mCurrentCode.toUpperCase())) {
            ToastUtil.show(R.string.image_verify_code_error);
            return;
        }

        if (!RegisterLogic.validatePWD(password)) {
            return;
        }

        final LoadingDialog dialog = new LoadingDialog(getContext());
        dialog.show();
        final String passwordMD5 = MD5.getMessageDigest(password.getBytes());
        NetSceneAlphaRegister register = new NetSceneAlphaRegister(username,
                passwordMD5, null, new NetSceneResponseCallback<Racecar.AofeiRegistResponse>() {

            @Override
            public void onRequestFailed(IRequest request) {
                //TODO SHOW DIALOG
            }

            @Override
            public void onResponse(IRequest request, Racecar.AofeiRegistResponse result) {
                if (result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    FileUtils.createFileIfNeed(new File(StorageConstants.USER_DATA_PATH));
                    CfgFs cfgFs = new CfgFs(StorageConstants.USER_DATA_PATH);
                    cfgFs.setString(StorageConstants.Info_Key.USER_NAME, username);
                    cfgFs.setString(StorageConstants.Info_Key.MD5_PASSWORD, passwordMD5);
                    Log.d(TAG, "write user data success");
                    NetSceneAlphaLogin login = new NetSceneAlphaLogin(username, passwordMD5, null);
                    login.doScene();
                    int flag = Intent.FLAG_ACTIVITY_NEW_TASK;
                    flag |= Intent.FLAG_ACTIVITY_CLEAR_TASK;
                    PluginStubBus.doAction(getContext(), PluginConstants.Plugin.PLUGIN_NAME_APP,
                            PluginConstants.App.Action.CMD_START_LOCATION_CHOOSE_UI, flag, null);
                } else {
                    RegisterLogic.showErrorNotice(getContext(), result.getPrimaryResp().getErrorMsg());
                }
            }

            @Override
            public void onNetSceneEnd() {
                mBinding.registerBtn.setEnabled(true);
                dialog.dismiss();
            }
        });
        register.doScene();
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_account_register;
    }


    public static class CheckAccountNetScene extends NetSceneBase {
        private String accountName;
        private byte[] tempKey;

        public CheckAccountNetScene(String accountName, ResponseCallBack callBack) {
            super(Racecar.CmdId.CHECK_ACCOUNT_VALUE, callBack);
            this.accountName = accountName;
            this.tempKey = Util.geneAESKey();
        }

        @Override
        public byte[] getRequestBody() {
            Racecar.CheckAccountRequest.Builder builder = Racecar.CheckAccountRequest.newBuilder();
            builder.setPrimaryReq(builderBaseRequest());
            builder.setAccount(accountName);
            return builder.build().toByteArray();
        }

        @Override
        public byte[] getAesKey() {
            return tempKey;
        }
    }
}
