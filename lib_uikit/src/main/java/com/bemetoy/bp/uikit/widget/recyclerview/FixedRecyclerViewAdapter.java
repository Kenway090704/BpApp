package com.bemetoy.bp.uikit.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

import com.bemetoy.bp.uikit.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by albieliang on 16/4/9.
 */
class FixedRecyclerViewAdapter<T extends ViewHolder> extends RecyclerView.Adapter<T> {

    public static final int VIEW_TYPE_HEADER_VIEW = Integer.MAX_VALUE;
    public static final int VIEW_TYPE_FOOTER_VIEW = Integer.MAX_VALUE - 1;

    private RecyclerView.Adapter<T> mAdapter;
    private List<View> mHeaderViews;
    private List<View> mFooterViews;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private AdapterDataObserver mAdapterDataObserver;
    private View mEmptyView;
    private boolean isStatusCorrect = true; // whether there is valid data to display.

    public FixedRecyclerViewAdapter() {
        mHeaderViews = new LinkedList<View>();
        mFooterViews = new LinkedList<View>();
        mAdapterDataObserver = new AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
                checkEmptyView();
            }
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart, itemCount);
                checkEmptyView();
            }
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                onItemRangeChanged(positionStart, itemCount, payload);
                checkEmptyView();
            }
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                onItemRangeInserted(positionStart, itemCount);
                checkEmptyView();
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                onItemRangeRemoved(positionStart, itemCount);
                checkEmptyView();
            }
            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                onItemRangeMoved(fromPosition, toPosition, itemCount);
                checkEmptyView();
            }
        };
    }

    public void setEmptyView(View view){
        mEmptyView = view;
    }

    public void setStatusCorrect(boolean isStatusCorrect) {
        this.isStatusCorrect = isStatusCorrect;
        if(!isStatusCorrect && mEmptyView != null && getDataSize() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }

        if(isStatusCorrect) {
            checkEmptyView();
        }
    }

    protected void checkEmptyView() {
        if(mEmptyView != null) {
            if(getDataSize() > 0 && isStatusCorrect) {
                mEmptyView.setVisibility(View.INVISIBLE);
            } else {
                mEmptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public final int getItemViewType(int position) {
        if (!mHeaderViews.isEmpty() && position == 0) {
            return VIEW_TYPE_HEADER_VIEW;
        }
        if (!mFooterViews.isEmpty() && position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER_VIEW;
        }
        return mAdapter.getItemViewType(position - (mHeaderViews.isEmpty() ? 0 : 1));
    }

    /**
     * Just for RecyclerView.
     *
     * @return
     *
     */
    @Override
    public final int getItemCount() {
        int count = mAdapter.getItemCount();
        if (!mHeaderViews.isEmpty()) {
            count++;
        }
        if (!mFooterViews.isEmpty()) {
            count++;
        }
        return count;
    }

    private final int getDataSize(){
        return mAdapter.getItemCount();
    }


    @Override
    public void onBindViewHolder(final T holder, final int position) {
        if (!mHeaderViews.isEmpty() && position == 0) {
            return;
        }
        if (!mFooterViews.isEmpty() && position == getItemCount() - 1) {
            return;
        }
        final int realPosition = position - (mHeaderViews.isEmpty() ? 0 : 1);
        mAdapter.onBindViewHolder(holder, realPosition);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, realPosition, holder.getItemId());
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(holder.itemView, realPosition, holder.getItemId());
                }
                return false;
            }
        });
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_FOOTER_VIEW:
                ViewGroup vg = createFooterViewContainer(parent);
                vg.removeAllViews();
                for (View view : mFooterViews) {
                    vg.addView(view);
                }
                return (T) new FixedViewHolder(vg);
            case VIEW_TYPE_HEADER_VIEW:
                vg = createHeaderViewContainer(parent);
                vg.removeAllViews();
                for (View view : mHeaderViews) {
                    vg.addView(view);
                }
                return (T) new FixedViewHolder(vg);
            default:
                break;
        }
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    public void setTargetAdapter(RecyclerView.Adapter adapter) {
        if (mAdapter != null) {
            if (mAdapter.equals(adapter)) {
                return;
            }
            mAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
        }
    }

    public RecyclerView.Adapter getTargetAdapter() {
        return mAdapter;
    }

    public int getHeaderViewCount() {
        return mHeaderViews.size();
    }

    public int getFooterViewCount() {
        return mFooterViews.size();
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
    }

    public void addHeaderView(int index, View view) {
        mHeaderViews.add(index, view);
    }

    public void removeHeaderView(View view) {
        mHeaderViews.remove(view);
    }

    public void addFooterView(View view) {
        mFooterViews.add(view);
    }

    public void addFooterView(int index, View view) {
        mFooterViews.add(index, view);
    }

    public void removeFooterView(View view) {
        mFooterViews.remove(view);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        mOnItemLongClickListener = l;
    }

    /**
     *
     * @param parent
     * @return
     */
    ViewGroup createFooterViewContainer(ViewGroup parent) {
        ViewGroup group =  (ViewGroup) View.inflate(parent.getContext(), R.layout.recycler_view_item_container, null);
        group.setLayoutParams( new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return group;
    }

    /**
     *
     * @param parent
     * @return
     */
    ViewGroup createHeaderViewContainer(ViewGroup parent) {
        ViewGroup group =  (ViewGroup) View.inflate(parent.getContext(), R.layout.recycler_view_item_container, null);
        group.setLayoutParams( new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return group;
    }

    static class FixedViewHolder extends ViewHolder {

        public FixedViewHolder(View itemView) {
            super(itemView);
        }
    }
}
