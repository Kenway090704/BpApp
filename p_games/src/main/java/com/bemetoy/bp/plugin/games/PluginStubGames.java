package com.bemetoy.bp.plugin.games;

import android.content.Context;
import android.os.Bundle;

import com.bemetoy.bp.plugin.games.ui.GameAddressUI;
import com.bemetoy.bp.plugin.games.ui.GameDetailUI;
import com.bemetoy.bp.plugin.games.ui.GamesUI;
import com.bemetoy.bp.plugin.games.ui.MapUI;
import com.bemetoy.bp.plugin.games.ui.PKGameDetailUI;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.sdk.core.base.PluginStubAdapter;

/**
 * Created by Tom on 2016/4/21.
 */
public class PluginStubGames extends PluginStubAdapter {

    @Override
    public boolean doAction(Context context, int cmd, Bundle data, int flag, OnActionResult onActionResult) {
        switch (cmd) {
            case PluginConstants.Games.Action.CMD_START_GAMES_UI:
                startActivity(context, GamesUI.class, flag, data);
                break;
            case PluginConstants.Games.Action.CMD_START_GAME_DETAIL:
                startActivity(context, GameDetailUI.class, flag, data);
                break;
            case PluginConstants.Games.Action.CMD_START_ADDRESS_DETAIL:
                startActivity(context, GameAddressUI.class, flag, data);
                break;
            case PluginConstants.Games.Action.CMD_START_MAP_UI:
                startActivity(context, MapUI.class, flag, data);
                break;
            case PluginConstants.Games.Action.CMD_START_PK_GAME_DETAIL:
                startActivity(context, PKGameDetailUI.class, flag, data);
                break;
        }

        return super.doAction(context, cmd, data, flag, onActionResult);
    }
}
