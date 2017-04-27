package com.bemetoy.bp.plugin.mall.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.R;
import com.bemetoy.bp.plugin.mall.databinding.UiPartListItemBinding;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;

/**
 * Created by Tom on 2016/4/22.
 */
public class PartsListAdapter extends ExtRecyclerViewAdapter<Racecar.GoodsListResponse.Item> {

    private Context mContext;

    private int itemWidth = 0;

    public PartsListAdapter(Context context) {
        this.mContext = context;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    @Override
    public IViewHolder onCreateViewHolder(View view, int viewType) {
        return new PartsListVH();
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.ui_part_list_item, parent, false);
    }

    private class PartsListVH extends ExtRecyclerViewAdapter.DataBindingViewHolder<UiPartListItemBinding,
            Racecar.GoodsListResponse.Item> {

        @Override
        public void onCreateBinding(View itemView, int viewType) {
            super.onCreateBinding(itemView, viewType);
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if (itemWidth > 0) {
                params.width = itemWidth;
                params.height = (int) (itemWidth * 0.65);
            }
            itemView.setLayoutParams(params);
            itemView.invalidate();

            int imageSize = (int) (itemWidth * 0.65 * 0.8 * 0.8); // itemWidth * 0.65 是单元格的高度, * 0.8 是图片所在布局的大小。
            params = mBinding.partIm.getLayoutParams() ;
            params.height = imageSize;
            params.width = imageSize;
            mBinding.partIm.setLayoutParams(params);
        }

        @Override
        public void onBind(Racecar.GoodsListResponse.Item item, int viewType) {
            mBinding.setPart(item);
        }
    }
}
