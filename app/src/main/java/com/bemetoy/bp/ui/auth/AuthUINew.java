package com.bemetoy.bp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.events.AuthResultEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.UiLoginDialogBinding;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.ui.AccountLoginFragment;
import com.bemetoy.bp.ui.ChangePasswordUI;
import com.bemetoy.bp.ui.DefaultFragmentAdapter;
import com.bemetoy.bp.ui.QuickLoginFragment;
import com.bemetoy.bp.ui.auth.model.RegisterLogic;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/5/12.
 */
public class AuthUINew extends BaseDataBindingActivity<UiLoginDialogBinding> {

    private static final String TAG = "App.AuthUINew";
    private EventObserver mEventObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {

        List<Fragment> fragmentList = new ArrayList();
        fragmentList.add(new AccountLoginFragment());
        fragmentList.add(new QuickLoginFragment());
        DefaultFragmentAdapter defaultFragmentAdapter = new DefaultFragmentAdapter(getSupportFragmentManager(),  fragmentList);
        mBinding.loginMethodVw.setOffscreenPageLimit(2);
        mBinding.loginMethodVw.setAdapter(defaultFragmentAdapter);
        mBinding.loginMethod.setViewPager(mBinding.loginMethodVw);


        mBinding.freeRegisterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthUINew.this, RegisterUINEW.class));
                finish();
            }
        });

        mBinding.forgetPswTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthUINew.this, ChangePasswordUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.URL_INFO, ProtocolConstants.ALPHA_FORGET_PWD);
                startActivity(intent);
            }
        });

        mBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEventObserver = new AccurateEventObserver<AuthResultEvent>() {

            @Override
            public void onReceiveEvent(AuthResultEvent event) {
                AuthResultEvent.Result result = event.result;
                switch (result.errorCode) {
                    case Racecar.AofeiLoginResponse.ErrorCode.ACCOUNT_ERROR_VALUE:
                    case Racecar.AofeiLoginResponse.ErrorCode.PASSWD_ERROR_VALUE:
                    case Racecar.AofeiLoginResponse.ErrorCode.DISALLOW_LOGIN_VALUE:
                    case Racecar.AofeiLoginResponse.ErrorCode.ACCOUNT_INVALID_VALUE:
                    case Racecar.AofeiLoginResponse.ErrorCode.PHONE_VERIFY_ERROR_VALUE:
                        RegisterLogic.showErrorNotice(AuthUINew.this ,event.result.errorMessage);
                        break;
                    case Racecar.ErrorCode.ERROR_OK_VALUE:
                        if(event.result.needFill) {
                            Bundle bundle = new Bundle();
                            bundle.putString(ProtocolConstants.IntentParams.LOCATION_PROVINCE, event.result.province);
                            bundle.putString(ProtocolConstants.IntentParams.LOCATION_CITY, event.result.city);
                            bundle.putString(ProtocolConstants.IntentParams.LOCATION_DISTRICT, event.result.district);
                            PluginStubBus.doAction(AuthUINew.this, PluginConstants.Plugin.PLUGIN_NAME_APP,
                                    PluginConstants.App.Action.CMD_START_LOCATION_CHOOSE_UI, Intent.FLAG_ACTIVITY_CLEAR_TASK, bundle);
                        } else {
                            int flag = Intent.FLAG_ACTIVITY_CLEAR_TASK;
                            flag |= Intent.FLAG_ACTIVITY_NEW_TASK;
                            PluginStubBus.doAction(AuthUINew.this, PluginConstants.Plugin.PLUGIN_NAME_APP,
                                    PluginConstants.App.Action.CMD_START_HOME_PAGE, flag, null);
                        }
                        finish();
                        break;
                    case ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR:
                        RegisterLogic.showErrorNotice(AuthUINew.this ,R.string.local_network_error);
                        break;
                    case ProtocolConstants.ErrorType.NETWORK_SVR_ERROR:
                        RegisterLogic.showErrorNotice(AuthUINew.this ,R.string.server_internal);
                        break;
                }
            }
        };
        RxEventBus.getBus().register(AuthResultEvent.ID, mEventObserver);
    }




    @Override
    protected void onDestroy() {
        RxEventBus.getBus().unregister(AuthResultEvent.ID, mEventObserver);
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_login_dialog;
    }
}
