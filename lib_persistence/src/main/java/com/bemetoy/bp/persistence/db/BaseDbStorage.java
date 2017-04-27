package com.bemetoy.bp.persistence.db;

import android.database.Cursor;
import android.database.SQLException;

import com.bemetoy.bp.persistence.db.base.DatabaseItem;
import com.bemetoy.bp.persistence.db.base.DatabaseStorage;
import com.bemetoy.bp.persistence.db.base.DbOperation;
import com.bemetoy.bp.persistence.db.base.StorageEvent;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;

import junit.framework.Assert;

import java.util.LinkedList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;

/**
 * 
 * @author AlbieLiang
 *
 * @param <DBItem>
 * @param <PrimaryKeyType>
 */
public abstract class BaseDbStorage<DBItem extends DatabaseItem, PrimaryKeyType> implements DatabaseStorage<DBItem>, DbOperation<DBItem> {

	private static final String TAG = "DB.BaseDbStorage";

	private String mTableName;
	private AbstractDao<DBItem, PrimaryKeyType> mAbstractDao;
	private List<OnStorageChangeListener> mOnStorageChangedListeners;
	
	public BaseDbStorage(AbstractDao<DBItem, PrimaryKeyType> dao) {
		Assert.assertNotNull(dao);
		mOnStorageChangedListeners = new LinkedList<OnStorageChangeListener>();
		mAbstractDao = dao;
		mTableName = mAbstractDao.getTablename();
		Assert.assertFalse(Util.isNullOrNil(mTableName));
	}

	public Cursor getAll() {
		return mAbstractDao.getDatabase().query(mTableName, mAbstractDao.getAllColumns(), null, null, null, null, null);
	}

	public Cursor getAll(String [] cols, String selections, String [] args, String groupBy, String having, String orderBy, String limit ) {
		return mAbstractDao.getDatabase().query(mTableName, cols, selections, args, groupBy, having, orderBy, limit);
	}


	public int getCount() {
		String sql = "select count(*) from " + mTableName;
		Cursor cursor = rawQuery(sql);
		int count = 0;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
			cursor.close();
		}
		return count;
	}
	
	public void deleteAll() {
		mAbstractDao.deleteAll();
	}
	
	public Cursor rawQuery(String selection, String... selectionArgs) {
		return mAbstractDao.getDatabase().rawQuery(selection, selectionArgs);
	}

	/**
	 * Notify all of the {@link OnStorageChangeListener}.
	 *
	 * @param event
	 */
	protected void doNotify(StorageEvent event) {
		if (event == null) {
			return;
		}
		// TODO: 16/3/14 albieliang control thread
		for (OnStorageChangeListener l : mOnStorageChangedListeners) {
			l.onStorageChanged(mTableName, event);
		}
	}

	/**
	 * Add a {@link OnStorageChangeListener} into the {@link BaseDbStorage}.
	 *
	 * @param l
	 * @return
	 */
	public boolean addOnVStorageChangeListener(OnStorageChangeListener l) {
		if(mOnStorageChangedListeners.contains(l)) {
			Log.i(TAG, "listener has been registered");
			return true;
		}
		return mOnStorageChangedListeners.add(l);
	}

	/**
	 * Remove a {@link OnStorageChangeListener} from the {@link BaseDbStorage}.
	 *
	 * @param l
	 * @return
	 */
	public boolean removeOnVStorageChangeListener(OnStorageChangeListener l) {
		return mOnStorageChangedListeners.remove(l);
	}

	@Override
	public boolean execSQL(String sql) {
		try {
			mAbstractDao.getDatabase().execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean insert(DBItem item) {
		return insert(true, item);
	}

	@Override
	public boolean insert(boolean notify, DBItem item) {
		long rowId = 0;
		try {
			if (item == null) {
				Log.e(TAG, "insert by item failed, item is null.");
				return false;
			}
			rowId = mAbstractDao.insert(item);
		} catch (Exception e) {
			Log.e(TAG, "insert item to table '%s' throw exception: %s", mTableName, e.getMessage());
			return false;
		}

		Log.i(TAG, "insert item into table '%s', rowId : %d.", mTableName, rowId);
		if (notify) {
			doNotify(new StorageInsertEvent<DBItem>(mTableName, rowId, item));
		}
		return rowId > 0;
	}

	@Override
	public boolean delete(long rowId) {
		return delete(true, rowId);
	}

	@Override
	public boolean delete(boolean notify, long rowId) {
		if (rowId <= 0) {
			Log.e(TAG, "delete failed, invalid rowId(%d).", rowId);
			return false;
		}
		int r = mAbstractDao.getDatabase().delete(mTableName, DatabaseItem.COL_ROWID + "=?", new String[] { "" + rowId });
		Log.i(TAG, "delete by rowId(%d).(r : %d)", r);
		if (notify) {
			doNotify(new StorageDeleteEvent(mTableName, rowId));
		}
		return r > 0;
	}

	@Override
	public boolean delete(String[] keys, Object[] values) {
		return delete(true, keys, values);
	}

	@Override
	public boolean delete(boolean notify, String[] keys, Object[] values) {
		if (keys == null || values == null || keys.length != values.length) {
			Log.e(TAG, "delete failed, keys or values is null or invalid.");
			return false;
		}
		int r = mAbstractDao.getDatabase().delete(mTableName, joinDBKeys(keys), convert2DBValues(values));
		Log.i(TAG, "delete by rowId(%d).(r : %d)", r);
		if (notify) {
			doNotify(new StorageDeleteEvent(mTableName));
		}
		return r > 0;
	}

	@Override
	public boolean delete(DBItem item) {
		return delete(true, item);
	}

	@Override
	public boolean delete(boolean notify, DBItem item) {
		if (item == null) {
			Log.e(TAG, "delete by item and keys failed, item is null.");
			return false;
		}
		try {
			mAbstractDao.delete(item);
		} catch (Exception e) {
			return false;
		}
		Log.i(TAG, "delete item(%s).", item);
		if (notify) {
			doNotify(new StorageDeleteEvent(mTableName, item));
		}
		return true;
	}

	@Override
	public DBItem get(long rowId) {
		if (rowId <= 0) {
			Log.e(TAG, "get failed, invalid rowId(%d).", rowId);
			return null;
		}
		return mAbstractDao.loadByRowId(rowId);
	}

	@Override
	public List<DBItem> get(String[] keys, Object[] values) {
		if (keys == null || values == null || keys.length != values.length) {
			Log.e(TAG, "get failed, invalid rowId(%d).");
			return null;
		}
		List<DBItem> items = mAbstractDao.queryRaw(joinDBKeys(keys), convert2DBValues(values));
		Log.i(TAG, "get by keys.");
		return items;
	}


	@Override
	public Cursor get(String[] keys, Object[] values, String order, String limit) {
		if (keys == null || values == null || keys.length != values.length) {
			Log.e(TAG, "get failed, invalid rowId(%d).");
			return null;
		}

		final String argsValues [] = new String[values.length];
		for(int i = 0; i < values.length; i++) {
			argsValues[i] = String.valueOf(values[i]);
		}

		Cursor cursor = mAbstractDao.getDatabase().query(true, getTableName(),null, keyToSelection(keys), argsValues, null, null, order, limit);
		return cursor;
	}

	@Override
	public Cursor get(String[] cols, String[] keys, Object[] values, String order, String limit) {
		if (keys == null || values == null || keys.length != values.length) {
			Log.e(TAG, "get failed, invalid rowId(%d).");
			return null;
		}

		final String argsValues [] = new String[values.length];
		for(int i = 0; i < values.length; i++) {
			argsValues[i] = String.valueOf(values[i]);
		}

		Cursor cursor = mAbstractDao.getDatabase().query(true, getTableName(), cols, keyToSelection(keys), argsValues, null, null, order, limit);
		return cursor;
	}

	public DBItem getByPrimaryKey(PrimaryKeyType key) {
		if (key == null) {
			Log.e(TAG, "get by key failed, key is null.");
			return null;
		}
		return mAbstractDao.load(key);
	}
	
	@Override
	public boolean update(DBItem item) {
		return update(true, item);
	}

	@Override
	public boolean update(boolean notify, DBItem item) {
		if (item == null) {
			Log.e(TAG, "update by item and keys failed, item is null.");
			return false;
		}
		boolean r = false;
		try {
			mAbstractDao.update(item);
			r = true;
		} catch (Exception e) {
		}
		Log.i(TAG, "update by item.(r : %s)", r);
		if (notify) {
			doNotify(new StorageUpdateEvent(mTableName, item));
		}
		return true;
	}

	/**
	 * Batch insert items.
	 *
	 * @param items
	 * @param notify
	 */
	public void batchInsert(List<DBItem> items, boolean notify) {
		if (items == null || items.isEmpty()) {
			return;
		}
		for (DBItem item : items) {
			insert(false, item);
		}
		if (notify) {
			doNotify(new StorageBatchInsertEvent<DBItem>(getTableName(), items));
		}
	}

	/**
	 * Batch insert or update items.
	 *
	 * @param items
	 * @param notify
	 */
	public void batchInsertOrUpdate(List<DBItem> items, boolean notify) {
		if (items == null || items.isEmpty()) {
			return;
		}
		for (DBItem item : items) {
			if (!insert(false, item)) {
				update(false, item);
			}
		}
		if (notify) {
			doNotify(new StorageBatchInsertEvent<DBItem>(getTableName(), items));
		}
	}

	/**
	 * Batch delete items.
	 *
	 * @param items
	 * @param notify
	 */
	public void batchDelete(List<DBItem> items, boolean notify) {
		if (items == null || items.isEmpty()) {
			return;
		}
		for (DBItem item : items) {
			delete(false, item);
		}
		if (notify) {
			doNotify(new StorageBatchDeleteEvent<DBItem>(getTableName(), items));
		}
	}

	/**
	 * Batch update items.
	 *
	 * @param items
	 * @param notify
	 */
	public void batchUpdate(List<DBItem> items, boolean notify) {
		if (items == null || items.isEmpty()) {
			return;
		}
		for (DBItem item : items) {
			update(false, item);
		}
		if (notify) {
			doNotify(new StorageBatchUpdateEvent<DBItem>(getTableName(), items));
		}
	}

	public static String keyToSelection(String...keys) {
		StringBuilder builder = new StringBuilder();
		if (keys != null && keys.length > 0) {
			builder.append(keys[0]);
			builder.append("=?");
			for (int i = 1; i < keys.length; i++) {
				builder.append(" and ");
				builder.append(keys[i]);
				builder.append("=?");
			}
		}
		return builder.toString();
	}


	public static String joinDBKeys(String...keys) {
		StringBuilder builder = new StringBuilder();
		if (keys != null && keys.length > 0) {
			builder.append(" WHERE ");
			builder.append(keys[0]);
			builder.append("=?");
			for (int i = 1; i < keys.length; i++) {
				builder.append(" and ");
				builder.append(keys[i]);
				builder.append("=?");
			}
		}
		return builder.toString();
	}

	public static String[] convert2DBValues(Object[] values) {
		String[] r = null;
		if (values != null) {
			r = new String[values.length];
			Object val = null;
			for (int i = 0; i < values.length; i++) {
				val = values[i];
				if (val != null) {
					r[i] = val.toString();
				} else {
					r[i] = "NULL";
				}
			}
		} else {
			r = new String[0];
		}
		return r;
	}

	protected boolean isDataBaseClosed(){
		if(mAbstractDao != null && mAbstractDao.getDatabase() != null && mAbstractDao.getDatabase().isOpen()) {
			return false;
		}
		return true;
	}

	/**
	 *
	 * @return
     */
	public String getTableName() {
		return mTableName;
	}

	/**
	 * 
	 * @author AlbieLiang
	 *
	 */
	public interface OnStorageChangeListener {
		void onStorageChanged(String table, StorageEvent event);
	}
}
