package com.bemetoy.bp.plugin.notice.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.autogen.table.MyMessage;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.notice.R;
import com.bemetoy.bp.plugin.notice.databinding.NoticeMyMessageItemBinding;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;
import com.bemetoy.stub.db.MyMessageStorage;
import com.bemetoy.stub.db.StorageManager;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.network.NetSceneLoadingWrapper;
import com.bemetoy.stub.network.sync.NetSceneSyncJson;
import com.bemetoy.stub.ui.BPDialogManager;
import com.bemetoy.stub.util.JsonManager;

/**
 * Created by albieliang on 16/5/14.
 */
public class MyNoticeListAdapter extends ExtRecyclerViewAdapter<MyMessage> {

    private static final String TAG = "Notice.MyNoticeListAdapter";

    private LayoutInflater mInflater;

    public MyNoticeListAdapter(LayoutInflater inflater) {
        mInflater = inflater;

    }

    @Override
    public IViewHolder<MyMessage> onCreateViewHolder(View view, int viewType) {
        return new NoticeRecordViewHolder();
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return mInflater.inflate(R.layout.notice_my_message_item, parent, false);
    }


    private static class NoticeRecordViewHolder extends DataBindingViewHolder<NoticeMyMessageItemBinding, MyMessage> {

        @Override
        public void onBind(final MyMessage item, int viewType) {
            super.onBind(item, viewType);
            final String date = Util.getDateFormat("yyyy-MM-dd", item.getTimestamp());
            if(date != null) {
                mBinding.setTime(date);
            }
            mBinding.setUsername(item.getFromUsername());
            int resultData = item.getResult();
            if(resultData != ProtocolConstants.JsonValue.ACTION_UN_HANDLE) {
                mBinding.setAction(Integer.valueOf(resultData));
            } else {
                mBinding.setAction(ProtocolConstants.JsonValue.ACTION_UN_HANDLE);
            }

            mBinding.agreeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    long myId = PluginStubBus.getInt(PluginConstants.Plugin.PLUGIN_NAME_APP, PluginConstants.App.DataKey.GET_USER_ID, -1);
                    String jsonData = JsonManager.createResponseToAddRequest(myId, item.getFromUserId(), true);
                    NetSceneSyncJson sceneSyncJson = new NetSceneSyncJson(jsonData, new NetSceneCallbackLoadingWrapper<Racecar.SyncJsonResponse>() {
                        @Override
                        public void onResponse(IRequest request, Racecar.SyncJsonResponse result) {
                            if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                                String messageContent = v.getContext().getString(R.string.notice_add_friend_successfully, item.getFromUsername());
                                BPDialogManager.showCongratulationDialog(v.getContext(), messageContent);
                                mBinding.setAction(ProtocolConstants.JsonValue.ACTION_PASS);
                                item.setResult(ProtocolConstants.JsonValue.ACTION_PASS);
                                item.setHasRead(true);
                                ThreadPool.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyMessageStorage messageDao = StorageManager.getMgr().getMyMessageStorage();
                                        boolean result = messageDao.update(item);
                                        Log.i(TAG, "update message %d operation %b", item.getId(), result);
                                    }
                                });
                            } else {
                                ToastUtil.show(R.string.notice_add_friend_failed);
                                Log.d(TAG, "add friend failed.");
                            }
                        }
                    });
                    new NetSceneLoadingWrapper(sceneSyncJson).doScene();
                }
            });


            mBinding.ignoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setResult(ProtocolConstants.JsonValue.ACTION_IGNORE);
                    item.setHasRead(true);
                    mBinding.setAction(ProtocolConstants.JsonValue.ACTION_IGNORE);
                    ThreadPool.post(new Runnable() {
                        @Override
                        public void run() {
                            MyMessageStorage messageDao = StorageManager.getMgr().getMyMessageStorage();
                            boolean result = messageDao.update(item);
                            Log.i(TAG, "update message %d operation %b", item.getId(), result);
                        }
                    });

                }
            });
        }



    }
}