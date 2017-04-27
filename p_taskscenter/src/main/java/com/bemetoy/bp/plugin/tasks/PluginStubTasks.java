package com.bemetoy.bp.plugin.tasks;

import android.content.Context;
import android.os.Bundle;

import com.bemetoy.bp.plugin.tasks.ui.TasksCenterUI;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.core.base.PluginStubAdapter;

/**
 * Created by albieliang on 16/5/10.
 */
public class PluginStubTasks extends PluginStubAdapter {

    @Override
    public boolean doAction(Context context, int cmd, Bundle data, int flag, OnActionResult onActionResult) {
        switch (cmd) {
            case PluginConstants.TasksCenter.Action.CMD_START_TASKS_CENTER_UI:
                startActivity(context, TasksCenterUI.class, flag, data);
                return true;
            default:
                break;
        }
        return super.doAction(context, cmd, data, flag, onActionResult);
    }
}
