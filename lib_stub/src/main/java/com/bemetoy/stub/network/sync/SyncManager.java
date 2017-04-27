package com.bemetoy.stub.network.sync;

import android.content.Intent;

import com.bemetoy.bp.autogen.events.NoticeUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.network.auth.NetSceneAlphaLogin;
import com.bemetoy.stub.util.JsonManager;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.bemetoy.bp.autogen.protocol.Racecar.CommandItem;
import static com.bemetoy.bp.autogen.protocol.Racecar.JsonMessage;
import static com.bemetoy.bp.autogen.protocol.Racecar.SyncRequest;
import static com.bemetoy.bp.autogen.protocol.Racecar.SyncResponse;
import static com.bemetoy.bp.autogen.protocol.Racecar.SyncSequence;

/**
 * Created by Tom on 2016/3/22.
 */
public class SyncManager implements ResponseCallBack<Racecar.SyncResponse> {

    private static final String TAG = "sync.SyncManager";

    private static final SyncManager mManager = new SyncManager();

    private SyncManager() {

    }

    public static SyncManager getInstance() {
        return mManager;
    }

    @Override
    public void onResponse(int errType, int errorCodeFromSVR, String errorMsg, final IRequest request, final SyncResponse result) {

        if (errType != ProtocolConstants.ErrorType.OK) {
            Log.e(TAG, "network issue.");
            return;
        }

        if (errorCodeFromSVR == Racecar.CommonHeaderErrorCode.Common_Header_Error_Code_Session_Key_Time_Out_VALUE) {
            NetSceneAlphaLogin netSceneAuth = NetSceneAlphaLogin.buildFromFile();
            if (netSceneAuth == null) {
                int flag = Intent.FLAG_ACTIVITY_CLEAR_TASK;
                flag |= Intent.FLAG_ACTIVITY_NEW_TASK;
                PluginStubBus.doAction(ApplicationContext.getContext(), PluginConstants.Plugin.PLUGIN_NAME_APP,
                        PluginConstants.App.Action.CMD_START_LAUNCHER_UI, flag, null);
                return;
            }
            netSceneAuth.doScene();
            Log.e(TAG, "session time out");
            return;
        }

        if (!(request instanceof NetSceneSync)) {
            Log.e(TAG, "error instance");
            return;
        }
        Log.i(TAG, "errType = %s, errorCodeFromSVR = %s, errorMsg = %s", errType, errorCodeFromSVR, errorMsg);

        if (result == null) {
            Log.e(TAG, "sync response is null");
            return;
        }

        final NetSceneSync netSceneSync = (NetSceneSync) request;
        ThreadPool.post(new Runnable() {
            @Override
            public void run() {
                String dataFilePath = StorageConstants.ACCOUNT_DATA_PATH;
                FileUtils.createFileIfNeed(new File(dataFilePath));
                CfgFs cfgFs = new CfgFs(dataFilePath);
                Object obj = cfgFs.get(StorageConstants.Info_Key.SYNC_KEY);
                List<Racecar.SyncSequence> syncSequences = new ArrayList<>();
                if (obj != null) {
                    syncSequences = (List<Racecar.SyncSequence>) obj;
                }

                mergeSyncSequence(syncSequences, result.getSyncSequenceList());
                if (result.getContinueflag()) {
                    //TODO continue to do sync.
                    doSync(SyncRequest.Selector.JSON, null);
                }
                dealWithCommandItem(result);
            }
        });

        //TODO handle the callback
        if (netSceneSync.getIOnSyncEnd() != null) {
            netSceneSync.getIOnSyncEnd().onSyncEnd(errType, errorCodeFromSVR, errorMsg,
                    request, getCommandItem(request.getCmdId()));
        }
    }


    public interface IOnSyncEnd {
        void onSyncEnd(int errType, int errorCodeFromSVR, String errorMsg, IRequest request, Object messageItem);
    }

    public void doSync(SyncRequest.Selector selector, IOnSyncEnd onSyncEnd) {
        NetSceneSync netSceneSync = new NetSceneSync(selector, onSyncEnd, this);
        netSceneSync.doScene();
    }

    /**
     * handle the callback.
     *
     * @param response
     */
    private void dealWithCommandItem(SyncResponse response) {
        Log.d(TAG, "item size is %d", response.getCommandItemCount());
        List<SyncSequence> syncSequences = response.getSyncSequenceList();
        for (CommandItem item : response.getCommandItemList()) {
            CommandItem.Type type = item.getType();
            switch (type) {
                case JSON_MESSAGE:
                    handleSystemMessage(item.getData());
                    break;
            }
        }
    }


    /**
     * TODO return the command item list.
     *
     * @param selector the item id bit map selector.
     * @return
     */
    private Object getCommandItem(int selector) {

        return null;
    }

    private void handleSystemMessage(ByteString bytes) {
        JsonMessage jsonMessage = null;
        try {
            jsonMessage = JsonMessage.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        JSONObject object = JsonManager.convertString2Json(jsonMessage.getData());
        final String method = JsonManager.getFiled(object, ProtocolConstants.JsonFiled.METHOD);
        Log.i(TAG, "[Time] %s : receive notice [method]:%s, [content]: %s", System.currentTimeMillis(), method, jsonMessage.getData());
        AddFriendMessageHandler addFriendMessageHandler = new AddFriendMessageHandler();
        SystemNoticeMessageHandler systemNoticeMessageHandler = new SystemNoticeMessageHandler();
        addFriendMessageHandler.setNextHandler(systemNoticeMessageHandler);
        boolean result = addFriendMessageHandler.handleMessage(jsonMessage.getData(), method);
        if(result) {
            RxEventBus.getBus().publish(new NoticeUpdateEvent());
        }
    }

    private static void mergeSyncSequence(List<SyncSequence> oldSyncSequence, List<SyncSequence> newSyncSequence) {
        if (Util.isNull(newSyncSequence) || newSyncSequence.isEmpty()) {
            Log.d(TAG, "newSyncSequence is null or empty");
            return;
        }

        Log.d(TAG, "old sync sequence is");
        for (SyncSequence syncSequence : oldSyncSequence) {
            Log.d(TAG, "cmd id is %s, sync sequence is %s", syncSequence.getKey(), syncSequence.getValue());
        }

        Log.d(TAG, "new sync sequence is");
        for (SyncSequence syncSequence : newSyncSequence) {
            Log.d(TAG, "cmd id is %s, sync sequence is %s", syncSequence.getKey(), syncSequence.getValue());
        }

        List<SyncSequence> all = new ArrayList<>();
        all.addAll(oldSyncSequence);

        for (int i = 0; i < newSyncSequence.size(); ++i) {
            SyncSequence syncSeq = newSyncSequence.get(i);
            int index = getIndex(syncSeq, all);
            if (-1 != index) {
                all.remove(index);
            }
            all.add(syncSeq);
        }
        Log.d(TAG, "sync sequence after merge is");
        for (SyncSequence syncSequence : all) {
            Log.d(TAG, "cmd id is %s, sync sequence is %s", syncSequence.getKey(), syncSequence.getValue());
        }

        String dataFilePath = StorageConstants.ACCOUNT_DATA_PATH;
        FileUtils.createFileIfNeed(new File(dataFilePath));
        CfgFs cfgFs = new CfgFs(dataFilePath);
        cfgFs.set(StorageConstants.Info_Key.SYNC_KEY, all);
    }


    private static int getIndex(SyncSequence syncSeq, List<SyncSequence> list) {
        if (Util.isNull(syncSeq) || Util.isNull(list)) {
            Log.e(TAG, "input param is errror");
            return -1;
        }
        int index = -1;
        for (int i = 0; i < list.size(); ++i) {
            SyncSequence tmpSyncSeq = list.get(i);
            if (tmpSyncSeq.getKey() == syncSeq.getKey()) {
                index = i;
                break;
            }
        }

        return index;
    }


}
