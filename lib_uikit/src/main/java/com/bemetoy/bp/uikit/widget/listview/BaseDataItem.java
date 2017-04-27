package com.bemetoy.bp.uikit.widget.listview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * The Data Entity of the Item for the {@link BaseMultiDataItemAdapter}
 * 
 * Created by albieliang on 16/3/23.
 * 
 */
public abstract class BaseDataItem {

	private int type;
	protected Object data;
	protected boolean hasFilled;

	public BaseDataItem(int type, Object data) {
		this.type = type;
		this.data = data;
	}

	public int getType() {
		return type;
	}

	public Object getData() {
		return data;
	}

	public boolean hasFilled() {
		return hasFilled;
	}

	/**
	 * The method must return the ViewItem base the ViewType.
	 * 
	 * @return can not be null.
	 */
	public abstract BaseViewItem getViewItem();

	/**
	 * The method must return the ViewHolder base the ViewType.
	 * 
	 * @return can not be null.
	 */
	public abstract BaseViewHolder getViewHolder();

	public abstract void fillingData(Context context, BaseViewHolder holder, Object... extraDatas);

	public static final class ViewType {
		public static final int VIEW_TYPE_COUNT = 3;
		public static final int UNKNOWN = 0;
		public static final int ITEM_CATALOG = 1;
		public static final int ITEM_MORE = 2;
		// TODO: 16/3/28 albieliang, add more view type here and update VIEW_TYPE_COUNT.
	}

	public static abstract class BaseViewItem {
		public abstract View inflate(Context context, ViewGroup parent, View convertView);

		public abstract void fillingView(Context context, BaseViewHolder holder, BaseDataItem data, Object... extraDatas);

		public abstract void attachViewHolder(View convertView, BaseViewHolder holder);

		public abstract boolean onItemClick(Context context, View v, BaseDataItem data, Object... extraDatas);
	}

	public interface BaseViewHolder {

	}
}