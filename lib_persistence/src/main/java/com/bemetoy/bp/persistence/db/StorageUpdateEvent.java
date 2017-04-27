package com.bemetoy.bp.persistence.db;

import com.bemetoy.bp.persistence.db.base.DatabaseItem;
import com.bemetoy.bp.persistence.db.base.StorageEvent;

/**
 * Created by albieliang on 16/3/13.
 */
public class StorageUpdateEvent<DBItem extends DatabaseItem> extends StorageEvent {

    public StorageUpdateEvent(String table) {
        super(table);
        mAction = ACTION_UPDATE;
    }

    public StorageUpdateEvent(String table, DBItem item) {
        super(table);
        mAction = ACTION_UPDATE;
        mItem = item;
    }

    public StorageUpdateEvent(String table, long rowId, DBItem item) {
        super(table);
        mAction = ACTION_UPDATE;
        mRowId = rowId;
        mItem = item;
    }
}
