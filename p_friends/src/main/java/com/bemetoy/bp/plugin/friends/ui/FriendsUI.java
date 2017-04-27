package com.bemetoy.bp.plugin.friends.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bemetoy.bp.autogen.events.FriendsListUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.friends.R;
import com.bemetoy.bp.plugin.friends.databinding.UiFriendsBinding;
import com.bemetoy.bp.plugin.friends.model.FriendLogic;
import com.bemetoy.bp.plugin.friends.model.NetSceneGetFriends;
import com.bemetoy.bp.plugin.friends.ui.adapter.FriendListAdapter;
import com.bemetoy.bp.plugin.friends.ui.adapter.SimpleSectionedRvAdapter;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.bp.uikit.widget.recyclerview.DividerItemDecoration;
import com.bemetoy.bp.uikit.widget.recyclerview.VRecyclerView;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.ui.AccountChangeActivity;
import com.bemetoy.stub.ui.LoadingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tom on 2016/4/27.
 */
public class FriendsUI extends AccountChangeActivity<UiFriendsBinding> {


    private static final String MISC_SYMBOL = "#";
    private static final String MISC_STRING = "misc";

    private static final String TAG = "Friend.FriendsUI";
    private SimpleSectionedRvAdapter<Racecar.AccountInfo> simpleSectionedRvAdapter;
    private FriendListAdapter friendAdapter;
    private EventObserver eventObserver;
    private NetSceneGetFriends netSceneGetFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {

        eventObserver = new AccurateEventObserver<FriendsListUpdateEvent>() {

            @Override
            public void onReceiveEvent(FriendsListUpdateEvent event) {
                refreshFriendsList();
            }
        };
        RxEventBus.getBus().register(FriendsListUpdateEvent.ID, eventObserver);
        mBinding.setUserInfo(AccountLogic.getAccountInfo());
        friendAdapter = new FriendListAdapter(this);
        simpleSectionedRvAdapter = new SimpleSectionedRvAdapter(this,R.layout.ui_friends_setion_header, R.id.section_headr_tv, friendAdapter) ;
        mBinding.friendsRv.setEmptyView(mBinding.placeHolderTv);
        mBinding.friendsRv.setAdapter(simpleSectionedRvAdapter);
        refreshFriendsList();
        mBinding.friendsRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.friendsRv.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST, R.drawable.divider));
        mBinding.friendsRv.setOnItemClickListener(new VRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Racecar.AccountInfo accountInfo = simpleSectionedRvAdapter.getItem(position);
                if(accountInfo == null) {
                    return;
                }
                Intent intent = new Intent(FriendsUI.this, FriendDetailUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.USER_INFO, accountInfo);
                intent.putExtra(ProtocolConstants.IntentParams.USER_POSITION, position);
                startActivityForResult(intent, ProtocolConstants.RequestCode.ACTION);
            }
        });

        mBinding.headerBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.headerBar.avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginStubBus.doAction(FriendsUI.this,
                        PluginConstants.Plugin.PLUGIN_NAME_P_PERSONAL_CENTER,
                        PluginConstants.PersonalCenter.Action.CMD_START_PERSONAL_CENTER_UI, 0, null);
            }
        });


        mBinding.searchFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginStubBus.doAction(FriendsUI.this, PluginConstants.Plugin.PLUGIN_NAME_P_FRIENDS,
                        PluginConstants.FRIENDS.Action.CMD_START_SEARCH_FRIEND_UI, 0, null);
            }
        });
    }

    private void refreshFriendsList() {

        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

         netSceneGetFriends = new NetSceneGetFriends(new NetSceneCallbackLoadingWrapper<Racecar.GetFriendResponse>() {

             @Override
             public void onRequestFailed(IRequest request) {
                 mBinding.friendsRv.setStatusCorrect(false);
                 mBinding.placeHolderTv.setText(R.string.network_error_notice);
             }

             @Override
             public void onLocalNetworkIssue(IRequest request) {
                 super.onLocalNetworkIssue(request);
                 onRequestFailed(request);
             }

             @Override
            public void onResponse(IRequest request, Racecar.GetFriendResponse result) {
                 mBinding.friendsRv.setStatusCorrect(true);
                 mBinding.placeHolderTv.setText(R.string.friend_no_friends);
                if(result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    List<Racecar.AccountInfo> list = new ArrayList<>(result.getAccountInfoList());
                    Log.d(TAG, "friends size is %d", list.size());
                    FriendLogic.initFriendKeySet(result.getAccountInfoList()); //init my friends data.
                    Map<String,ArrayList<Racecar.AccountInfo>> data = getSortedMap(list);
                    SimpleSectionedRvAdapter.Section [] sections = new SimpleSectionedRvAdapter.Section [data.size()];
                    int lastSectionIndex = 0;
                    List<SimpleSectionedRvAdapter.Section> sectionList = new ArrayList();
                    for(Map.Entry<String,ArrayList<Racecar.AccountInfo>> entry : data.entrySet()) {
                        String key = entry.getKey();
                        if(key.equals(MISC_SYMBOL)) {
                            key = MISC_STRING;
                        }
                        sectionList.add(new SimpleSectionedRvAdapter.Section(lastSectionIndex, key));
                        lastSectionIndex += entry.getValue().size();
                    }
                    sections = sectionList.toArray(sections);
                    simpleSectionedRvAdapter.setSections(sections);
                    friendAdapter.setData(list);
                    friendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNetSceneEnd() {
                super.onNetSceneEnd();
                loadingDialog.dismiss();
            }
        });

        netSceneGetFriends.doScene();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ProtocolConstants.ResultCode.ACTION_DONE && requestCode == ProtocolConstants.RequestCode.ACTION) {
            refreshFriendsList();
        }
    }

    @Override
    protected void onDestroy() {
        RxEventBus.getBus().unregister(FriendsListUpdateEvent.ID, eventObserver);
        if(netSceneGetFriends != null) {
            netSceneGetFriends.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onAccountUpdate(Racecar.AccountInfo accountInfo) {
        mBinding.setUserInfo(accountInfo);
    }

    private Map<String, ArrayList<Racecar.AccountInfo>> getSortedMap(List<Racecar.AccountInfo> accountInfoList) {
        final Comparator pinyinComparator =  new Comparator<Racecar.AccountInfo>() {
            @Override
            public int compare(Racecar.AccountInfo lhs, Racecar.AccountInfo rhs) {
                return lhs.getPinyin().trim().toLowerCase().compareTo(rhs.getPinyin().trim().toLowerCase());
            }
        };

        Collections.sort(accountInfoList, pinyinComparator);
        int end = 0;
        for(Racecar.AccountInfo accountInfo :accountInfoList) {
            if(accountInfo.getPinyin().equals(MISC_SYMBOL)) {
                end++;
            }
        }

        if(end > 0 && end < accountInfoList.size()) {
            List<Racecar.AccountInfo> miscPart = new ArrayList(accountInfoList.subList(0, end));
            if(accountInfoList.removeAll(miscPart)) {
                accountInfoList.addAll(miscPart);
            }
        }

        final Map<String, ArrayList<Racecar.AccountInfo>> map = new LinkedHashMap();

        //todo Need refine
        for(Racecar.AccountInfo accountInfo : accountInfoList) {
            if(map.containsKey(accountInfo.getPinyin().toLowerCase())) {
                map.get(accountInfo.getPinyin().toLowerCase()).add(accountInfo);
            } else {
                map.put(accountInfo.getPinyin().toLowerCase(), new ArrayList());
                map.get(accountInfo.getPinyin().toLowerCase()).add(accountInfo);
            }
        }

        return map;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_friends;
    }
}
