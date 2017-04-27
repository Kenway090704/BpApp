package com.bemetoy.bp.sdk.tools.log;

import com.bemetoy.bp.protocols.LocalDataConstants;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.WorkerThread;
import com.bemetoy.bp.sdk.core.WorkerThread.DoTaskProxy;
import com.bemetoy.bp.sdk.utils.DeviceUtils;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.sdk.utils.ValueOptUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 
 * @author AlbieLiang
 *
 */
public class DefaultLogWriter implements ILogWriter {
	
	private static final String TAG = "Log.DefaultLogWriter";
	
	private volatile boolean mHasStarted;
	private File mFile;
	private FileOutputStream mFileOutputStream;
	private WorkerThread<StringBuffer> mWorkerThread;
	private int mPid;
	
	public DefaultLogWriter() {
		mPid = android.os.Process.myPid();
	}
	
	@Override
	public synchronized void writeLog(long tid, long msec, int priority, String tag, String log) {
		if (!mHasStarted) {
			android.util.Log.w(TAG, "File output stream has been closed or the writer has not started at all.");
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("[priority:").append(priority).append("]");
		sb.append("[pid:").append(mPid).append(",tid:").append(tid).append("]");
		sb.append("[msec:").append(msec).append("]");
		sb.append("[time:").append(Util.getDateFormat("Z yyyy-MM-dd HH:mm:ss.SSS", msec)).append("]");
		sb.append("[tag:").append(tag).append("]");
		sb.append(log);
		sb.append("\n");
		if(mWorkerThread != null) {
			mWorkerThread.addTask(new DoTaskProxyImpl(sb, mFileOutputStream));
		}
	}

    @Override
	public synchronized boolean start(String dir, String fileName, int pid, long mainTid, long msec) {
		if (mHasStarted) {
			android.util.Log.w(TAG, "The writer has already been started.");
			return false;
		}
		String path = dir + File.separator + fileName;
		if (!DeviceUtils.hasSDCard()) {
			android.util.Log.w(TAG, ValueOptUtils.format("try to create file failed, sdcard do not exist.(%s)", path));
			return false;
		}
		mHasStarted = true;
		mFile = new File(path);
		FileUtils.createFileIfNeed(mFile);
		/**
		 * When new FileOutputStream(mFile, true) throw exception
		 * mWorkerThread will be null.
		 */
		try {
            mFileOutputStream = new FileOutputStream(mFile, true);
			mWorkerThread = ThreadPool.newWorkerThread(StringBuffer.class);
			mWorkerThread.setName("Thread-LogWriter");
			mWorkerThread.start();
			android.util.Log.i(TAG, "Start Log writer successfully.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public synchronized void shutdown() {
		if (!mHasStarted) {
			return;
		}
		mHasStarted = false;
		if (mFileOutputStream != null) {
			try {
				mFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				mWorkerThread.shutdown();
				mWorkerThread = null;
				mFileOutputStream = null;
				android.util.Log.i(TAG, "Shutdown Log writer successfully.");
			}
		}
	}

	/**
	 * 
	 * @author AlbieLiang
	 *
	 */
	private static final class DoTaskProxyImpl extends DoTaskProxy<StringBuffer> {

		private FileOutputStream fos;
		
		public DoTaskProxyImpl(StringBuffer task, FileOutputStream fos) {
			super(task, task);
			this.fos = fos;
		}

		@Override
		public void doTask(StringBuffer sBuffer) {
			if (fos == null) {
				android.util.Log.w(TAG, "fos is null.");
				return;
			}
			byte[] buffer = null;
			try {
				buffer = sBuffer.toString().getBytes(LocalDataConstants.Charset.LOG_CHARSET);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			sBuffer.setLength(0);
			if (buffer == null) {
				return;
			}
			try {
				fos.write(buffer);
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
