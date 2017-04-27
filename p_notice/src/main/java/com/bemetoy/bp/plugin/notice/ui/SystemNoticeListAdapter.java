package com.bemetoy.bp.plugin.notice.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.table.SysMessage;
import com.bemetoy.bp.plugin.notice.R;
import com.bemetoy.bp.plugin.notice.databinding.NoticeSystemMessageItemBinding;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;
import com.bemetoy.stub.app.WebViewUI;
import com.bemetoy.stub.util.AppUtil;
import com.bemetoy.stub.util.JsonManager;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by albieliang on 16/5/14.
 */
public class SystemNoticeListAdapter extends ExtRecyclerViewAdapter<SysMessage> {

    private LayoutInflater mInflater;


    public SystemNoticeListAdapter(LayoutInflater inflater, String category) {
        mInflater = inflater;
    }

    @Override
    public IViewHolder<SysMessage> onCreateViewHolder(View view, int viewType) {
        return new SysMessageViewHolder();
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return mInflater.inflate(R.layout.notice_system_message_item, parent, false);
    }

    /**
     *
     */
    private static class SysMessageViewHolder extends DataBindingViewHolder<NoticeSystemMessageItemBinding, SysMessage> {

        @Override
        public void onBind(SysMessage item, int viewType) {
            super.onBind(item, viewType);
            switch (item.getMethod()) {
                case ProtocolConstants.MessageType.NOTICE:
                    bindNotice(item);
                    break;
                case ProtocolConstants.MessageType.NOTICE_GAME:
                    bindGameNotice(item);
                    break;
                case ProtocolConstants.MessageType.NOTICE_REWARD:
                    bindReward(item);
                    break;
            }
        }

        private void bindNotice(SysMessage item){
            JSONObject jsonObject = JsonManager.convertString2Json(item.getContent());
            if(jsonObject == null) {
                return;
            }

            String time = JsonManager.getFiled(jsonObject, ProtocolConstants.JsonFiled.TIME_STAMP);
            final Date date = Util.getDate("yyyy-MM-dd hh:mm:ss", time);
            if(date != null) {
                mBinding.setTime(Util.getDateFormat("yyyy/MM/dd", date.getTime()));
            }

            final String title = JsonManager.getFiled(jsonObject, ProtocolConstants.JsonFiled.TITLE);
            mBinding.setContent(title);
            final String content = JsonManager.getFiled(jsonObject, ProtocolConstants.JsonFiled.CONTENT);
            mBinding.queryDetailBtn.setBackgroundResource(R.drawable.notice_query_detail_drawable);
            mBinding.queryDetailBtn.setVisibility(View.VISIBLE);
            mBinding.queryDetailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!AppUtil.checkNetworkFirst((Activity) v.getContext())) {
                        return;
                    }

                    if(!Util.isNullOrNil(content)) {
                        Intent intent = new Intent(v.getContext(), WebViewUI.class);
                        intent.putExtra(ProtocolConstants.IntentParams.SHOW_BACK, false);
                        intent.putExtra(ProtocolConstants.IntentParams.URL_INFO, content);
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }

        private void bindGameNotice(SysMessage item) {
            JSONObject jsonObject = JsonManager.convertString2Json(item.getContent());
            if(jsonObject == null) {
                return;
            }

            String time = JsonManager.getFiled(jsonObject, ProtocolConstants.JsonFiled.TIME_STAMP);
            final Date date = Util.getDate("yyyy-MM-dd hh:mm:ss", time);
            if(date != null) {
                mBinding.setTime(Util.getDateFormat("yyyy/MM/dd", date.getTime()));
            }
            mBinding.queryDetailBtn.setBackgroundResource(R.drawable.notice_query_detail_drawable);
            mBinding.queryDetailBtn.setVisibility(View.VISIBLE);
            String content = JsonManager.getFiled(jsonObject, ProtocolConstants.JsonFiled.TITLE);
            mBinding.setContent(content);
            final int gameId = JsonManager.getInt(jsonObject, ProtocolConstants.JsonFiled.GAME_ID, -1);
            mBinding.queryDetailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(ProtocolConstants.IntentParams.GAME_ID, gameId);
                    PluginStubBus.doAction(v.getContext(), PluginConstants.Plugin.PLUGIN_NAME_P_GAMES, PluginConstants.Games.Action.CMD_START_GAME_DETAIL, 0, bundle);
                }
            });
        }

        private void bindReward(SysMessage item) {
            JSONObject jsonObject = JsonManager.convertString2Json(item.getContent());
            if(jsonObject == null) {
                return;
            }

            String time = JsonManager.getFiled(jsonObject, ProtocolConstants.JsonFiled.TIME_STAMP);
            final Date date = Util.getDate("yyyy-MM-dd hh:mm:ss", time);
            if(date != null) {
                mBinding.setTime(Util.getDateFormat("yyyy/MM/dd", date.getTime()));
            }

            String content = JsonManager.getFiled(jsonObject, ProtocolConstants.JsonFiled.TITLE);
            mBinding.setContent(content);
            mBinding.queryDetailBtn.setVisibility(View.INVISIBLE);
        }

    }
}