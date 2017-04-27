package com.bemetoy.bp.persistence.db;

import com.bemetoy.bp.persistence.db.base.DatabaseItem;
import com.bemetoy.bp.persistence.db.base.StorageEvent;

/**
 * Created by albieliang on 16/3/13.
 */
public class StorageInsertEvent<DBItem extends DatabaseItem> extends StorageEvent {

    public StorageInsertEvent(String table, long rowId, DBItem item) {
        super(table);
        mAction = ACTION_INSERT;
        mRowId = rowId;
        mItem = item;
    }
}
