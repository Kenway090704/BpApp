package com.bemetoy.bp.plugin.mall.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.R;
import com.bemetoy.bp.plugin.mall.databinding.UiMyOrderItemBinding;
import com.bemetoy.bp.plugin.mall.model.OrderItem;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;

/**
 * Created by Tom on 2016/5/11.
 */
public class MyOrderListAdapter extends ExtRecyclerViewAdapter<Racecar.OrderListResponse.Order> {

    private Context context;

    public MyOrderListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public IViewHolder<Racecar.OrderListResponse.Order> onCreateViewHolder(View view, int viewType) {
        return new OrderVH();
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(context).inflate(R.layout.ui_my_order_item, parent, false);
    }

    private class OrderVH extends DataBindingViewHolder<UiMyOrderItemBinding, Racecar.OrderListResponse.Order> {
        @Override
        public void onBind(Racecar.OrderListResponse.Order item, int viewType) {
            super.onBind(item, viewType);
            mBinding.setIndex(getItems().indexOf(item) + 1);
            String date = Util.getDateFormat("yyyy/MM/dd", Util.getDate("yyyy-MM-dd hh:mm:ss", item.getCreateTime()).getTime());
            mBinding.setOrder(item);
            mBinding.setDate(date);
        }
    }
}
