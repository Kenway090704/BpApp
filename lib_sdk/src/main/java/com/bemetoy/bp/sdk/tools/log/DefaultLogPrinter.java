package com.bemetoy.bp.sdk.tools.log;

import android.os.Looper;

import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.BuildConfig;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.sdk.utils.ValueOptUtils;

/**
 * A default LogPrinter. Invoked {@link Log#setLogPrinter(ILogPrinter)} to set a
 * {@link ILogPrinter} for the Log tools.
 * 
 * @author AlbieLiang
 * @see Log#setLogPrinter(ILogPrinter)
 */
public class DefaultLogPrinter implements ILogPrinter {

    private static final String LOG_BASE_DIR = StorageConstants.SDCard.LOG_STORAGE_DIR;
    private static final String LOG_FILE_NAME = "";

    private static final String LOG_FILE_SUFFIX = "log";
    private ILogWriter mLogWriter;
    
    public DefaultLogPrinter() {
    	mLogWriter = new DefaultLogWriter();
		long msec = System.currentTimeMillis();
		String fileName = ValueOptUtils.chain("", LOG_FILE_NAME,
                ApplicationContext.getPidKey(ApplicationContext.getApplication(),
                        android.os.Process.myPid()), "_", Util.getDateFormat("yyyy-MM-dd", msec), ".", LOG_FILE_SUFFIX);

        mLogWriter.start(LOG_BASE_DIR, fileName,
                android.os.Process.myPid(), Looper.getMainLooper().getThread().getId(), msec);
    }
    
	@Override
	public void printLog(int priority, String tag, String format, Object... args) {
		if (priority < getPriority()) {
			return;
		}
		String msg = ValueOptUtils.format(format, args);
		if(Util.isNull(msg)) {
			return;
		}
		android.util.Log.println(priority, tag, msg);
		mLogWriter.writeLog(Thread.currentThread().getId(), System.currentTimeMillis(), priority, tag, msg);
	}

    @Override
	public boolean isLoggable(String tag, int priority) {
		return android.util.Log.isLoggable(tag, priority);
	}

	@Override
	public int getPriority() {
		int priority = Log.VERBOSE;
		if (!BuildConfig.DEBUG) {
			priority = Log.INFO;
		}
		return priority;
	}

	@Override
	public ILogWriter getLogWriter() {
		return mLogWriter;
	}
}