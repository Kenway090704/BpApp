package com.bemetoy.bp.sdk.plugin.download;

import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.bp.sdk.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tom on 2016/7/15.
 */
public class MultiThreadDownLoad {

    private static final String TAG = "sdk.MultiThreadDownLoad";

    private String mFilePath;
    private String mTargetUrl;
    private boolean mBreakPointContinue;
    private int mThreadCount;
    private int currentProcess = 0;
    private DownLoadTaskListener loadTaskListener = null;

    public MultiThreadDownLoad(String targetUrl, String filePath) {
        this(targetUrl, filePath, false, 2);
    }

    public MultiThreadDownLoad(String targetUrl, String filePath, boolean breakPointContinue, int threadCount) {
        this.mTargetUrl = targetUrl;
        this.mBreakPointContinue = breakPointContinue;
        this.mThreadCount = threadCount;
        this.mFilePath = filePath;
    }

    public void setDownLoadTaskListener(DownLoadTaskListener loadTaskListener) {
        this.loadTaskListener = loadTaskListener;
    }

    public boolean start() {
        if (Util.isNullOrBlank(mTargetUrl) || Util.isNullOrBlank(mFilePath)) {
            Log.w(TAG, "target url or mFilePath is null or blank");
            return false;
        }

        File file = new File(mFilePath);
        FileUtils.createFileIfNeed(file);
        HttpURLConnection connection = null;
        RandomAccessFile accessFile = null;
        MultiThreadDownLoad.DownloadListener listener = new  MultiThreadDownLoad.DownloadListener() {
            @Override
            public void onTaskFinish() {
                synchronized (MultiThreadDownLoad.this) {
                    currentProcess++;
                    if(currentProcess == mThreadCount) {
                        if(loadTaskListener != null) {
                            loadTaskListener.onDownloadFinished();
                        }
                    }
                }
            }
        };

        try {
            URL url = new URL(mTargetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent","Bemetoy-bp");
            connection.setConnectTimeout(30 * 1000);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                long contentLength = connection.getContentLength();
                Log.d(TAG, "target url content-length is %s", contentLength);
                accessFile = new RandomAccessFile(file, "rwd");
                accessFile.setLength(contentLength);
                long taskSize = (contentLength % mThreadCount == 0 ? contentLength / mThreadCount : contentLength / mThreadCount + 1);
                for (int i = 0; i < mThreadCount; i++) {
                    if(i == mThreadCount - 1) {
                        long startPoint = i * taskSize;
                        taskSize = contentLength - (mThreadCount - 1) * taskSize;
                        Thread thread = ThreadPool.newThread(new DownLoadTask(mTargetUrl, mFilePath, startPoint, taskSize, listener));
                        thread.start();
                    } else {
                        Thread thread = ThreadPool.newThread(new DownLoadTask(mTargetUrl, mFilePath, i * taskSize, taskSize, listener));
                        thread.start();
                    }
                }
            } else {
                Log.d(TAG, "response code is %d", connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(accessFile != null) {
                    accessFile.close();
                }

                if(connection != null) {
                    connection.disconnect();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return true;
    }


    public interface DownLoadTaskListener {
        void onDownloadFinished();
    }


    protected interface DownloadListener {
        void onTaskFinish();
    }
}
