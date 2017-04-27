package com.bemetoy.bp.persistence.db;

import com.bemetoy.bp.persistence.db.base.DatabaseItem;
import com.bemetoy.bp.persistence.db.base.StorageEvent;

/**
 * Created by albieliang on 16/2/26.
 */
public class StorageDeleteEvent<DBItem extends DatabaseItem> extends StorageEvent {

    public StorageDeleteEvent(String table) {
        super(table);
        mAction = ACTION_DELETE;
    }

    public StorageDeleteEvent(String table, long rowId) {
        super(table);
        mAction = ACTION_DELETE;
        mRowId = rowId;
    }

    public StorageDeleteEvent(String table, DBItem item) {
        super(table);
        mAction = ACTION_DELETE;
        mItem = item;
    }
}
