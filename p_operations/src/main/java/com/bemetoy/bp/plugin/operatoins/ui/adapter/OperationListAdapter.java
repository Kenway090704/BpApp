package com.bemetoy.bp.plugin.operatoins.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.operatoins.R;
import com.bemetoy.bp.plugin.operatoins.databinding.UiWelfareItemBinding;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.utils.NetWorkUtil;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.stub.databinding.UiNetworkIssueDialogBinding;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;
import com.bemetoy.stub.app.WebViewUI;

import java.util.Date;

/**
 * Created by Tom on 2016/5/11.
 */
public class OperationListAdapter extends ExtRecyclerViewAdapter<Racecar.Operation> {

    private Context mContext;
    private float itemWidth;

    public OperationListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public IViewHolder<Racecar.Operation> onCreateViewHolder(View view, int viewType) {
        return new WelfareItemVH();
    }

    public void setItemWidth(float itemWidth) {
        this.itemWidth = itemWidth;
    }


    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.ui_welfare_item, parent, false);
    }

    private class WelfareItemVH extends DataBindingViewHolder<UiWelfareItemBinding, Racecar.Operation> {

        @Override
        public void onCreateBinding(View itemView, int viewType) {
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if (itemWidth > 0) {
                params.width = (int)itemWidth;
            }
            itemView.setLayoutParams(params);
            super.onCreateBinding(itemView, viewType);
            params = mBinding.welfareIm.getLayoutParams();
            params.width = (int)itemWidth;
            params.height = (int)(itemWidth * 1.2f);
            mBinding.welfareIm.setLayoutParams(params);
        }

        @Override
        public void onBind(final Racecar.Operation item, int viewType) {
            super.onBind(item, viewType);
            mBinding.setOperation(item);
            Date date = Util.getDate("yyyy-MM-dd hh:mm:ss", item.getTime());
            mBinding.welfareDateTv.setText(Util.getDateFormat("yyyy/MM/dd", date.getTime()));
            mBinding.detailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!NetWorkUtil.isConnected(v.getContext())) {
                        final BpDialog<UiNetworkIssueDialogBinding> dialog = new BpDialog(v.getContext(), R.layout.ui_network_issue_dialog);
                        dialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        return;
                    }

                    Intent intent = new Intent(mContext, WebViewUI.class);
                    intent.putExtra(ProtocolConstants.IntentParams.URL_INFO, item.getContent());
                    intent.putExtra(ProtocolConstants.IntentParams.SHOW_BACK, false);
                    mContext.startActivity(intent);
                }
            });
        }
    }

}
