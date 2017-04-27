package com.bemetoy.bp.uikit.widget.recyclerview;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by albieliang on 16/3/22.
 */
public abstract class ExtRecyclerViewAdapter<T> extends BaseRecyclerViewAdapter<T> {

    public ExtRecyclerViewAdapter() {
    }

    @Override
    public final T getItem(int position) {
        T item = super.getItem(position);
        if (item != null && shouldRefillDataItem(position, item)) {
            fillDataItem(position, item);
        }
        return item;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<T> holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public final BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = createItemView(parent, viewType);
        BaseViewHolder<T> holder = new ViewHolderProxy<T>(itemView, viewType, onCreateViewHolder(itemView, viewType));
        holder.onCreateBinding(itemView, viewType);
        return holder;
    }

    /**
     *
     * @param item
     * @return
     */
    protected boolean shouldRefillDataItem(int position, T item) {
        return false;
    }

    /**
     *
     * @param position
     * @param item
     */
    protected void fillDataItem(int position, T item) {
        // Do nothing
    }

    /**
     *
     * @param view
     * @param viewType
     * @return
     */
    public abstract IViewHolder<T> onCreateViewHolder(View view, int viewType);

    /**
     * Just a proxy.
     *
     * @param <T>
     */
    static class ViewHolderProxy<T> extends BaseViewHolder<T> {

        private IViewHolder<T> mViewHolder;

        public ViewHolderProxy(View itemView, int viewType, IViewHolder<T> holder) {
            super(itemView, viewType);
            mViewHolder = holder;
        }

        @Override
        public void onCreateBinding(View itemView, int viewType) {
            super.onCreateBinding(itemView, viewType);
            if (mViewHolder != null) {
                mViewHolder.onCreateBinding(itemView, viewType);
            }
        }

        @Override
        public void onBind(T item, int viewType) {
            super.onBind(item, viewType);
            if (mViewHolder != null) {
                mViewHolder.onBind(item, viewType);
            }
        }

        @Override
        public void onRecycle() {
            super.onRecycle();
            if (mViewHolder != null) {
                mViewHolder.onRecycle();
            }
        }
    }

    /**
     *
     * @param <T>
     */
    public static class ViewHolderAdapter<T> implements IViewHolder<T> {

        @Override
        public void onCreateBinding(View itemView, int viewType) {
        }

        @Override
        public void onBind(T item, int viewType) {
        }

        @Override
        public void onRecycle() {
        }
    }

    /**
     *
     * @param <DataBinding>
     * @param <T>
     */
    public static class DataBindingViewHolder<DataBinding extends ViewDataBinding, T> extends ViewHolderAdapter<T> {

        private T mItem;
        private int mViewType;
        protected DataBinding mBinding;

        @Override
        public void onCreateBinding(View itemView, int viewType) {
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void onBind(T item, int viewType) {
            mItem = item;
        }

        protected T getItem() {
            return mItem;
        }

        protected int getViewType() {
            return mViewType;
        }
    }
}
