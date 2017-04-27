package com.bemetoy.bp.plugin.car;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bemetoy.bp.plugin.car.ui.UnityPlayerActivity;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.base.PluginStubAdapter;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.util.JsonManager;

/**
 * Created by Tom on 2016/8/29.
 */
public class PluginStubMyCar extends PluginStubAdapter {

    @Override
    public boolean doAction(Context context, int cmd, Bundle data, int flag, OnActionResult onActionResult) {
        switch (cmd) {
            case PluginConstants.MyCard.Action.CMD_START_MYCAR_UI:
                Intent intent = new Intent(context, UnityPlayerActivity.class);
                String accountJson = JsonManager.getAccountJson(AccountLogic.getAccountInfo(false));
                intent.putExtra(ProtocolConstants.IntentParams.USER_INFO, accountJson);
                intent.putExtra(ProtocolConstants.IntentParams.USER_ID, PluginStubBus.getInt(PluginConstants.Plugin.PLUGIN_NAME_APP,
                        PluginConstants.App.DataKey.GET_USER_ID, -1));
                intent.putExtra(ProtocolConstants.IntentParams.SESSION_ID,
                        AppCore.getCore().getAccountManager().getAccountInfo().getSessionKey());
                context.startActivity(intent);
                return true;
        }
        return super.doAction(context, cmd, data, flag, onActionResult);
    }
}
