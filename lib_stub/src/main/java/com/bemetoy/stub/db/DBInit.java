package com.bemetoy.stub.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bemetoy.bp.persistence.db.SQLiteDBHelper;
import com.bemetoy.bp.persistence.db.base.DatabaseInit;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by albieliang on 16/3/30.
 *
 * TODO
 */
public class DBInit implements DatabaseInit {

    private static final String TAG = "DB.DBInit";

    private SQLiteOpenHelper mSQLiteOpenHelper;
    private ISQLiteDatabaseListener mDatabaseListener;
    private String mDatabasePath;

    @Override
    public SQLiteDatabase openOrCreateDatabase(final String name, final int version) {
        // TODO: 16/4/1 albieliang 
        if (mSQLiteOpenHelper == null) {
            mSQLiteOpenHelper = new SQLiteOpenHelper(ApplicationContext.getContext(), name, null, version) {
                @Override
                public void onCreate(SQLiteDatabase db) {
                    Log.i(TAG, "onCreate database '%s'(0x%x)", name, db.getVersion());
                    if (mDatabaseListener != null) {
                        mDatabaseListener.onCreate(db);
                    }
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    Log.i(TAG, "onUpgrade database '%s'(0x%x -> 0x%x)", name, oldVersion, newVersion);
                    if (mDatabaseListener != null) {
                        mDatabaseListener.onUpgrade(db, oldVersion, newVersion);
                    }
                }
            };
            mDatabasePath = ApplicationContext.getContext().getDatabasePath(name).getPath();
        }

        Log.i(TAG, "openOrCreateDatabase(%s).", name);
        return mSQLiteOpenHelper.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String path, String name, int version) {
        // TODO: 16/4/1 albieliang
        final String databasePath = path + "/" + name;
        SQLiteDBHelper helper = new SQLiteDBHelper(ApplicationContext.getContext(), databasePath, version) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                Log.i(TAG, "onCreate database '%s'(0x%x)", databasePath, db.getVersion());
                if (mDatabaseListener != null) {
                    mDatabaseListener.onCreate(db);
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.i(TAG, "onUpgrade database '%s'(0x%x -> 0x%x)", databasePath, oldVersion, newVersion);
                if (mDatabaseListener != null) {
                    mDatabaseListener.onUpgrade(db, oldVersion, newVersion);
                }
            }
        };
        mDatabasePath = databasePath;
        return helper.getDatabase();
    }

    @Override
    public String getDatabasePath() {
        return mDatabasePath;
    }

    public void setDatabaseListener(ISQLiteDatabaseListener l) {
        this.mDatabaseListener = l;
    }

    /**
     *
     * @author AlbieLiang
     *
     */
    public interface ISQLiteDatabaseListener {

        void onCreate(SQLiteDatabase db);

        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }
}
