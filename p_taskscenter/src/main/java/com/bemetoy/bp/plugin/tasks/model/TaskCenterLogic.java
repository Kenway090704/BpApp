package com.bemetoy.bp.plugin.tasks.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.tasks.R;
import com.bemetoy.bp.plugin.tasks.ui.TaskRewardUI;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.app.WebViewUI;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.network.NetSceneLoadingWrapper;
import com.bemetoy.stub.util.AppUtil;

/**
 * Created by Tom on 2016/6/8.
 */
public class TaskCenterLogic {


    public static void toDoTask(Context context, int pageId) {
        switch (pageId) {
            case ProtocolConstants.PageId.GAME:

                PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_P_GAMES,
                        PluginConstants.MyCard.Action.CMD_START_MYCAR_UI, 0, null);
                break;
            case ProtocolConstants.PageId.MY_CAR:
                PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_P_MY_CAR,
                        PluginConstants.Games.Action.CMD_START_GAMES_UI, 0, null);
                break;
            case ProtocolConstants.PageId.FRIENDS:
                PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_P_FRIENDS,
                        PluginConstants.FRIENDS.Action.CMD_START_FRIENDS_UI, 0, null);
                break;
            case ProtocolConstants.PageId.MALL:
                PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_P_MALL,
                        PluginConstants.Mall.Action.CMD_START_MALL_UI, 0, null);
                break;
            case ProtocolConstants.PageId.NOTICE:
                PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_P_NOTICE,
                        PluginConstants.Games.Action.CMD_START_GAMES_UI, 0, null);
                break;
            case ProtocolConstants.PageId.TMALL:
                PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_APP,
                        PluginConstants.App.Action.CMD_START_TMALL, 0, null);
                break;
            case ProtocolConstants.PageId.DATABASE:
                if(!AppUtil.checkNetworkFirst((Activity) context)) {
                    return;
                }
                Intent intent = new Intent(context, WebViewUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.URL_INFO, ProtocolConstants.DATABASE_URL);
                intent.putExtra(ProtocolConstants.IntentParams.SHOW_BACK, false);
                context.startActivity(intent);
                break;
            case ProtocolConstants.PageId.RANKING:
                PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_P_RANKING,
                        PluginConstants.Ranking.Action.CMD_START_RANKING_UI, 0, null);
                break;
            case ProtocolConstants.PageId.ACTIVATION:
                PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_APP, PluginConstants.App.Action.CMD_START_ACTIVATION, 0, null);
                break;
        }
    }

    /**
     * Get reward from the server.
     * @param context
     * @param taskId
     * @param callback
     */
    public static void getReward(final Context context, int taskId, final OnGetRewardCallback callback) {
        final NetSceneGetReward sceneGetReward = new NetSceneGetReward(taskId, new NetSceneCallbackLoadingWrapper<Racecar.GetRewardResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.GetRewardResponse result) {
                if(result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    Intent intent = new Intent(context, TaskRewardUI.class);
                    intent.putExtra(ProtocolConstants.IntentParams.TASK_EXP, result.getExp());
                    intent.putExtra(ProtocolConstants.IntentParams.TASK_SCORE, result.getScore());
                    context.startActivity(intent);
                    callback.onGetReward();
                } else {
                    ToastUtil.show(R.string.tasks_center_get_reward_failed);
                }
            }
        });
        new NetSceneLoadingWrapper(sceneGetReward).doScene();
    }

    public interface OnGetRewardCallback {
        void onGetReward();
    }

}
