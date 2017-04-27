package com.bemetoy.bp.plugin.operatoins;

import android.content.Context;
import android.os.Bundle;

import com.bemetoy.bp.plugin.operatoins.ui.OperationCenterUI;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.core.base.PluginStubAdapter;

/**
 * Created by Tom on 2016/4/22.
 */
public class PluginStubOperations extends PluginStubAdapter {

    @Override
    public boolean doAction(Context context, int cmd, Bundle data, int flag, OnActionResult onActionResult) {
        switch (cmd) {
            case PluginConstants.Operations.Action.CMD_START_OPERATIONS_UI:
                startActivity(context, OperationCenterUI.class, flag, data);
                break;
        }
        return super.doAction(context, cmd, data, flag, onActionResult);
    }
}
