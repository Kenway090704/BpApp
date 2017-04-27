package com.bemetoy.bp.persistence.fs;

import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by Tom on 2016/5/17.
 */
public class FSConstants {

    private static final String TAG = "DB.DatabaseConstants";

    public static String getCfgPath(String seed) {
//        if (Util.isNullOrNil(seed)) {
//            Log.i(TAG, "get cfg path failed, seed is null or nil.");
//            return null;
//        }
        String path = StorageConstants.DATA_ROOT + "cfg";
        Log.v(TAG, "get cfg path(%s)", path);
        return path;
    }

}
