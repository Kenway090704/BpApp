package com.bemetoy.stub.network.sync;

/**
 * Created by Tom on 2016/7/5.
 */
public abstract class JsonMessageHandler {

    private static final String TAG = "Sync.JsonMessageHandler";

    private JsonMessageHandler messageHandler;
    public final boolean handleMessage(String jsonMessage, String messageType) {
         if(shouldDealWith(messageType)) {
             return dealWithMessage(jsonMessage);
         } else if(getNextHandler() != null ) {
             return getNextHandler().handleMessage(jsonMessage, messageType);
         }
        return false;
    }

    protected abstract boolean dealWithMessage(String jsonMessage);

    public abstract boolean shouldDealWith(String messageType);

    public void setNextHandler(JsonMessageHandler handler) {
        messageHandler = handler;
    }

    public JsonMessageHandler getNextHandler() {
        return messageHandler;
    }

}
