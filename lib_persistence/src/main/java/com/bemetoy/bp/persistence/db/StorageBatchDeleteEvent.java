package com.bemetoy.bp.persistence.db;

import com.bemetoy.bp.persistence.db.base.DatabaseItem;
import com.bemetoy.bp.persistence.db.base.StorageEvent;

import java.util.List;

/**
 * Created by albieliang on 16/4/2.
 */
public class StorageBatchDeleteEvent<DBItem extends DatabaseItem> extends StorageBatchActionEvent {

    public StorageBatchDeleteEvent(String table, List<DBItem> items) {
        super(table, items, StorageEvent.ACTION_BATCH_DELETE);
    }
}
