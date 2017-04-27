package com.bemetoy.bp.uikit.widget.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.bemetoy.bp.sdk.reflect.ReflectField;

import java.util.List;

/**
 * Created by albieliang on 16/4/9.
 */
public class VRecyclerView extends RecyclerView {

    protected FixedRecyclerViewAdapter mAdapter;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public VRecyclerView(Context context) {
        super(context);
        init();
    }

    public VRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mAdapter = new FixedRecyclerViewAdapter();
        mAdapter.setOnItemClickListener(new com.bemetoy.bp.uikit.widget.recyclerview.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(VRecyclerView.this, view, position, id);
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new com.bemetoy.bp.uikit.widget.recyclerview.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position, long id) {
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(VRecyclerView.this, view, position, id);
                }
                return false;
            }
        });
        super.setAdapter(mAdapter);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter.setTargetAdapter(adapter);
    }

    public int getItemDecorationsCount(){
        List<ItemDecoration> itemDecorations = new ReflectField<List<ItemDecoration>>(this.getClass(),
                this, "mItemDecorations").getWithoutThrow();
        return itemDecorations.size();
    }

    public void setEmptyView(View view) {
        mAdapter.setEmptyView(view);
    }

    public void setStatusCorrect(boolean isStatusCorrect) {
        mAdapter.setStatusCorrect(isStatusCorrect);
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }



    public void addHeaderView(View view) {
        mAdapter.addHeaderView(view);
    }

    public void addHeaderView(int index, View view) {
        mAdapter.addHeaderView(index, view);
    }

    public void removeHeaderView(View view) {
        mAdapter.removeHeaderView(view);
    }

    public void addFooterView(View view) {
        mAdapter.addFooterView(view);
    }

    public void addFooterView(int index, View view) {
        mAdapter.addFooterView(index, view);
    }

    public void removeFooterView(View view) {
        mAdapter.removeFooterView(view);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        mOnItemLongClickListener = l;
    }

    /**
     *
     */
    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position, long id);
    }

    /**
     *
     */
    public interface OnItemLongClickListener {
        boolean onItemLongClick(RecyclerView parent, View view, int position, long id);
    }
}
