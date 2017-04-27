package com.bemetoy.bp.persistence.db.base;

import android.database.Cursor;

import java.util.List;

/**
 * 
 * @author AlbieLiang
 *
 * @param <T>
 */
public interface DbOperation<T> {

	boolean execSQL(String sql);

	boolean insert(T item);

	boolean insert(boolean notify, T item);

	boolean delete(long rowId);

	boolean delete(boolean notify, long rowId);

	boolean delete(String[] keys, Object[] values);

	boolean delete(boolean notify, String[] keys, Object[] values);

	boolean delete(T item);

	boolean delete(boolean notify, T item);

	T get(long rowId);

	List<T> get(String[] keys, Object[] values);

	Cursor get(String [] keys, Object[] values, String order, String limit);

	Cursor get(String [] cols, String [] keys, Object[] values, String order, String limit);

	boolean update(T item);

	boolean update(boolean notify, T item);

}
