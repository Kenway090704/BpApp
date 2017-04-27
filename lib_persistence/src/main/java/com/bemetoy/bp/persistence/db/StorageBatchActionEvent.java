package com.bemetoy.bp.persistence.db;

import com.bemetoy.bp.persistence.db.base.DatabaseItem;
import com.bemetoy.bp.persistence.db.base.StorageEvent;

import java.util.List;

/**
 * Created by albieliang on 16/4/2.
 */
public abstract class StorageBatchActionEvent<DBItem extends DatabaseItem> extends StorageEvent {

    private List<DBItem> mItems;

    public StorageBatchActionEvent(String table, List<DBItem> items, int action) {
        super(table);
        // Invalid action code
        mAction = action;
        mItems = items;
    }

    public List<DBItem> getItems() {
        return mItems;
    }
}
