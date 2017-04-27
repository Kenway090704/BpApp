package com.bemetoy.bp.sdk.plugin.crashhandler;

import android.os.Build;

import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.sync.LockEntity;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.tools.log.DefaultCrashLogWriter;
import com.bemetoy.bp.sdk.tools.log.ILogWriter;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.sdk.utils.ValueOptUtils;

/**
 * Created by Tom on 2016/3/23.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "skd.crashHandler";
    private static final CrashHandler crashHandler= new CrashHandler();
    private static final String CRASH_LOG_DIR = StorageConstants.SDCard.CRASH_STORAGE_DIR;
    private static final String CRASH_LOG_FILE_NAME = "Crash";
    private static final String LOG_FILE_SUFFIX = "txt";

    private ILogWriter mILogWriter;

    private CrashHandler(){
        mILogWriter = new DefaultCrashLogWriter(new LockEntity());
    }

    public static CrashHandler getInstance() {
        return crashHandler;
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        long msec = System.currentTimeMillis();
        android.util.Log.e(TAG, android.util.Log.getStackTraceString(ex));
        String fileName = ValueOptUtils.chain("", CRASH_LOG_FILE_NAME,
                ApplicationContext.getPidKey(ApplicationContext.getApplication(),
                        android.os.Process.myPid()), "_", Util.getDateFormat("yyyy-MM-dd", msec), ".",LOG_FILE_SUFFIX );
        mILogWriter.start(CRASH_LOG_DIR, fileName, android.os.Process.myPid(), thread.getId(), msec);
        mILogWriter.writeLog(thread.getId(), msec, Log.ASSERT, TAG, getCrashInfoFormat(thread, ex));
        mILogWriter.shutdown();
        uploadCrashLog();

        //TODO need refine the process will restart!
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private String getCrashInfoFormat(Thread thread,Throwable throwable) {
        StringBuffer sb = new StringBuffer();
        sb.append("[Time]=").append(Util.getDateFormat("yyyy-MM-dd hh:mm:ss", System.currentTimeMillis())).append("\r\n");
        sb.append("[Device]=").append(Build.MANUFACTURER + " ").append(Build.MODEL + " ").append(Build.VERSION.RELEASE).append("\r\n");
        sb.append(throwable.getClass().getName());
        if(throwable.getMessage() != null) {
            sb.append(" : ").append(throwable.getMessage());
        }
        sb.append("\r\n");
        StackTraceElement [] stackTraceElement =  throwable.getStackTrace();
        for(int i = 0 ; i < stackTraceElement.length ; i++) {
            sb.append("    ");
            sb.append("at ");
            sb.append(stackTraceElement[i].getClassName()).append(".");
            sb.append(stackTraceElement[i].getMethodName());
            sb.append("(").append(stackTraceElement[i].getClassName()).append(":")
                    .append(stackTraceElement[i].getLineNumber()).append(")");
            sb.append("\r\n");
        }
        sb.append("\r\n");
        return sb.toString();
    }

    //TODO upload the log to the server
    private void uploadCrashLog() {

    }

}
