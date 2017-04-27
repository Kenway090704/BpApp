package com.bemetoy.bp.plugin.friends.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.plugin.friends.R;
import com.bemetoy.bp.plugin.friends.databinding.UiFriendsItemBinding;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;

import java.io.File;

import static com.bemetoy.bp.autogen.protocol.Racecar.AccountInfo;

/**
 * Created by Tom on 2016/4/27.
 */
public class FriendListAdapter extends ExtRecyclerViewAdapter<Racecar.AccountInfo> {

    private static final String TAG = "Friends.FriendListAdapter";

    private String baseUrl;

    private Context mContext;

    public FriendListAdapter(Context context) {
        this.mContext = context;
        FileUtils.createFileIfNeed(new File(StorageConstants.COMM_SETTING_PATH));
        CfgFs cfgFs = new CfgFs(StorageConstants.COMM_SETTING_PATH);
        baseUrl = cfgFs.getString(StorageConstants.SETTING_KEY.AVATAR_BASE_URL, ProtocolConstants.ALPHA_AVATAR_BASE_URL);
    }

    @Override
    public IViewHolder<Racecar.AccountInfo> onCreateViewHolder(View view, int viewType) {
        return new FriendVH();
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(getItemCount() == 0) {
            return;
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.ui_friends_item, parent, false);
    }


    private class FriendVH extends DataBindingViewHolder<UiFriendsItemBinding, AccountInfo> {
        @Override
        public void onBind(AccountInfo item, int viewType) {
            mBinding.setAccInfo(item);
            mBinding.setBaseUrl(baseUrl);
        }
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }
}
