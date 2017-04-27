package com.bemetoy.stub.network.sync;

import com.bemetoy.bp.autogen.table.MyMessage;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.stub.db.StorageManager;
import com.bemetoy.stub.util.JsonManager;

/**
 * Created by Tom on 2016/7/5.
 */
public class AddFriendMessageHandler extends JsonMessageHandler {

    private static final String TAG = "Sync.AddFriendMessageHandler";

    @Override
    protected boolean dealWithMessage(String jsonMessage) {
        MyMessage message = JsonManager.convertStringToMyMessage(jsonMessage);
        boolean insertResult = StorageManager.getMgr().getMyMessageStorage().insert(false, message);
        Log.d(TAG, "insert add friend request result %b", insertResult);
        return insertResult;
    }

    @Override
    public boolean shouldDealWith(String messageType) {
        return messageType.equalsIgnoreCase(ProtocolConstants.MessageType.ADD_REQUEST);
    }
}
