package com.bemetoy.bp.plugin.friends.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.friends.R;
import com.bemetoy.bp.plugin.friends.databinding.UiSearchFriendBinding;
import com.bemetoy.bp.plugin.friends.model.FriendLogic;
import com.bemetoy.bp.plugin.friends.model.NetSceneSearchFriends;
import com.bemetoy.bp.plugin.friends.ui.adapter.SearchFriendListAdapter;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.utils.KeyBoardUtil;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.VRecyclerView;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.network.NetSceneLoadingWrapper;
import com.bemetoy.stub.ui.BPDialogManager;

/**
 * Created by Tom on 2016/4/28.
 */
public class SearchFriendUI extends BaseDataBindingActivity<UiSearchFriendBinding> {

    private static final String TAG = "Friends.SearchFriendUI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final SearchFriendListAdapter friendListAdapter = new SearchFriendListAdapter(this);
        mBinding.resultRv.setAdapter(friendListAdapter);
        mBinding.resultRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.resultRv.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));

        mBinding.resultRv.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Racecar.AccountInfo userInfo = friendListAdapter.getItem(position);
                boolean isMyFriend = FriendLogic.isMyFriend(userInfo.getUserId());
                Bundle bundle = new Bundle();
                bundle.putSerializable(ProtocolConstants.IntentParams.USER_INFO, userInfo);
                if(isMyFriend) {
                    PluginStubBus.doAction(SearchFriendUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_FRIENDS,
                            PluginConstants.FRIENDS.Action.CMD_START_FRIEND_DETAIL, 0, bundle);
                } else {
                    PluginStubBus.doAction(SearchFriendUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_FRIENDS,
                            PluginConstants.FRIENDS.Action.CMD_START_USER_DETAIL, 0, bundle);
                }
            }
        });

        mBinding.usernameEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.searchLayout.setVisibility(View.VISIBLE);
                mBinding.usernameTv.setText(getString(R.string.search_user, mBinding.usernameEd.getText().toString()));
                friendListAdapter.clearData();
                friendListAdapter.notifyDataSetChanged();
            }
        });

        mBinding.usernameEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    mBinding.searchLayout.setVisibility(View.VISIBLE);
                    mBinding.usernameTv.setText(getString(R.string.search_user, mBinding.usernameEd.getText().toString()));
            }
        });


        mBinding.searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mBinding.usernameEd.getText().toString().trim();
                if(Util.isNullOrBlank(keyword)) {
                    return;
                }
                KeyBoardUtil.hide(SearchFriendUI.this, mBinding.usernameEd);
                NetSceneSearchFriends netSceneSearchFriends = new NetSceneSearchFriends(keyword,
                        new NetSceneCallbackLoadingWrapper<Racecar.FindFriendResponse>() {
                            @Override
                            public void onResponse(IRequest request, Racecar.FindFriendResponse result) {
                                if (result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                                    if (result.getAccountInfoCount() != 0) {
                                        friendListAdapter.setData(result.getAccountInfoList());
                                        friendListAdapter.notifyDataSetChanged();
                                        mBinding.searchLayout.setVisibility(View.GONE);
                                    } else {
                                        mBinding.searchLayout.setVisibility(View.GONE);
                                        BPDialogManager.showSorryDialog(SearchFriendUI.this,getString(R.string.none_relative_user));
                                    }
                                }
                            }
                        });
                new NetSceneLoadingWrapper(netSceneSearchFriends).doScene();
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_search_friend;
    }
}
