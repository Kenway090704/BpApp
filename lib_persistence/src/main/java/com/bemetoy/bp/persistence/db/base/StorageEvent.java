package com.bemetoy.bp.persistence.db.base;

/**
 * 
 * @author AlbieLiang
 *
 */
public class StorageEvent {
	
	public static final int ACTION_INSERT = 1;
	public static final int ACTION_DELETE = 2;
	public static final int ACTION_UPDATE = 3;
	public static final int ACTION_BATCH_INSERT = 4;
	public static final int ACTION_BATCH_DELETE = 5;
	public static final int ACTION_BATCH_UPDATE = 6;
	
	private String mTable;
	protected int mAction;
	protected long mRowId;
	protected DatabaseItem mItem;
	protected Object[] mArgs;
	
	public StorageEvent(String table) {
		mTable = table;
	}
	
	public int getAction() {
		return mAction;
	}
	
	public String getTable() {
		return mTable;
	}
	
	public long getRowId() {
		return mRowId;
	}
	
	public DatabaseItem getVigorDBItem() {
		return mItem;
	}
	
	public Object[] getArgs() {
		return mArgs;
	}
}