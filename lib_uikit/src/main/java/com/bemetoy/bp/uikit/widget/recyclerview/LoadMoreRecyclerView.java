package com.bemetoy.bp.uikit.widget.recyclerview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by albieliang on 16/4/11.
 */
public class LoadMoreRecyclerView extends VRecyclerView {

    private static final String TAG = "Widget.LoadMoreRecyclerView";

    private View mLoadingView;
    private View mHeaderLoadingView;
    private IOnLoadingStateChangedListener mOnLoadingStateChangedListener;
    private boolean mIsLoadingShowing;
    private boolean mLoadingMoreAtTop;
    private MLayoutManager layoutManager;

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        layoutManager = new MLayoutManager(getContext());
        super.setLayoutManager(layoutManager);
        addOnScrollListener(new RecyclerView.OnScrollListener() {

            int lastVisibleItem = 0;
            int firstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isLoadingShowing()){

                    if(mOnLoadingStateChangedListener == null) {
                        return;
                    }

                    if(lastVisibleItem == mAdapter.getItemCount() - mAdapter.getFooterViewCount() - 1) {
                        mOnLoadingStateChangedListener.onLoadMore(LoadMoreRecyclerView.this, mAdapter.getTargetAdapter(), false);
                    }

                    if(mLoadingMoreAtTop && firstVisibleItem == 0) {
                        mOnLoadingStateChangedListener.onLoadMore(LoadMoreRecyclerView.this, mAdapter.getTargetAdapter(), true);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition() - getItemDecorationsCount();
                firstVisibleItem =  layoutManager.findFirstVisibleItemPosition();
            }
        });
    }

    public void enableScroll(boolean enable) {
        layoutManager.setScrollEnabled(enable);
    }

    @Override
    public void addHeaderView(View view) {
        int index = mAdapter.getHeaderViewCount() - 1;
        if (mHeaderLoadingView == null || index < 0) {
            super.addHeaderView(view);
            return;
        }
        super.addHeaderView(index, view);
    }

    @Override
    public void addFooterView(View view) {
        int index = mAdapter.getFooterViewCount() - 1;
        if (mLoadingView == null || index < 0) {
            super.addFooterView(view);
            return;
        }
        super.addFooterView(index, view);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        //super.setLayoutManager(layout);
    }

    public void setLoadingMoreAtTop (boolean allow) {
        mLoadingMoreAtTop = allow;
    }


    /**
     * Set a loading footer view.
     *
     * @param view
     *
     * @see #setFooterLoadingView(int)
     */
    public void setFooterLoadingView(View view) {
        if (mLoadingView == view) {
            return;
        }
        if (mLoadingView != null && !mLoadingView.equals(view)) {
            removeFooterView(mLoadingView);
        }
        mLoadingView = view;
        if (mLoadingView != null) {
            addFooterView(mLoadingView);
            mLoadingView.setVisibility(mIsLoadingShowing ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public int getFooterViewCount() {
        return mAdapter.getFooterViewCount();
    }

    public int getHeaderViewCount() {
        return mAdapter.getHeaderViewCount();
    }

    /**
     * Set a loading footer view by given layout id.
     *
     * @param id
     *
     * @see #setFooterLoadingView(View)
     */
    public void setFooterLoadingView(int id) {
        setFooterLoadingView(LayoutInflater.from(getContext()).inflate(id, this, false));
    }

    public void setHeaderLoadingView(View view) {
        if (mHeaderLoadingView == view) {
            return;
        }
        if (mHeaderLoadingView != null && !mHeaderLoadingView.equals(view)) {
            removeHeaderView(mHeaderLoadingView);
        }
        mHeaderLoadingView = view;
        if (mHeaderLoadingView != null) {
            addHeaderView(mHeaderLoadingView);
            mHeaderLoadingView.setVisibility(mIsLoadingShowing ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setHeaderLoadingView(@LayoutRes int id) {
        setHeaderLoadingView(LayoutInflater.from(getContext()).inflate(id, this, false));
    }


    public boolean isLoadingShowing() {
        return mIsLoadingShowing;
    }

    /**
     * Hide the loading view or not.
     * @param hide
     */
    public void hideLoadingView(boolean hide) {
        if(mLoadingView == null) {
            return;
        }
        if(hide) {
            mLoadingView.setVisibility(INVISIBLE);
            if(mHeaderLoadingView != null) {
                mHeaderLoadingView.setVisibility(GONE);
            }
        } else {
            mLoadingView.setVisibility(VISIBLE);
            if(mHeaderLoadingView != null) {
                mHeaderLoadingView.setVisibility(VISIBLE);
            }
        }
    }

    public void showLoading(boolean show) {
        if (mIsLoadingShowing == show) {
            return;
        }
        mIsLoadingShowing = show;
        //mLoadingView.setVisibility(mIsLoadingShowing ? View.VISIBLE : View.INVISIBLE);
    }

    public void setOnLoadingStateChangedListener(IOnLoadingStateChangedListener l) {
        mOnLoadingStateChangedListener = l;
    }

    /**
     *
     */
    public interface IOnLoadingStateChangedListener {
        void onLoadMore(LoadMoreRecyclerView parent, Adapter adapter, boolean isForward);
    }

    private static class MLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        public MLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}
