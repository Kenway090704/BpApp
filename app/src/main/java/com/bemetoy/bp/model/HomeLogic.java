package com.bemetoy.bp.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.UiActivationDialogBinding;
import com.bemetoy.bp.databinding.UiActivationErrorBinding;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.stub.databinding.UiVersionUpdateDialogBinding;
import com.bemetoy.bp.ui.ActivationCarUI;
import com.bemetoy.bp.ui.ActivationScoreUI;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.ui.LoadingDialog;

import java.io.File;

/**
 * Created by Tom on 2016/5/18.
 */
public class HomeLogic {

    private static final String TAG = "APP.HomeLogic";

    /**
     * Do the Activate action.
     * @param context
     * @param code
     */
    public static void doExchange(final Context context, final String code) {
        if(Util.isNullOrBlank(code)) {
            final BpDialog<UiActivationErrorBinding> bpDialog = new BpDialog(context, R.layout.ui_activation_error);
            bpDialog.mBinding.errorMsgTv.setText(R.string.exchange_hint);
            bpDialog.mBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bpDialog.dismiss();
                }
            });

            bpDialog.mBinding.retryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bpDialog.dismiss();
                    showExchangeDialog(context);
                }
            });
            bpDialog.setCancelable(true);
            bpDialog.show();
            return;
        }


        final LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.show();

        final NetSceneActivation netSceneActivation = new NetSceneActivation(code, new NetSceneCallbackLoadingWrapper<Racecar.ActivateCdkeyResponse>() {
            @Override
            public void onResponse(IRequest request, final Racecar.ActivateCdkeyResponse result) {
                if(result.getPrimaryResp().getResult() != Racecar.ErrorCode.ERROR_OK_VALUE) {
                    final BpDialog<UiActivationErrorBinding> bpDialog = new BpDialog(context, R.layout.ui_activation_error);
                    bpDialog.mBinding.errorMsgTv.setText(result.getPrimaryResp().getErrorMsg());
                    bpDialog.mBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bpDialog.dismiss();
                        }
                    });

                    bpDialog.mBinding.retryBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bpDialog.dismiss();
                            showExchangeDialog(context);
                        }
                    });
                    bpDialog.setCancelable(true);
                    bpDialog.show();
                } else {
                    if(result.getType() == 2 ) {
                        Intent intent = new Intent(context, ActivationCarUI.class);
                        intent.putExtra(ProtocolConstants.IntentParams.CAR_COUNT, result.getCount());
                        intent.putExtra(ProtocolConstants.IntentParams.CAR_INFO, result.getCar());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, ActivationScoreUI.class);
                        intent.putExtra(ProtocolConstants.IntentParams.SCORE_INFO, result.getScore());
                        context.startActivity(intent);
                    }
                }
            }

            @Override
            public void onNetSceneEnd() {
                super.onNetSceneEnd();
                loadingDialog.dismiss();
            }
        });
        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                netSceneActivation.cancel();
            }
        });
        netSceneActivation.doScene();
    }


    /**
     * Show the activate dialog.
     * @param context
     */
    public static void showExchangeDialog(final Context context) {
        final BpDialog<UiActivationDialogBinding> dialog = new BpDialog<UiActivationDialogBinding>(context, R.layout.ui_activation_dialog);
        dialog.mBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = dialog.mBinding.keyEd.getText().toString();
                    dialog.dismiss();
                    doExchange(context, key);

            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    /**
     * Show the update dialog and update the apk
     * @param context
     * @param updateLog
     * @param filePath
     */
    public static void showUpdateDialog(final Context context, String updateLog, final String filePath) {
        BpDialog<UiVersionUpdateDialogBinding> dialog = new BpDialog(context, R.layout.ui_version_update_dialog);
        dialog.mBinding.contentTv.setText(updateLog);
        dialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        dialog.show();
    }

    public static int getCarImage(String carId) {
        switch(carId) {
            case "668101":
                return R.drawable.car_101;
            case "668102":
                return R.drawable.car_102;
            case "668103":
                return R.drawable.car_103;
            case "668104":
                return R.drawable.car_104;
            case "668105":
                return R.drawable.car_105;
            case "668201":
                return R.drawable.car_201;
            case "668202":
                return R.drawable.car_202;
            case "668203":
                return R.drawable.car_203;
            case "668204":
                return R.drawable.car_204;
            case "668205":
                return R.drawable.car_205;
            case "668206":
                return R.drawable.car_206;
            case "668207":
                return R.drawable.car_207;
            case "668301":
                return R.drawable.car_301;
            case "668302":
                return R.drawable.car_302;
            case "668303":
                return R.drawable.car_303;
            case "668304":
                return R.drawable.car_304;
            case "668401":
                return R.drawable.car_401;
            case "668402":
                return R.drawable.car_402;
            case "668403":
                return R.drawable.car_403;
            case "668404":
                return R.drawable.car_404;
            default:
                return R.drawable.car_106;
        }
    }

    public static String getCaName(String carId) {
        switch(carId) {
            case "668101":
                return "超音子弹";
            case "668102":
                return "脉冲新星";
            case "668103":
                return "裂地飞轮";
            case "668104":
                return "流火号";
            case "668105":
                return "影舞者";
            case "668201":
                return "爆裂飞弹";
            case "668202":
                return "霹雳彗星";
            case "668203":
                return "轰天速轮";
            case "668204":
                return "超域飞影";
            case "668205":
                return "逐魂号";
            case "668206":
                return "独眼巨魔";
            case "668207":
                return "凯旋战神";
            case "668301":
                return "先驱角号";
            case "668302":
                return "落日之弓";
            case "668303":
                return "噬魂巨刃";
            case "668304":
                return "末日战斧";
            case "668401":
                return "专业爆裂飞弹";
            case "668402":
                return "专业霹雳彗星";
            case "668403":
                return "专业轰天速轮";
            case "668404":
                return "专业凯旋战神";
            default:
                return "冰原猎人";
        }
    }


}

