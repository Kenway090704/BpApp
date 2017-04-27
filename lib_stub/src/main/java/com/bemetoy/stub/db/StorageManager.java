package com.bemetoy.stub.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bemetoy.bp.autogen.table.DaoMaster;
import com.bemetoy.bp.autogen.table.DaoSession;
import com.bemetoy.bp.persistence.db.DatabaseConstants;
import com.bemetoy.stub.account.AccountManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albieliang on 16/3/14.
 */
public class StorageManager {

    private static final String TAG = "Stg.StorageManager";

    private static final String BP_APP_MAIN_DB_NAME = DatabaseConstants.APP_MAIN_DB_NAME;

    private static StorageManager sStorageManager;

    private DaoMaster mDaoMaster;
    private SQLiteDatabase mDatabase;
    private DaoSession mDaoSession;

    private DBInit mDatabaseInit;
    private volatile boolean mInited;
    // Storage
    private DownloadInfoStorage mDownloadInfoStorage;
    private SysMessageStorage mSysMessageStorage;
    private MyMessageStorage myMessageStorage;

    private List<OnStorageManagerStateChangeListener> listenerList = new ArrayList();

    private StorageManager() {

    }

    public static StorageManager getMgr() {
        if (sStorageManager == null) {
            synchronized (StorageManager.class) {
                if (sStorageManager == null) {
                    sStorageManager = new StorageManager();
                }
            }
        }
        return sStorageManager;
    }

    /**
     *
     * @param accMgr
     */
    public synchronized void initialize(AccountManager accMgr) {
        if (mInited) {
            Log.i(TAG, "initialize failed, it has been initialized.");
            return;
        }
        if (accMgr == null) {
            Log.i(TAG, "initialize failed, acc mgr is null.");
            return;
        }
        if (!accMgr.isReady()) {
            Log.w(TAG, "initialize failed, acc mgr has not ready.");
            return;
        }
        mInited = true;

        Log.i(TAG, "initialize storage manager.");
//        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(ApplicationContext.getApplication(), BP_APP_MAIN_DB_NAME, null);
        mDatabaseInit = new DBInit();
        mDatabaseInit.setDatabaseListener(new DBInit.ISQLiteDatabaseListener() {
            @Override
            public void onCreate(SQLiteDatabase db) {
                DaoMaster.createAllTables(db, true);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // TODO: 16/4/1 albieliang, deal upgrade
                Log.i(TAG, "db update oldVersion is %d " + oldVersion + " and newVersion is %d"+ newVersion);
            }
        });

        String path = DatabaseConstants.getDbRootPath(accMgr.getSessionKey());
        String databaseName = DatabaseConstants.APP_MAIN_DB_NAME;
        int version = DaoMaster.SCHEMA_VERSION;

        mDatabase = mDatabaseInit.openOrCreateDatabase(path, databaseName, version);

        mDaoMaster = new DaoMaster(mDatabase);
        mDaoSession = mDaoMaster.newSession();

        mDownloadInfoStorage = new DownloadInfoStorage(mDaoSession.getDownloadTaskInfoDao());
        mSysMessageStorage = new SysMessageStorage(mDaoSession.getSysMessageDao());
        myMessageStorage = new MyMessageStorage(mDaoSession.getMyMessageDao());

        for(OnStorageManagerStateChangeListener listener : listenerList) {
            listener.onStateChange(true);
        }
    }

    public synchronized boolean hasInit(){
        return mInited;
    }


    /**
     * Reset all of the storage and database.
     *
     * @param accMgr
     */
    public synchronized void reset(AccountManager accMgr) {
        if (!mInited) {
            Log.w(TAG, "reset storage manager failed, it has not initialized before.");
            return;
        }
        if (accMgr == null) {
            Log.i(TAG, "reset storage mananger failed, acc mgr is null.");
            return;
        }
        Log.i(TAG, "reset storage manager.");
        mInited = false;
        // Reset storage
        mDownloadInfoStorage = null;
        // Reset dao session
        mDaoSession.clear();
        mDaoSession = null;
        mDaoMaster = null;
        // Close database
        mDatabase.close();
        mDatabase = null;
        for(OnStorageManagerStateChangeListener listener : listenerList) {
            listener.onStateChange(false);
        }
    }

    public boolean addOnStorageManagerStateChangeListener(OnStorageManagerStateChangeListener l) {
        return listenerList.add(l);
    }

    public boolean removeStorageManagerStateChangeListener(OnStorageManagerStateChangeListener l) {
        return listenerList.remove(l);
    }


    /**
     *
     * @return
     */
    protected DaoSession getSession() {
        return mDaoSession;
    }

    /**
     * Sample.
     *
     * @return
     */
    public DownloadInfoStorage getDownloadInfoStg() {
        return mDownloadInfoStorage;
    }

    public MyMessageStorage getMyMessageStorage() {
        return myMessageStorage;
    }

    public SysMessageStorage getSysMessageStorage(){
        return mSysMessageStorage;
    }

    public interface OnStorageManagerStateChangeListener {
        void onStateChange(boolean isInit);
    }

}
