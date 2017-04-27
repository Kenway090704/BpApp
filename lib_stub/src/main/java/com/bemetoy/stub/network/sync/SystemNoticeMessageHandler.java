package com.bemetoy.stub.network.sync;

import com.bemetoy.bp.autogen.table.SysMessage;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.stub.db.StorageManager;
import com.bemetoy.stub.util.JsonManager;

import org.json.JSONObject;

/**
 * Created by Tom on 2016/7/5.
 */
public class SystemNoticeMessageHandler extends JsonMessageHandler {

    @Override
    protected boolean dealWithMessage(String jsonMessage) {
        JSONObject object = JsonManager.convertString2Json(jsonMessage);
        final String method = JsonManager.getFiled(object, ProtocolConstants.JsonFiled.METHOD);
        final String content  =  JsonManager.getFiled(object, ProtocolConstants.JsonFiled.PARAMS);
        SysMessage sysMsg = new SysMessage();
        sysMsg.setMethod(method);
        sysMsg.setContent(content);
        sysMsg.setHasRead(false);
        sysMsg.setResult(String.valueOf(ProtocolConstants.JsonValue.ACTION_UN_HANDLE));
        return StorageManager.getMgr().getSysMessageStorage().insert(sysMsg);
    }

    @Override
    public boolean shouldDealWith(String messageType) {
        return ProtocolConstants.MessageType.NOTICE.equals(messageType)
                || ProtocolConstants.MessageType.NOTICE_GAME.equals(messageType)
                || ProtocolConstants.MessageType.NOTICE_REWARD.equals(messageType);
    }
}
