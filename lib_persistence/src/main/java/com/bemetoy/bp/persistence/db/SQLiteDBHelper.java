package com.bemetoy.bp.persistence.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bemetoy.bp.sdk.tools.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by albieliang on 16/3/30.
 */
public abstract class SQLiteDBHelper {

    private static final String TAG = "DB.SQLiteHelper";

    private SQLiteDatabase mDB;
    protected Context mContext;

    /**
     *
     * @param context
     * @param path
     * @param version
     */
    public SQLiteDBHelper(Context context, String path, int version) {
        if(context == null) {
            throw new IllegalArgumentException("Context is null.");
        }
        mContext = context;
        if (version < 1)
            throw new IllegalArgumentException("Version can't be smaller than 1.");
        if (path == null || path.equals("")) {
            throw new IllegalArgumentException("Path is illegal,maybe is null or nil.");
        }
        File file = new File(path);
        if (!file.isFile()) {
            boolean r = false;
            try {
                if (!file.exists()) {
                    File parent = file.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }
                    r = file.createNewFile();
                }
            } catch (IOException e) {
                Log.e(TAG, "create database file(%s) failed, exception : %s", path, e);
            }
            if (!r) {
                throw new RuntimeException("create database file failed.");
            }
        }
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file, null);
        if (db == null) {
            throw new IllegalArgumentException("Can't open or create the database.");
        }
        int mVersion = db.getVersion();
        if (mVersion != version) {
            db.beginTransaction();
            if (mVersion == 0) {
                onCreate(db);
            } else if (version < mVersion) {
                throw new IllegalArgumentException("New version must bigger than the old one.");
            } else {
                onUpgrade(db, mVersion, version);
            }
            db.setVersion(version);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        this.mDB = db;
    }

    public abstract void onCreate(SQLiteDatabase db);

    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion,
                                   int newVersion);

    public SQLiteDatabase getDatabase() {
        return mDB;
    }
}
