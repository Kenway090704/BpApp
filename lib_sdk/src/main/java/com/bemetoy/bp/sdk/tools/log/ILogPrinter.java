package com.bemetoy.bp.sdk.tools.log;

/**
 * 
 * @author AlbieLiang
 *
 */
public interface ILogPrinter {
	
	void printLog(int priority, String tag, String format, Object... args);

	boolean isLoggable(String tag, int priority);
	
	int getPriority();
	
	ILogWriter getLogWriter();
}
