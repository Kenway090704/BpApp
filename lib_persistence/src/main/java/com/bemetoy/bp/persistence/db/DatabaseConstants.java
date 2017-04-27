package com.bemetoy.bp.persistence.db;

import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.arithmetic.MD5;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;

/**
 * 
 * @author AlbieLiang
 *
 */
public class DatabaseConstants {

	private static final String TAG = "DB.DatabaseConstants";

	public static final String APP_MAIN_DB_NAME = "bp_app.db";

	/**
	 *
	 * @param seed
	 * @return
     */
	public static String getDbPath(String seed) {
		if (Util.isNullOrNil(seed)) {
			Log.i(TAG, "get db path failed, seed is null or nil.");
			return null;
		}
		String path = MD5.getMessageDigest(seed.getBytes()) + "/" + APP_MAIN_DB_NAME;
		Log.v(TAG, "get database path(%s)", path);
		return path;
	}

	/**
	 *
	 * @param seed
	 * @return
     */
	public static String getDbRootPath(String seed) {
		if (Util.isNullOrNil(seed)) {
			Log.i(TAG, "get db path failed, seed is null or nil.");
			return null;
		}
		String path = StorageConstants.DATA_ROOT + "databases/" + MD5.getMessageDigest(seed.getBytes());
		Log.v(TAG, "get database path(%s)", path);
		return path;
	}

}
