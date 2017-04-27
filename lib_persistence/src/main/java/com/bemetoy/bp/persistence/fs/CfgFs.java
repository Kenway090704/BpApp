package com.bemetoy.bp.persistence.fs;

import com.bemetoy.bp.persistence.fs.base.KeyValueAccessor;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by albieliang on 16/4/2.
 *
 * Config file system.
 */
public final class CfgFs implements KeyValueAccessor<Integer> {

	private static final String TAG = "Stg.CfgFs";

	private Map<Integer, Object> mKeyValues;
	private String mFilePath = "";
	private volatile boolean mWriteLocked;
	private boolean mOccurExceptionWhenOpen;


	public CfgFs(String filePath) {
		if (!new File(filePath).exists()) {
			Log.e(TAG, "CfgFs not exit path[%s]", filePath);
		}
		this.mFilePath = filePath;
		openCfg();
		mWriteLocked = false;
	}

	public synchronized void lockWrite() {
		mWriteLocked = true;
	}

	public synchronized void unlockWrite() {
		mWriteLocked = false;
		writeCfg();
	}

	@SuppressWarnings("unchecked")
	private synchronized void openCfg() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			File file = new File(mFilePath);
			if (!file.exists()) {
				boolean created = file.createNewFile();
				Log.w(TAG, "CfgFs openCfg not exit path[%s], created[%b]", mFilePath, created);
			}
			if (file.length() == 0) {
				mKeyValues = new HashMap<Integer, Object>();
				Log.w(TAG, "CfgFs openCfg file len == 0 path[%s]", mFilePath);
				return;
			}
			fis = new FileInputStream(file);

			ois = new ObjectInputStream(fis);
			mKeyValues = (Map<Integer, Object>) ois.readObject();
			mOccurExceptionWhenOpen = false;
			Log.i(TAG, "openCfg end, keys count:%d", mKeyValues.size());
		} catch (Exception e) {
			mKeyValues = new HashMap<Integer, Object>();
			Log.e(TAG, "openCfg Exception : %s", Util.getStack(e));
			mOccurExceptionWhenOpen = true;
		} finally {
			try {
				if (ois != null) {
					ois.close();
					ois = null;
				}
				if (fis != null) {
					fis.close();
					fis = null;;
				}
			} catch (Exception e) { }
		}
	}

	/**
	 * Note：currently the config file maybe invoked in different process.
	 * So Add the {@link FileLock}
	 */
	private synchronized void writeCfg() {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		FileLock fileLock = null;
		try {
			long begin = System.currentTimeMillis();
			fos = new FileOutputStream(mFilePath);
			oos = new ObjectOutputStream(fos);
			fileLock = fos.getChannel().tryLock();
			oos.writeObject(mKeyValues);
			fos.flush();
			long end = System.currentTimeMillis();
			Log.d(TAG, "writeCfg end, keys count : %d time : %d", mKeyValues.keySet().toArray().length , end - begin);
		} catch (IOException e) {
			Log.e(TAG , "exception : %s" , Util.getStack(e));
		} finally {
			try {
				if (oos != null) {
					oos.close();
					oos = null;
				}
				if (fos != null) {
					fos.close();
					fos = null;
				}

				if(fileLock != null && fileLock.isValid()) {
					fileLock.release();
					fileLock = null;
				}
			} catch(Exception e) {
			}
		}
	}

	public synchronized void reset() {
		Log.i(TAG, "CfgFs reset ");
		File file = new File(mFilePath);
		if (file.exists()) {
			file.delete();
		}
		mKeyValues = new HashMap<Integer, Object>();
	}

	public boolean isOccurExceptionWhenOpen() {
		return mOccurExceptionWhenOpen;
	}

	/**
	 *
	 * @param key
	 * @param value
     */
	public synchronized void set(Integer key, Object value) {
		mKeyValues.put(key, value);
		if (!mWriteLocked) {
			writeCfg();
		}
	}

	/**
	 *
	 * @param key
	 * @return
     */
	@Override
	public synchronized Object get(Integer key) {
		return mKeyValues.get(key);
	}

	/**
	 *
	 * @param key
	 * @param def
     * @return
     */
	@Override
	public synchronized Object get(Integer key, Object def) {
		Object obj = mKeyValues.get(key);
		if (obj == null) {
			return def;
		}
		return obj;
	}

	/**
	 *
	 * @param key
	 * @param val
     */
	@Override
	public synchronized void setInt(Integer key, int val) {
		set(key, Integer.valueOf(val));
	}

	/**
	 *
	 * @param key
	 * @param def
     * @return
     */
	@Override
	public synchronized int getInt(Integer key, int def) {
		Object obj = get(key);
		if (obj == null || !(obj instanceof Integer)) {
			return def;
		}
		return (Integer) obj;
	}

	/**
	 *
	 * @param key
	 * @param val
     */
	@Override
	public synchronized void setLong(Integer key, long val) {
		set(key, Long.valueOf(val));
	}

	/**
	 *
	 * @param key
	 * @param def
     * @return
     */
	@Override
	public synchronized long getLong(Integer key, long def) {
		Object obj = get(key);
		if (obj == null || !(obj instanceof Long)) {
			return def;
		}
		return (Long) obj;
	}

	/**
	 *
	 * @param key
	 * @param val
     */
	@Override
	public synchronized void setFloat(Integer key, float val) {
		set(key, Float.valueOf(val));
	}

	/**
	 *
	 * @param key
	 * @param def
     * @return
     */
	@Override
	public synchronized float getFloat(Integer key, float def) {
		Object obj = get(key);
		if (obj == null || !(obj instanceof Float)) {
			return def;
		}
		return (Float) obj;
	}

	/**
	 *
	 * @param key
	 * @param val
     */
	@Override
	public synchronized void setDouble(Integer key, double val) {
		set(key, Double.valueOf(val));
	}

	/**
	 *
	 * @param key
	 * @param def
     * @return
     */
	@Override
	public synchronized double getDouble(Integer key, double def) {
		Object obj = get(key);
		if (obj == null || !(obj instanceof Double)) {
			return def;
		}
		return (Double) obj;
	}

	/**
	 *
	 * @param key
	 * @param val
     */
	@Override
	public synchronized void setString(Integer key, String val) {
		set(key, val);
	}

	/**
	 *
	 * @param key
	 * @param def
     * @return
     */
	@Override
	public synchronized String getString(Integer key, String def) {
		Object obj = get(key);
		if (obj == null || !(obj instanceof String)) {
			return def;
		}
		return (String) obj;
	}
}
