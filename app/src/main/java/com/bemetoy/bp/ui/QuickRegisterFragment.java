package com.bemetoy.bp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.UiPhoneRegisterBinding;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.arithmetic.MD5;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.KeyBoardUtil;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.ui.auth.model.RegisterLogic;
import com.bemetoy.bp.uikit.BaseDataBindingFragment;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.network.auth.NetSceneAlphaLogin;
import com.bemetoy.stub.network.auth.NetSceneAlphaRegister;
import com.bemetoy.stub.network.auth.NetSceneGetPhoneVerifyCode;
import com.bemetoy.stub.ui.LoadingDialog;

/**
 * Created by Tom on 2016/6/17.
 */
public class QuickRegisterFragment extends BaseDataBindingFragment<UiPhoneRegisterBinding> {

    private static final String TAG = "App.QuickRegisterFragment";

    private String mCurrentCode;

    private CountDownTimer timer;

    @Override
    public void init() {
        mBinding.clearPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.phoneEd.setText("");
            }
        });

        mBinding.phoneEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!"".equals(s.toString())) {
                    mBinding.clearPhoneBtn.setVisibility(View.VISIBLE);
                } else {
                    mBinding.clearPhoneBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.phonePwdTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mBinding.phonePwdEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mBinding.phonePwdEd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        mBinding.codeTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVerifySms();
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
                doPhoneRegister();
                KeyBoardUtil.hideSoftKeyboard(v);
            }
        });
    }

    /**
     * Get the phone verify code. And Before get the phone verify code
     * the user must input the input
     */
    private void getVerifySms(){

        String verifyCode = mBinding.imageCodeEd.getText().toString().toUpperCase();
        if(Util.isNullOrNil(verifyCode)) {
            ToastUtil.show("请输入页面验证码");
            return;
        }

        if(mCurrentCode == null) {
            ToastUtil.show(R.string.refresh_verify_code);
            return;
        }

        if(!verifyCode.equals(mCurrentCode.toUpperCase())) {
            ToastUtil.show("页面验证码错误");
            return;
        }

        String phone = mBinding.phoneEd.getText().toString();
        if(!RegisterLogic.validateAccount(ProtocolConstants.LOGIN_METHOD.PHONE_LOGIN, phone)) {
            return;
        }

        NetSceneGetPhoneVerifyCode getVerifyCode = new NetSceneGetPhoneVerifyCode(phone, ProtocolConstants.VerifyCode.REGISTER_SMS, new NetSceneResponseCallback<Racecar.GetPhoneVerifyResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.GetPhoneVerifyResponse response) {
                if (response.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                        Log.v(TAG, "verify code is %s", mCurrentCode);
                        mBinding.codeTimeTv.setEnabled(false);
                        timer = new CountDownTimer(2 * 60 * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                mBinding.codeTimeTv.setText(getString(R.string.code_time, millisUntilFinished / 1000));
                            }

                            @Override
                            public void onFinish() {
                                mBinding.codeTimeTv.setText(R.string.get_verify_code);
                                mBinding.codeTimeTv.setEnabled(true);
                            }
                        };
                        timer.start();// 开始计时
                        ToastUtil.show(R.string.get_verify_code_success);
                } else {
                    ToastUtil.show(response.getPrimaryResp().getErrorMsg());
                }
            }
        });

        getVerifyCode.doScene();
    }


    private void doPhoneRegister() {

        final String phoneNum = mBinding.phoneEd.getText().toString();
        final String password = mBinding.phonePwdEd.getText().toString();
        if(!RegisterLogic.validateAccount(ProtocolConstants.LOGIN_METHOD.PHONE_LOGIN, phoneNum)) {
            return;
        }


        String imageVerifyCode = mBinding.imageCodeEd.getText().toString().toUpperCase();
        if(Util.isNullOrNil(imageVerifyCode)) {
            ToastUtil.show("请输入页面验证码");
            return;
        }

        if(mCurrentCode == null) {
            ToastUtil.show(R.string.image_verify_code_error);
            return;
        }

        String verifyCode = mBinding.imageCodeEd.getText().toString().toUpperCase();
        if(!verifyCode.equals(mCurrentCode.toUpperCase())) {
            ToastUtil.show(R.string.image_verify_code_error);
            return;
        }

        verifyCode = mBinding.phoneCodeEd.getText().toString();
        if(Util.isNullOrNil(verifyCode)) {
            ToastUtil.show("请输入短信验证码");
            return;
        }

        if(!RegisterLogic.validatePWD(password)) {
            return;
        }


        final String pwdMD5 = MD5.getMessageDigest(password.getBytes());
        final LoadingDialog dialog = new LoadingDialog(getActivity());
        dialog.setCancelable(false);
        dialog.show();
        NetSceneAlphaRegister register = new NetSceneAlphaRegister(phoneNum, pwdMD5,
                verifyCode, new NetSceneResponseCallback<Racecar.AofeiRegistResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.AofeiRegistResponse result) {

                switch (result.getPrimaryResp().getResult()) {
                    case Racecar.ErrorCode.ERROR_OK_VALUE:
                        int flag = Intent.FLAG_ACTIVITY_NEW_TASK;
                        flag |= Intent.FLAG_ACTIVITY_CLEAR_TASK;
                        NetSceneAlphaLogin login = new NetSceneAlphaLogin(phoneNum, pwdMD5, null);
                        login.doScene();
                        PluginStubBus.doAction(getActivity(), PluginConstants.Plugin.PLUGIN_NAME_APP,
                                PluginConstants.App.Action.CMD_START_LOCATION_CHOOSE_UI, flag, null);
                        break;
                    case Racecar.AofeiRegistResponse.ErrorCode.ACCOUNT_EXIST_VALUE:
                        RegisterLogic.showErrorNotice(getActivity(), R.string.account_exist_error);
                        break;
                    case Racecar.AofeiRegistResponse.ErrorCode.ACCOUNT_INVALID_VALUE:
                        RegisterLogic.showErrorNotice(getActivity(), R.string.account_format_error);
                        break;
                    case Racecar.AofeiRegistResponse.ErrorCode.PHONE_VERIFY_ERROR_VALUE:
                        RegisterLogic.showErrorNotice(getActivity(), R.string.verify_code_error);
                        break;
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
    public void onDetach() {
        if(timer != null) {
            timer.cancel();
        }
        super.onDetach();
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_phone_register;
    }
}
