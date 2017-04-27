package com.bemetoy.bp.plugin.friends;

import android.content.Context;
import android.os.Bundle;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.friends.model.FriendLogic;
import com.bemetoy.bp.plugin.friends.model.NetSceneGetFriends;
import com.bemetoy.bp.plugin.friends.ui.FriendDetailUI;
import com.bemetoy.bp.plugin.friends.ui.FriendsUI;
import com.bemetoy.bp.plugin.friends.ui.SearchFriendUI;
import com.bemetoy.bp.plugin.friends.ui.UserInfoUI;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.base.PluginStubAdapter;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.stub.network.NetSceneResponseCallback;

/**
 * Created by Tom on 2016/4/27.
 */
public class PluginStubFriends extends PluginStubAdapter {

    private final static String TAG = "Friends.PluginStubFriends";

    @Override
    public boolean doAction(Context context, int cmd, Bundle data, int flag, OnActionResult onActionResult) {
        switch (cmd) {
            case PluginConstants.FRIENDS.Action.CMD_START_FRIENDS_UI:
                startActivity(context, FriendsUI.class, flag, data);
                break;
            case PluginConstants.FRIENDS.Action.CMD_START_SEARCH_FRIEND_UI:
                startActivity(context, SearchFriendUI.class, flag, data);
                break;
            case PluginConstants.FRIENDS.Action.CMD_START_USER_DETAIL:
                startActivity(context, UserInfoUI.class, flag, data);
                break;
            case PluginConstants.FRIENDS.Action.CMD_START_FRIEND_DETAIL:
                startActivity(context, FriendDetailUI.class, flag, data);
                break;
            case PluginConstants.FRIENDS.Action.CMD_INIT_FRIENDS_DATA:
                new NetSceneGetFriends(new NetSceneResponseCallback<Racecar.GetFriendResponse>() {
                    @Override
                    public void onRequestFailed(IRequest request) {
                        Log.e(TAG, "load friends data error");
                    }

                    @Override
                    public void onResponse(IRequest request, Racecar.GetFriendResponse result) {
                        if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                            FriendLogic.initFriendKeySet(result.getAccountInfoList());
                        } else {
                            Log.e(TAG, "load friends data error, error code is %d", result.getPrimaryResp().getResult());
                        }
                    }
                }).doScene();
                break;
            case PluginConstants.FRIENDS.Action.CMD_QUERY_RELATIONSHIP:
                if(data != null && onActionResult != null) {
                    int id = data.getInt(ProtocolConstants.IntentParams.USER_ID);
                    boolean isMyFriend = FriendLogic.isMyFriend(id);
                    Bundle result = new Bundle();
                    result.putBoolean(ProtocolConstants.IntentParams.IS_FRIEND, isMyFriend);
                    onActionResult.onActionResult(context, cmd, OnActionResult.ACTION_RESULT_OK, result);
                }
                break;
        }
        return super.doAction(context, cmd, data, flag, onActionResult);
    }
}
