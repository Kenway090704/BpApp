package com.bemetoy.bp.uikit.widget.listview;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.widget.listview.BaseDataItem.BaseViewHolder;
import com.bemetoy.bp.uikit.widget.listview.BaseDataItem.BaseViewItem;

import junit.framework.Assert;

import java.util.Collection;
import java.util.List;

/**
 * The Adapter support multiple style Item, see {@link BaseDataItem}
 * 
 * Created by albieliang on 16/3/23.
 * 
 */
public abstract class BaseMultiDataItemAdapter extends BaseAdapter implements OnScrollListener, OnItemClickListener {

	private final static String TAG = "BaseMutilDataItemAdapter";
	private SparseArray<BaseDataItem> mItemCache;
	protected Context mContext;
	private Runnable mNotifyDataSetChangedRunnable;
	private IOnDataItemClickListener mOnItemClickListener;

	public BaseMultiDataItemAdapter(Context context) {
		if (context == null) {
			throw new NullPointerException("context is null.");
		}
		mContext = context;
		mItemCache = new SparseArray<BaseDataItem>();
		mNotifyDataSetChangedRunnable = new Runnable() {

			@Override
			public void run() {
				notifyDataSetChanged();
			}
		};
	}


	public void setData(List<? extends BaseDataItem> dataItems) {
		if(Util.isNull(dataItems)) {
			Log.e(TAG, "data items is null");
			return;
		}
		clearData();
		for(int i = 0; i < dataItems.size(); i++) {
			BaseDataItem baseDataItem = createDataItem(i);
			mItemCache.put(i, baseDataItem);
		}
		refresh();
	}


	@Override
	public int getCount() {
		return mItemCache.size();
	}

	@Override
	public int getItemViewType(int position) {
		BaseDataItem baseDataItem = getItem(position);
		if (baseDataItem != null) {
			return getItem(position).getType();
		} else {
			Log.d(TAG, "getItemViewType: get data item fail, return unkown Type, totalCount(%d) , position(%d)", getCount(), position);
			return BaseDataItem.ViewType.UNKNOWN;
		}
	}

	@Override
	public int getViewTypeCount() {
		return BaseDataItem.ViewType.VIEW_TYPE_COUNT;
	}

	@Override
	public BaseDataItem getItem(int position) {
		if (position < 0 || position > getCount()) {
			Log.e(TAG, "The given position(%d) is illegal.", position);
			return null;
		}
		BaseDataItem baseDataItem = mItemCache.get(position);
		if (baseDataItem == null) {
			baseDataItem = createDataItem(position);
			mItemCache.put(position, baseDataItem);
		}
		return baseDataItem;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		long first = System.currentTimeMillis();
		BaseDataItem dataItem = getItem(position);
		if (dataItem == null) {
			Log.e(TAG, "DataItem is null.");
			return convertView;
		}
		long second = System.currentTimeMillis();
		BaseViewHolder holder = null;
		BaseViewItem viewItem = dataItem.getViewItem();
		if (viewItem == null) {
			Log.e(TAG, "ViewItem is null.");
			return convertView;
		}
		if (convertView == null) {
			convertView = viewItem.inflate(mContext, parent, convertView);
			holder = dataItem.getViewHolder();
			viewItem.attachViewHolder(convertView, holder);
			convertView.setTag(holder);
		} else {
			holder = (BaseViewHolder) convertView.getTag();
		}
		Object[] extraDatas = getExtDatas(position);
		Assert.assertNotNull(holder);
		if (!dataItem.hasFilled()) {
			dataItem.fillingData(mContext, holder, extraDatas);
		}
		long third = System.currentTimeMillis();
		viewItem.fillingView(mContext, holder, dataItem, extraDatas);
		long forth = System.currentTimeMillis();
		Log.d(TAG, "position %d, getItem %d, inflate %d, filling data %d", position, second - first, third - second, forth - third);
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		BaseDataItem item = getItem(position);
		if (item == null) {
			return;
		}
		BaseViewItem vItem = item.getViewItem();
		if (vItem == null) {
			return;
		}
		if (vItem.onItemClick(mContext, view, item, getExtDatas(position))) {
			return;
		}
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(parent, view, position, id);
		}
	}

	public static interface IOnDataItemClickListener {
		void onItemClick(AdapterView<?> parent, View view, int position, long id);
	}

	public void clearData() {
		mItemCache.clear();
		refresh();
	}

	public void refresh() {
		ThreadPool.postToMainThread(mNotifyDataSetChangedRunnable);
	}

	protected abstract BaseDataItem createDataItem(int position);

	protected abstract Object[] getExtDatas(int position);

	public IOnDataItemClickListener getOnDataItemClickListener() {
		return mOnItemClickListener;
	}

	public void setOnDataItemClickListener(IOnDataItemClickListener mOnItemClickListener) {
		this.mOnItemClickListener = mOnItemClickListener;
	}
}
