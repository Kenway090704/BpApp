package com.bemetoy.bp.ui;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CompoundButton;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.events.AuthResultEvent;
import com.bemetoy.bp.databinding.UiAccountLoginBinding;
import com.bemetoy.bp.sdk.arithmetic.MD5;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.utils.KeyBoardUtil;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.ui.auth.model.RegisterLogic;
import com.bemetoy.bp.uikit.BaseDataBindingFragment;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.network.auth.NetSceneAlphaLogin;
import com.bemetoy.stub.ui.LoadingDialog;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Tom on 2016/6/17.
 */
public class AccountLoginFragment extends BaseDataBindingFragment<UiAccountLoginBinding> {

    private static final String TAG = "App.AccountLoginFragment";
    private EventObserver eventObserver = null;
    private LoadingDialog loadingDialog = null;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();


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
                mBinding.loginBtn.setEnabled(true);
                loadingDialog.dismiss();
            }
        };
        RxEventBus.getBus().register(AuthResultEvent.ID, eventObserver);

        mBinding.loginUsernameEd.addTextChangedListener(new TextWatcher() {
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

        mBinding.clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.loginUsernameEd.setText("");
            }
        });

        mBinding.pwdTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBinding.passwordEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mBinding.passwordEd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        Subscription subscription = RxView.clicks(mBinding.loginBtn).throttleFirst(500, TimeUnit.MILLISECONDS).
                subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        doAccountLogin();
                        KeyBoardUtil.hideSoftKeyboard(mBinding.loginBtn);
                    }
                });
        compositeSubscription.add(subscription);
    }

    private void doAccountLogin() {
        String alphaAccount = mBinding.loginUsernameEd.getText().toString();
        if(Util.isNullOrBlank(alphaAccount)) {
            ToastUtil.show(R.string.account_empty_error);
            return;
        }

        String password = mBinding.passwordEd.getText().toString();
        if (RegisterLogic.validatePWD(password)) {
            loadingDialog.show();
            String pwdMD5 = MD5.getMessageDigest(mBinding.passwordEd.getText().toString().getBytes());
            NetSceneAlphaLogin netSceneAlphaLogin = new NetSceneAlphaLogin(alphaAccount, pwdMD5, null);
            netSceneAlphaLogin.doScene();
            mBinding.loginBtn.setEnabled(false);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_account_login;
    }

    @Override
    public void onDestroyView() {
        RxEventBus.getBus().unregister(AuthResultEvent.ID, eventObserver);
        compositeSubscription.unsubscribe();
        super.onDestroyView();
    }
}
