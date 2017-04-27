package com.bemetoy.bp.plugin.friends.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.friends.R;
import com.bemetoy.bp.plugin.friends.databinding.UiFriendsItemBinding;
import com.bemetoy.bp.uikit.widget.recyclerview.BaseRecyclerViewAdapter;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;
import com.bemetoy.stub.account.AccountLogic;

/**
 * Created by Tom on 2016/4/29.
 */
public class SearchFriendListAdapter extends ExtRecyclerViewAdapter<Racecar.AccountInfo> {

    private static final String TAG = "Friends.SearchFriendListAdapter";

    private Context mContext;

    public SearchFriendListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public BaseRecyclerViewAdapter.IViewHolder<Racecar.AccountInfo> onCreateViewHolder(View view, int viewType) {
        return new FriendVH();
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.ui_friends_item, parent, false);
    }

    private class FriendVH extends ExtRecyclerViewAdapter.DataBindingViewHolder<UiFriendsItemBinding, Racecar.AccountInfo> {

        @Override
        public void onCreateBinding(View itemView, int viewType) {
            super.onCreateBinding(itemView, viewType);
            mBinding.userAvatarIm.setVisibility(View.GONE);
        }

        @Override
        public void onBind(Racecar.AccountInfo item, int viewType) {
            mBinding.setAccInfo(item);
            mBinding.setBaseUrl(AccountLogic.getBaseAvatarUrl());
        }
    }

}
