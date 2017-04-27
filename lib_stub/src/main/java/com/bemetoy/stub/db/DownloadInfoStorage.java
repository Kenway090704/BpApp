package com.bemetoy.stub.db;

import com.bemetoy.bp.autogen.table.DownloadTaskInfo;
import com.bemetoy.bp.autogen.table.DownloadTaskInfoDao;
import com.bemetoy.bp.persistence.db.BaseDbStorage;

/**
 * Created by albieliang on 16/3/14.
 */
public class DownloadInfoStorage extends BaseDbStorage<DownloadTaskInfo, Long> {

    public DownloadInfoStorage(DownloadTaskInfoDao dao) {
        super(dao);
    }
}
