package com.bemetoy.bp.plugin.personalcenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bemetoy.bp.plugin.personalcenter.ui.AddNewAddressUI;
import com.bemetoy.bp.plugin.personalcenter.ui.AddressListUI;
import com.bemetoy.bp.plugin.personalcenter.ui.EditAddressUI;
import com.bemetoy.bp.plugin.personalcenter.ui.EditNicknameUI;
import com.bemetoy.bp.plugin.personalcenter.ui.PersonalCenterUI;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.base.PluginStubAdapter;

/**
 * Created by albieliang on 16/4/16.
 */
public class PluginStubPersionalCenter extends PluginStubAdapter {

    private static final String TAG = "PluginStubPersionalCenter";

    @Override
    public boolean doAction(final Context context, final int cmd, Bundle data, int flag, final OnActionResult onActionResult) {
        switch (cmd) {
            case PluginConstants.PersonalCenter.Action.CMD_START_PERSONAL_CENTER_UI:
                startActivity(context, PersonalCenterUI.class, flag, data);
                return true;
            case PluginConstants.PersonalCenter.Action.CMD_SHOW_NICKNAME_EDIT_DIALOG:
                return true;
            case PluginConstants.PersonalCenter.Action.CMD_START_ADD_ADDRESS_UI:
                startActivity(context, AddNewAddressUI.class, flag, data);
                return true;
            case PluginConstants.PersonalCenter.Action.CMD_START_EDIT_ADDRESS_UI:
                startActivity(context, EditAddressUI.class, flag, data);

                return true;
            case PluginConstants.PersonalCenter.Action.CMD_START_EDIT_NICKNAME_UI:
                startActivity(context, EditNicknameUI.class, flag, data);
                return true;
            case PluginConstants.PersonalCenter.Action.CMD_START_ADD_ADDRESS_UI_FOR_RESULT:
                if(!(context instanceof Activity)) {
                    throw new IllegalArgumentException("Only activity context is support for cmd 'CMD_START_ADD_ADDRESS_UI_FOR_RESULT' ");
                }
                Activity activity = (Activity)context;
                Intent intent = new Intent(activity, AddNewAddressUI.class);
                if(data != null && !data.isEmpty()) {
                    intent.putExtras(data);
                }
                activity.startActivityForResult(intent, ProtocolConstants.RequestCode.ACTION_ADD_NEW_ADDRESS);
                return true;
            case PluginConstants.PersonalCenter.Action.CMD_START_ADDRESS_LIST_UI_FOR_RESULT:
                if(!(context instanceof Activity)) {
                    throw new IllegalArgumentException("Only activity context is support for cmd 'CMD_START_ADD_ADDRESS_UI_FOR_RESULT' ");
                }
                Activity srcActivity = (Activity)context;
                Intent intent1 = new Intent(srcActivity, AddressListUI.class);
                if(data != null && !data.isEmpty()) {
                    intent1.putExtras(data);
                }
                intent1.putExtra(ProtocolConstants.IntentParams.ADAPTER_CHOOSE_MODEL, true);
                srcActivity.startActivityForResult(intent1, ProtocolConstants.RequestCode.ACTION_CHOOSE_ADDRESS);
                return true;
            default:
                break;
        }
        return super.doAction(context, cmd, data, flag, onActionResult);
    }
}
