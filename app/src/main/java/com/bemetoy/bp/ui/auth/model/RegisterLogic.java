package com.bemetoy.bp.ui.auth.model;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.UiAlphaNoticeBinding;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.network.auth.NetSceneGetImageVerify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tom on 2016/5/16.
 */
public class RegisterLogic {

    public interface OnLoadImageVerifyCode  {
        void onGetImageCode(String imgUrl, String code);
    }

    public static void loadImageVerifyCode(final OnLoadImageVerifyCode callback) {
        NetSceneGetImageVerify getImageVerify = new NetSceneGetImageVerify(new NetSceneResponseCallback<Racecar.GetImageVerifyResponse>() {

            @Override
            public void onRequestFailed(IRequest request) {
            }

            @Override
            public void onLocalNetworkIssue(IRequest request) {
            }

            @Override
            public void onResponse(IRequest request, Racecar.GetImageVerifyResponse result) {
                if(result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    callback.onGetImageCode(result.getUrl(), result.getVerifyCode());
                } else {
                    ToastUtil.show(R.string.load_verify_fail);
                }
            }
        });
        getImageVerify.doScene();
    }




    /**
     * Validate the account is valid or not
     * @param type account , phone or one of them.
     * @param account
     * @return
     */
    public static boolean validateAccount(int type, String account) {
        String accountRep = null;
        Pattern pattern = null;

        if(type == ProtocolConstants.LOGIN_METHOD.ACCOUNT_LOGIN) {
            if(Util.isNullOrBlank(account)) {
                ToastUtil.show(R.string.account_empty_error);
                return false;
            }

            accountRep = "[a-z][A-Za-z0-9]{5,11}";
            pattern = Pattern.compile(accountRep);
            Matcher matcher = pattern.matcher(account);
            if(!matcher.find()) {
                ToastUtil.show(R.string.account_format_error);
                return false;
            }
        } else if(type == ProtocolConstants.LOGIN_METHOD.PHONE_LOGIN) {
            if(Util.isNullOrBlank(account)) {
                ToastUtil.show(R.string.phone_empty_error);
                return false;
            }

            if(account.length() != ProtocolConstants.Register.PHONE_NUM_LENGTH) {
                ToastUtil.show(R.string.phone_format_error);
                return false;
            }
            return true;
        } else {
            accountRep = "[a-z][A-Za-z0-9]{5,11}";
            pattern = Pattern.compile(accountRep);
            Matcher matcher = pattern.matcher(account);
            if(matcher.find()) {
                return true;
            }

            accountRep = "1[34578][0-9]{9}";
            pattern = Pattern.compile(accountRep);
            matcher = pattern.matcher(account);
            if(!matcher.find()) {
                ToastUtil.show(R.string.format_error);
                return false;
            }
        }

        return true;
    }

    /**
     * Validate the password.
     * @param password
     * @return
     */
    public static boolean validatePWD(String password) {
        if(Util.isNullOrNil(password)) {
            ToastUtil.show(R.string.pwd_empty_error);
            return false;
        }

        if(password.length() < ProtocolConstants.Register.PASSWORD_MIN_LENGTH ||
                password.length() > ProtocolConstants.Register.PASSWORD_MAX_LENGTH) {
            ToastUtil.show(R.string.pwd_length_error);
            return false;
        }

        return true;
    }

    /**
     * Show the notice dialog.
     * @param context
     * @param string
     */
    public static void showErrorNotice(Context context, @StringRes int string) {
        if(context == null) {
            return;
        }
        final BpDialog<UiAlphaNoticeBinding> dialog = new BpDialog(context, R.layout.ui_alpha_notice);
        String content = context.getString(string);
        dialog.setCancelable(true);
        dialog.mBinding.setContent(content);
        dialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * Show the notice dialog.
     * @param context
     * @param content
     */
    public static void showErrorNotice(Context context, String content) {
        if(context == null) {
            return;
        }
        final BpDialog<UiAlphaNoticeBinding> dialog = new BpDialog(context, R.layout.ui_alpha_notice);
        dialog.setCancelable(true);
        dialog.mBinding.setContent(content);
        dialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
