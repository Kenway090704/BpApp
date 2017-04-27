package com.bemetoy.stub.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bemetoy.bp.autogen.events.DownLoadFinishEvent;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.plugin.download.MultiThreadDownLoad;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by Tom on 2016/7/14.
 */
public class DownloadService extends Service {

    private boolean isFirstStart = true;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if(isFirstStart) {
            isFirstStart = false;
            ThreadPool.post(new Runnable() {
                @Override
                public void run() {
                    if(intent == null) {
                        return;
                    }

                    final String targetFile = intent.getStringExtra(ProtocolConstants.IntentParams.DOWNLOAD_PATH);
                    final String sourceUrl =  intent.getStringExtra(ProtocolConstants.IntentParams.URL_INFO);

                    FileUtils.deleteAll(new File(targetFile));
                    MultiThreadDownLoad downLoad = new MultiThreadDownLoad(sourceUrl, targetFile);
                    downLoad.setDownLoadTaskListener(new MultiThreadDownLoad.DownLoadTaskListener (){
                        @Override
                        public void onDownloadFinished() {
                            Log.e("DownloadService", "down load task finished");
                            DownLoadFinishEvent downLoadFinishEvent = new DownLoadFinishEvent();
                            downLoadFinishEvent.localPath = targetFile;
                            downLoadFinishEvent.targetUrl = sourceUrl;
                            RxEventBus.getBus().publish(downLoadFinishEvent);
                            stopSelf();
                        }
                    });
                    downLoad.start();
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("DownloadService", "DownloadService done");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
