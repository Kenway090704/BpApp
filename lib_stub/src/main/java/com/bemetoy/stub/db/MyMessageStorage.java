package com.bemetoy.stub.db;

import android.database.Cursor;

import com.bemetoy.bp.autogen.table.MyMessage;
import com.bemetoy.bp.persistence.db.BaseDbStorage;
import com.bemetoy.bp.sdk.tools.Log;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by Tom on 2016/6/30.
 */
public class MyMessageStorage extends BaseDbStorage<MyMessage,Long> {

    private static final String TAG = "Stub.MyMessageStorage";

    public static final String COL_ID = "ID";
    public static final String COL_METHOD = "METHOD";
    public static final String COL_FROM_USERNAME = "FROM_USERNAME";
    public static final String COL_CONTENT = "CONTENT";
    public static final String COL_RESULT = "RESULT";
    public static final String COL_HAS_READ = "HAS_READ";
    public static final String COL_FROM_USER_ID = "FROM_USER_ID";
    public static final String COL_TIME_STAMP = "TIMESTAMP";


    public MyMessageStorage(AbstractDao<MyMessage, Long> dao) {
        super(dao);
    }

    public List<MyMessage> loadMessageWithoutDuplicate() {
        Cursor cursor = this.getAll(null, null, null, COL_FROM_USER_ID, null, COL_TIME_STAMP + " DESC", null);
        List<MyMessage> messageList = new ArrayList();
        try {
            while(cursor.moveToNext()) {
                MyMessage myMessage = new MyMessage();
                myMessage.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
                myMessage.setMethod(cursor.getString(cursor.getColumnIndex(COL_METHOD)));
                myMessage.setFromUsername(cursor.getString(cursor.getColumnIndex(COL_FROM_USERNAME)));
                myMessage.setContent(cursor.getString(cursor.getColumnIndex(COL_CONTENT)));
                myMessage.setResult(cursor.getInt(cursor.getColumnIndex(COL_RESULT)));
                myMessage.setHasRead(cursor.getInt(cursor.getColumnIndex(COL_HAS_READ)) == 1);
                myMessage.setFromUserId(cursor.getLong(cursor.getColumnIndex(COL_FROM_USER_ID)));
                myMessage.setTimestamp(cursor.getLong(cursor.getColumnIndex(COL_TIME_STAMP)));
                messageList.add(myMessage);
            }
        } catch (Exception e) {
            Log.e(TAG, "loadMessageWithoutDuplicate exception:%s", e.getMessage());
        }finally {
            cursor.close();
        }
        return messageList;
    }

    public boolean hasUnReadMessage() {
        if(isDataBaseClosed()) {
            return false;
        }
        return get(new String[]{COL_HAS_READ}, new String [] {"0"}).size() > 0;
    }

    public boolean markAllHasRead(){
        boolean result =  execSQL("UPDATE " + getTableName() + " SET HAS_READ = 1");
        Log.d(TAG, "mark all message as read %b", result);
        return result;
    }
}
