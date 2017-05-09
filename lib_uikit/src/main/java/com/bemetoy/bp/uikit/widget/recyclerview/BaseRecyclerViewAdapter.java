package com.bemetoy.bp.uikit.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.uikit.widget.recyclerview.BaseRecyclerViewAdapter.BaseViewHolder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by albieliang on 16/3/19.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {

    private List<T> mItems;

    public BaseRecyclerViewAdapter() {
        mItems = new LinkedList<T>();
    }

    @Override
    public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = createItemView(parent, viewType);
        BaseViewHolder<T> holder = new BaseViewHolder<T>(itemView, viewType);
        holder.onCreateBinding(itemView, viewType);
        // 创建返回一个ViewHolde
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<T> holder, int position) {
        holder.onBindView(this, position);
        // 这里是刷新Item

    }

    @Override
    public void onViewRecycled(BaseViewHolder<T> holder) {
        holder.onRecycle();
    }

    @Override
    public int getItemCount() {
        //items有多少个数据
        return mItems.size();
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    public abstract View createItemView(ViewGroup parent, int viewType);

    public List<T> getItems() {
        return mItems;
    }

    public void setData(List<T> list) {
        mItems.clear();
        if (list == null || list.isEmpty()) {
            return;
        }
        mItems.addAll(list);
    }

    public void clearData() {
        mItems.clear();
    }

    public void appendData(List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        mItems.addAll(list);
    }

    public void insertData(List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        mItems.addAll(0, list);
    }


    /**
     * @author AlbieLiang
     */
    public static class BaseViewHolder<T> extends RecyclerView.ViewHolder implements IViewHolder<T> {

        protected int mViewType;

        public BaseViewHolder(final View itemView, int viewType) {
            super(itemView);
            mViewType = viewType;
        }

        public final void onBindView(BaseRecyclerViewAdapter<T> adapter, int position) {
            if (position >= adapter.getItems().size() || position < 0) {
                return;
            }
            onBind(adapter.getItem(position), mViewType);
        }

        @Override
        public void onCreateBinding(View itemView, int viewType) {
            // do nothing
        }

        /**
         * @param viewType
         */
        @Override
        public void onBind(T item, int viewType) {
            // do nothing
        }

        @Override
        public void onRecycle() {
            // do nothing
        }
    }

    /**
     * @param <T>
     */
    public interface IViewHolder<T> {

        void onCreateBinding(View itemView, int viewType);

        void onBind(T item, int viewType);

        void onRecycle();
    }
}
