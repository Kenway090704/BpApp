package com.bemetoy.bp.plugin.mall;

import android.content.Context;
import android.os.Bundle;

import com.bemetoy.bp.plugin.mall.ui.MallUI;
import com.bemetoy.bp.plugin.mall.ui.MyOrderUI;
import com.bemetoy.bp.plugin.mall.ui.PartDetailUI;
import com.bemetoy.bp.plugin.mall.ui.adapter.MyOrderListAdapter;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.core.base.PluginStubAdapter;

/**
 * Created by Tom on 2016/4/22.
 */
public class PluginStubMall extends PluginStubAdapter {

    @Override
    public boolean doAction(Context context, int cmd, Bundle data, int flag, OnActionResult onActionResult) {
        switch (cmd) {
            case PluginConstants.Mall.Action.CMD_START_MALL_UI:
                startActivity(context, MallUI.class, flag, data);
                return true;
            case PluginConstants.Mall.Action.CMD_START_PART_UI:
                startActivity(context, PartDetailUI.class, flag, data);
                return true;
            case PluginConstants.Mall.Action.CMD_START_ORDERS_UI:
                startActivity(context, MyOrderUI.class, flag, data);
                return true;
        }

        return super.doAction(context, cmd, data, flag, onActionResult);
    }
}
