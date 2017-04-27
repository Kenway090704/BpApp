package com.bemetoy.bp.plugin.ranking;

import android.content.Context;
import android.os.Bundle;

import com.bemetoy.bp.plugin.ranking.ui.RankingUI;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.core.base.PluginStubAdapter;

/**
 * Created by Tom on 2016/4/18.
 */
public class PluginStubRanking extends PluginStubAdapter {

    @Override
    public boolean doAction(Context context, int cmd, Bundle data, int flag, OnActionResult onActionResult) {

        switch (cmd) {
            case PluginConstants.Ranking.Action.CMD_START_RANKING_UI:
                startActivity(context, RankingUI.class, flag, data);
                break;
        }

        return super.doAction(context, cmd, data, flag, onActionResult);
    }
}
