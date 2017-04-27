package com.bemetoy.bp.plugin.friends.model;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.network.NetSceneLoadingWrapper;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.network.sync.NetSceneSyncJson;
import com.bemetoy.stub.util.JsonManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Tom on 2016/6/21.
 */
public class FriendLogic {

    private static final String TAG = "Friends.FriendLogic";

    private static final Set<Integer> friendKeySet = new HashSet();

    public interface AddFriendCallback {
        /**
         * Invoked when get the response from the server.
         * @param result send the msg success or failed
         */
        void onGetResponse(boolean result);
    }

    public interface DeleteFriendCallback {
        /**
         * Invoked when get the response from the server.
         * @param result send the msg success or failed
         */
        void onGetResponse(boolean result);
    }


    public static void initFriendKeySet(List<Racecar.AccountInfo> list) {
        friendKeySet.clear();
        for(Racecar.AccountInfo accountInfo : list) {
            friendKeySet.add(accountInfo.getUserId());
        }
    }

    public static boolean isMyFriend(int userId) {
        return friendKeySet.contains(userId);
    }

    /**
     * Remove user id form my friends keyset.
     * @param userId
     * @return
     */
    public static boolean removeFriendKey(int userId) {
        return friendKeySet.remove(userId);
    }

    /**
     * Send apply friend request to the user.
     * @param userId the id of the target user.
     * @param content the attach message.
     * @param callback
     */
    public static void sendAddFriendMessage(int userId, String content, final AddFriendCallback callback) {
        Racecar.AccountInfo myAccount = AccountLogic.getAccountInfo();
        if(content == null) {
            content = "";
        }

        String addFriendJson = JsonManager.createAddFriendJson(myAccount.getUserId(), myAccount.getName(), userId, content);
        NetSceneSyncJson sceneSyncJson = new NetSceneSyncJson(addFriendJson, new NetSceneResponseCallback<Racecar.SyncJsonResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.SyncJsonResponse result) {
                if(result == null ) {
                    Log.e(TAG, "SyncJsonResponse is null");
                    return;
                }

                if(callback == null) {
                    return;
                }

                if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    callback.onGetResponse(true);
                } else {
                    callback.onGetResponse(false);
                }
            }
        });
        sceneSyncJson.doScene();
    }

    public static void deleteFriend(final int userId, final DeleteFriendCallback callback) {
        NetSceneDeleteFriend deleteFriend = new NetSceneDeleteFriend(userId, new NetSceneCallbackLoadingWrapper<Racecar.DeleteFriendResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.DeleteFriendResponse result) {
                if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    callback.onGetResponse(true);
                } else {
                    callback.onGetResponse(false);
                }
            }
        });
        new NetSceneLoadingWrapper(deleteFriend).doScene();
    }


}
