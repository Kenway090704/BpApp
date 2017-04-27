package com.bemetoy.bp.plugin.notice.ui;

import android.support.v7.widget.LinearLayoutManager;

import com.bemetoy.bp.autogen.table.SysMessage;
import com.bemetoy.bp.plugin.notice.R;
import com.bemetoy.bp.plugin.notice.databinding.NoticeListBinding;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BaseDataBindingFragment;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.stub.db.StorageManager;
import com.bemetoy.stub.db.SysMessageStorage;
import com.bemetoy.stub.ui.LoadingDialogManager;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by albieliang on 2016/5/10.
 */
public class SysNoticeListFragment extends BaseDataBindingFragment<NoticeListBinding> {

    private static final String TAG = "UI.SysNoticeListFragment";
    private SystemNoticeListAdapter mAdapter;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    public void init() {
        Log.e(TAG, this.toString());
        mAdapter = new SystemNoticeListAdapter(getActivity().getLayoutInflater(), getString(R.string.tasks_center_online));
        mBinding.dataRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.dataRv.setAdapter(mAdapter);

        Subscription sub = Observable.create(new Observable.OnSubscribe<List<SysMessage>>() {
            @Override
            public void call(Subscriber<? super List<SysMessage>> subscriber) {
                final SysMessageStorage messageStorage = StorageManager.getMgr().getSysMessageStorage();
                if(messageStorage != null) {
                    List<SysMessage> myMessageList =  messageStorage.load(new String[]{
                            SysMessageStorage.COL_CONTENT, SysMessageStorage.COL_HAS_READ,SysMessageStorage.COL_METHOD, SysMessageStorage.COL_RESULT
                    }, new String[]{}, new String[]{},
                            "id desc", null);
                    subscriber.onNext(myMessageList);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Action1<List<SysMessage>>() {
            @Override
            public void call(List<SysMessage> messages) {
                mAdapter.appendData(messages);
                mAdapter.notifyDataSetChanged();
                LoadingDialogManager.countDown();
            }
        });
        subscriptions.add(sub);
        mBinding.placeHolderTv.setText(R.string.notice_no_system_msg);
        mBinding.dataRv.setEmptyView(mBinding.placeHolderTv);

        mBinding.dataRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
    }

    @Override
    public void onDestroyView() {
        subscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.notice_list;
    }
}
