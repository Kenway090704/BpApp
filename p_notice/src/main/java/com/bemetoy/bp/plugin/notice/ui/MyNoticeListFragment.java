package com.bemetoy.bp.plugin.notice.ui;

import android.support.v7.widget.LinearLayoutManager;

import com.bemetoy.bp.autogen.table.MyMessage;
import com.bemetoy.bp.plugin.notice.R;
import com.bemetoy.bp.plugin.notice.databinding.NoticeListBinding;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BaseDataBindingFragment;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.stub.db.MyMessageStorage;
import com.bemetoy.stub.db.StorageManager;
import com.bemetoy.stub.ui.LoadingDialogManager;

import java.util.List;

/**
 * Created by albieliang on 2016/5/14.
 */
public class MyNoticeListFragment extends BaseDataBindingFragment<NoticeListBinding> {

    private static final String TAG = "UI.SysNoticeListFragment";

    private MyNoticeListAdapter mAdapter;

    @Override
    public void init() {
        mAdapter = new MyNoticeListAdapter(getActivity().getLayoutInflater());
        mBinding.dataRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.dataRv.setAdapter(mAdapter);
        mBinding.dataRv.setEmptyView(mBinding.placeHolderTv);

        ThreadPool.post(new Runnable() {
            @Override
            public void run() {
                final MyMessageStorage messageStorage = StorageManager.getMgr().getMyMessageStorage();
                if(messageStorage != null) {
                    final List<MyMessage> myMessageList = messageStorage.loadMessageWithoutDuplicate();
                    Log.i(TAG, "local message size is %d", myMessageList.size());
                    ThreadPool.postToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.appendData(myMessageList);
                            mAdapter.notifyDataSetChanged();
                            LoadingDialogManager.countDown();
                        }
                    });

                }
            }
        });

        mBinding.dataRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
    }

    @Override
    public int getLayoutId() {
        return R.layout.notice_list;
    }
}
