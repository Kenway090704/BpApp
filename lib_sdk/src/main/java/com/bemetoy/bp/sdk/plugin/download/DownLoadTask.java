package com.bemetoy.bp.sdk.plugin.download;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tom on 2016/7/25.
 */

public class DownLoadTask implements Runnable {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private static final String TAG = "sdk.DownLoadTask";

    private String mFilePth;
    private String mTargetUrl;
    private long startPoint;
    private long dataLength;
    private MultiThreadDownLoad.DownloadListener listener;

    public DownLoadTask(String targetUrl, String filePath, long startPoint, long dataLength, MultiThreadDownLoad.DownloadListener listener) {
        this.mFilePth = filePath;
        mTargetUrl = targetUrl;
        this.startPoint = startPoint;
        this.dataLength = dataLength;
        this.listener = listener;
    }

    @Override
    public void run() {
        RandomAccessFile accessFile = null;
        InputStream ins = null;
        HttpURLConnection connection = null;
        try {
            if (Util.isNullOrBlank(mFilePth) || Util.isNullOrBlank(mTargetUrl)) {
                throw new NullPointerException("target file or inputStream is null.");
            }

            URL url = new URL(mTargetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30 * 1000);
            connection.setRequestProperty("User-Agent", "Bemetoy-bp");
            connection.setRequestProperty("Range", "bytes=" + startPoint + "-" + (startPoint + dataLength - 1));
            Log.d(TAG, "Current thread name is [%s], startPosition is [%d], endPoint is [%d], dataLength is [%s]",
                    Thread.currentThread().getName(), startPoint, startPoint + dataLength - 1, dataLength);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL || connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "content length from the server %d", connection.getContentLength());
                ins = connection.getInputStream();
                accessFile = new RandomAccessFile(new File(mFilePth), "rwd");
                accessFile.seek(startPoint);
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int len = 0;
                int totalLength = 0;
                while ((len = ins.read(buffer)) != -1) {
                    accessFile.write(buffer, 0, len);
                    totalLength += len;
                }
                if(listener != null) {
                    listener.onTaskFinish();
                }
                Log.d(TAG, "current download length is %d", totalLength);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
