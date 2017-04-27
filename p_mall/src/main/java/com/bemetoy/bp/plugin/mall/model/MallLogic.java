package com.bemetoy.bp.plugin.mall.model;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.MallContract;
import com.bemetoy.bp.plugin.mall.MallPresent;
import com.bemetoy.bp.plugin.mall.R;
import com.bemetoy.bp.plugin.mall.databinding.UiAddressChooseDialogBinding;
import com.bemetoy.bp.plugin.mall.databinding.UiConfirmExchangeBinding;
import com.bemetoy.bp.plugin.mall.databinding.UiScoreNotEnoughBinding;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.stub.account.AccountLogic;

import junit.framework.Assert;

import java.io.Serializable;

/**
 * Created by tomliu on 16-9-6.
 */
public class MallLogic {

    public static void showExchangeConfirmDialog(final Context context, final Racecar.Address address,
                                                 final Racecar.GoodsListResponse.Item item, final MallContract.ExchangePresent present) {
        final BpDialog<UiConfirmExchangeBinding> bpDialog =
                new BpDialog(context, R.layout.ui_confirm_exchange);
        bpDialog.setCancelable(true);
        bpDialog.mBinding.contentIm.setImageURI(Uri.parse(item.getImage()));
        bpDialog.mBinding.contentTv.setText(context.getString(R.string.exchange_notice, item.getScore(), item.getName()));
        bpDialog.mBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpDialog.dismiss();
            }
        });

        bpDialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpDialog.dismiss();
                if(address == null) {
                    PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_P_PERSONAL_CENTER,
                            PluginConstants.PersonalCenter.Action.CMD_START_ADD_ADDRESS_UI_FOR_RESULT, 0, null);
                } else {
                    showAddressChooseDialog(context, address, item, present);
                }
            }
        });
        bpDialog.show();
    }

    public static void showDoTaskDialog(final Context context) {
        final BpDialog<UiScoreNotEnoughBinding> bpDialog =
                new BpDialog(context, R.layout.ui_score_not_enough);
        bpDialog.setCancelable(true);
        bpDialog.mBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpDialog.dismiss();
            }
        });

        bpDialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpDialog.dismiss();
                PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_P_TASKS_CENTER,
                        PluginConstants.TasksCenter.Action.CMD_START_TASKS_CENTER_UI, 0, null);
            }
        });
        bpDialog.show();
    }

    public static void showAddressChooseDialog(final Context context, final Racecar.Address defaultAddress,
                                               final Racecar.GoodsListResponse.Item item, final MallContract.ExchangePresent present){
        Assert.assertNotNull(present);
        Assert.assertNotNull(defaultAddress);
        Assert.assertNotNull(item);

        final BpDialog<UiAddressChooseDialogBinding> bpDialog =
                new BpDialog(context, R.layout.ui_address_choose_dialog);
        bpDialog.setCancelable(true);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(defaultAddress.getProvince());
        stringBuilder.append(defaultAddress.getCity());
        stringBuilder.append(defaultAddress.getDistrict());
        stringBuilder.append(defaultAddress.getDetail());
        stringBuilder.append("\r\n");
        stringBuilder.append(defaultAddress.getContact());
        stringBuilder.append(" ");
        stringBuilder.append(defaultAddress.getMobile());
        bpDialog.mBinding.defaultAddressValueTv.setText(stringBuilder.toString());
        bpDialog.mBinding.chooseAddressLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(ProtocolConstants.IntentParams.ADDRESS_ID, defaultAddress.getId());
                PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_P_PERSONAL_CENTER,
                        PluginConstants.PersonalCenter.Action.CMD_START_ADDRESS_LIST_UI_FOR_RESULT, 0 , bundle);
                bpDialog.dismiss();
            }
        });
        bpDialog.mBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpDialog.dismiss();
            }
        });

        bpDialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpDialog.dismiss();
                present.doExchange(defaultAddress.getId(), item.getGoodsId());

            }
        });
        bpDialog.show();
    }

}
