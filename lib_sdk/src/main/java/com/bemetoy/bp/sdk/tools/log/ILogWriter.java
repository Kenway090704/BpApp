package com.bemetoy.bp.sdk.tools.log;

/**
 * 
 * @author AlbieLiang
 *
 */
public interface ILogWriter {
	
	boolean start(String dir, String fileName, int pid, long mainTid, long msec);

	void shutdown();
	
	void writeLog(long tid, long msec, int priority, String tag, String log);
}