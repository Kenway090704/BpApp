package com.bemetoy.stub.network.sync;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.ResponseCallBack;
import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.stub.network.NetSceneBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/3/22.
 */
public class NetSceneSync extends NetSceneBase {

    private static final String TAG = "sync.NetSceneSync";

    private Racecar.SyncRequest.Selector mSelector;
    private SyncManager.IOnSyncEnd mOnSyncEnd;

    public NetSceneSync(Racecar.SyncRequest.Selector selector, final SyncManager.IOnSyncEnd iOnSyncEnd,ResponseCallBack callBack) {
        super(Racecar.CmdId.SYNC_REQUEST_VALUE,callBack);
        mOnSyncEnd = iOnSyncEnd;
        mSelector = selector;
    }

    public SyncManager.IOnSyncEnd getIOnSyncEnd() {
        return mOnSyncEnd;
    }


    @Override
    public byte[] getRequestBody() {

        Racecar.SyncRequest.Builder builder = Racecar.SyncRequest.newBuilder();
        builder.setPrimaryReq(builderBaseRequest());

        String dataFilePath = StorageConstants.ACCOUNT_DATA_PATH;
        CfgFs cfgFs = new CfgFs(dataFilePath);
        Object obj = cfgFs.get(StorageConstants.Info_Key.SYNC_KEY);
        List<Racecar.SyncSequence> syncSequences = new ArrayList<>();
        if(obj != null) {
            syncSequences = (List<Racecar.SyncSequence>) obj;
        }
        for(Racecar.SyncSequence sequence : syncSequences) {
            Log.d(TAG, "cmd is %s, sync sequence is %s", sequence.getKey(), sequence.getValue());
        }

        builder.setSelector(mSelector);
        builder.addAllSyncSequence(syncSequences);
        return builder.build().toByteArray();
    }
}
