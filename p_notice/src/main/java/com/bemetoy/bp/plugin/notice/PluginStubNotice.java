package com.bemetoy.bp.plugin.notice;

import android.content.Context;
import android.os.Bundle;

import com.bemetoy.bp.plugin.notice.ui.NoticeCenterUI;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.core.base.PluginStubAdapter;

/**
 * Created by albieliang on 16/5/13.
 */
public class PluginStubNotice extends PluginStubAdapter {

    @Override
    public boolean doAction(Context context, int cmd, Bundle data, int flag, OnActionResult onActionResult) {
        switch (cmd) {
            case PluginConstants.Notice.Action.CMD_START_NOTICE_CENTER_UI:
                startActivity(context, NoticeCenterUI.class, flag, data);
                return true;
            default:
                break;
        }
        return super.doAction(context, cmd, data, flag, onActionResult);
    }
}
