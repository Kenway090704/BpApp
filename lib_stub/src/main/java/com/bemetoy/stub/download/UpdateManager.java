package com.bemetoy.stub.download;

/**
 * Created by Tom on 2016/7/14.
 */
public class UpdateManager {

    private static UpdateManager manager;
    private long appDownloadId = -1;
    private String updateLog;
    private String filePath;
    private String fileMd5;

    public static UpdateManager getManager(){
        synchronized (UpdateManager.class) {
            if(manager == null) {
                manager = new UpdateManager();
            }
            return manager;
        }
    }

    public long getAppDownloadId() {
        return appDownloadId;
    }

    public void setAppDownloadId(long appDownloadId) {
        this.appDownloadId = appDownloadId;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

}
