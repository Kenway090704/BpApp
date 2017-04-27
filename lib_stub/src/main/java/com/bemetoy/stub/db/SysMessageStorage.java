package com.bemetoy.stub.db;

import android.database.Cursor;

import com.bemetoy.bp.autogen.table.SysMessage;
import com.bemetoy.bp.persistence.db.BaseDbStorage;
import com.bemetoy.bp.sdk.tools.Log;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by Tom on 2016/6/3.
 */
public class SysMessageStorage  extends BaseDbStorage<SysMessage, Long> {

    private static final String TAG = "Stub.SysMessageStorage";

    public static final String COL_HAS_READ = "HAS_READ";
    public static final String COL_METHOD = "METHOD";
    public static final String COL_CONTENT = "CONTENT";
    public static final String COL_RESULT = "RESULT";
    public static final String COL_ID = "ID";

    public SysMessageStorage(AbstractDao<SysMessage, Long> dao) {
        super(dao);
    }

    public boolean markAllHasRead(){
        boolean result =  execSQL("UPDATE " + getTableName() + " SET HAS_READ = 1");
        Log.d(TAG, "mark all message as read %b", result);
        return result;
    }

    public boolean hasUnReadMessage() {
        if(isDataBaseClosed()) {
            return false;
        }
        return get(new String[]{COL_HAS_READ}, new String [] {"0"}).size() > 0;
    }


    public Cursor get(String[] keys, Object[] values, String order, String limit) {
        return super.get(keys, values, order, limit);
    }

    public List<SysMessage> load(String[] cols, String[] keys, Object[] values, String order, String limit) {
        final Cursor cursor = get(cols, keys, values, order, limit);
        final List<SysMessage> list = new ArrayList();
        int count = cursor.getCount();
        if(count == 0) {
            Log.v(TAG, "data size is zero");
            return list;
        }
        try {
            while(cursor.moveToNext()) {
                SysMessage sysMessage = new SysMessage();
                sysMessage.setHasRead(cursor.getInt(cursor.getColumnIndex(COL_HAS_READ)) == 1);
                sysMessage.setMethod(cursor.getString(cursor.getColumnIndex(COL_METHOD)));
                sysMessage.setResult(cursor.getString(cursor.getColumnIndex(COL_RESULT)));
                sysMessage.setContent(cursor.getString(cursor.getColumnIndex(COL_CONTENT)));
                list.add(sysMessage);
            }
        } catch (Exception exception) {
            Log.e(TAG, "get result exception:%s", exception.getMessage());
        }finally {
            cursor.close();
        }

        cursor.close();
        return list;
    }

}
