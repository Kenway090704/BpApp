package com.bemetoy.bp.ui;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.events.AuthResultEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.UiPhoneLoginBinding;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.KeyBoardUtil;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.ui.auth.model.RegisterLogic;
import com.bemetoy.bp.uikit.BaseDataBindingFragment;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.network.auth.NetSceneAlphaLogin;
import com.bemetoy.stub.network.auth.NetSceneGetPhoneVerifyCode;
import com.bemetoy.stub.ui.LoadingDialog;

/**
 * Created by Tom on 2016/6/17.
 */
public class QuickLoginFragment extends BaseDataBindingFragment<UiPhoneLoginBinding> {

    private static final String TAG = "App.QuickLoginFragment";

    private String mCurrentCode;

    private LoadingDialog loadingDialog = null;
    private EventObserver eventObserver = null;

    private CountDownTimer timer;

    @Override
    public void init() {
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.setDismissCallback(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mBinding.loginBtn.setEnabled(true);
            }
        });
        eventObserver = new AccurateEventObserver<AuthResultEvent>() {
            @Override
            public void onReceiveEvent(AuthResultEvent event) {
                loadingDialog.dismiss();
                mBinding.loginBtn.setEnabled(true);
            }
        };
        RxEventBus.getBus().register(AuthResultEvent.ID, eventObserver);

        mBinding.imageCodeIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterLogic.loadImageVerifyCode(new RegisterLogic.OnLoadImageVerifyCode() {
                    @Override
                    public void onGetImageCode(String imgUrl, String code) {
                        mBinding.imageCodeIm.setImageURI(Uri.parse(imgUrl));
                        mCurrentCode = code;
                    }
                });
            }
        });
        mBinding.imageCodeIm.performClick();

        mBinding.codeTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVerifySms();
            }
        });

        mBinding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doQuickLogin();
                KeyBoardUtil.hideSoftKeyboard(mBinding.loginBtn);
            }
        });
        mBinding.codeTimeTv.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = mBinding.codeTimeTv.getLayoutParams();
                params.height = mBinding.codeEd.getHeight();
                mBinding.codeTimeTv.setLayoutParams(params);
                mBinding.codeTimeTv.invalidate();
            }
        }, 10);
    }


    private void getVerifySms() {

        String verifyCode = mBinding.imageCodeEd.getText().toString().toUpperCase();

        if (Util.isNullOrNil(verifyCode)) {
            ToastUtil.show("请输入页面验证码");
            return;
        }

        if (mCurrentCode == null) {
            ToastUtil.show(R.string.refresh_verify_code);
            return;
        }

        if (!verifyCode.equals(mCurrentCode.toUpperCase())) {
            ToastUtil.show("页面验证码错误");
            return;
        }


        String phone = mBinding.phoneEd.getText().toString();
        if (!RegisterLogic.validateAccount(ProtocolConstants.LOGIN_METHOD.PHONE_LOGIN, phone)) {
            return;
        }

        NetSceneGetPhoneVerifyCode getVerifyCode = new NetSceneGetPhoneVerifyCode(phone, ProtocolConstants.VerifyCode.LOGIN_SMS, new NetSceneResponseCallback<Racecar.GetPhoneVerifyResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.GetPhoneVerifyResponse response) {
                if (response.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    Log.v(TAG, "verify code is %s", mCurrentCode);
                    mBinding.codeTimeTv.setEnabled(false);
                    timer = new CountDownTimer(2 * 60 * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if(isAdded()) {
                                mBinding.codeTimeTv.setText(getString(R.string.code_time, millisUntilFinished / 1000));
                            }
                        }

                        @Override
                        public void onFinish() {
                            if(isAdded()) {
                                mBinding.codeTimeTv.setText(R.string.get_verify_code);
                                mBinding.codeTimeTv.setEnabled(true);
                            }
                        }
                    };
                    timer.start();// 开始计时
                    ToastUtil.show(R.string.get_verify_code_success);
                } else {
                    if(response.getPrimaryResp().hasErrorMsg() && getActivity() != null) {
                        RegisterLogic.showErrorNotice(getActivity(), response.getPrimaryResp().getErrorMsg());
                    } else {
                        ToastUtil.show("发送短信失败");
                    }
                }
            }
        });

        getVerifyCode.doScene();
    }

    private void doQuickLogin() {

        String alphaAccount = mBinding.phoneEd.getText().toString();
        if (!RegisterLogic.validateAccount(ProtocolConstants.LOGIN_METHOD.PHONE_LOGIN, alphaAccount)) {
            return;
        }

        String imageVerifyCode = mBinding.imageCodeEd.getText().toString().toUpperCase();
        if (Util.isNullOrBlank(imageVerifyCode)) {
            ToastUtil.show("请输入页面验证码");
            return;
        }

        if(mCurrentCode == null) {
            ToastUtil.show(R.string.image_verify_code_error);
            return;
        }

        if(!imageVerifyCode.equals(mCurrentCode.toUpperCase())) {
            ToastUtil.show(R.string.image_verify_code_error);
            return;
        }

        String verifyCode = mBinding.codeEd.getText().toString();
        if (Util.isNullOrBlank(verifyCode)) {
            ToastUtil.show("请输入短信验证码");
            return;
        }

        loadingDialog.show();
        mBinding.loginBtn.setEnabled(false);

        NetSceneAlphaLogin netSceneAlphaLogin = new NetSceneAlphaLogin(alphaAccount, null,
                verifyCode);
        netSceneAlphaLogin.doScene();
    }

    @Override
    public void onDestroyView() {
        RxEventBus.getBus().unregister(AuthResultEvent.ID, eventObserver);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDetach();
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_phone_login;
    }
}
