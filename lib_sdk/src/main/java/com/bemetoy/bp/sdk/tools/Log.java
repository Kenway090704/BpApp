package com.bemetoy.bp.sdk.tools;

import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.tools.log.DefaultLogPrinter;
import com.bemetoy.bp.sdk.tools.log.ILogPrinter;
import com.bemetoy.bp.sdk.utils.DeviceUtils;

/**
 * A Log tools. Invoked {@link #setLogPrinter(ILogPrinter)} to set a
 * {@link ILogPrinter} to do a real Log action.
 * 
 * @author AlbieLiang
 * 
 */
public class Log {

    public static final int VERBOSE = android.util.Log.VERBOSE;
    public static final int DEBUG = android.util.Log.DEBUG;
    public static final int INFO = android.util.Log.INFO;
    public static final int WARN = android.util.Log.WARN;
    public static final int ERROR = android.util.Log.ERROR;
    public static final int ASSERT = android.util.Log.ASSERT;
	
	private static ILogPrinter sLogPrinter = new DefaultLogPrinter();

	public static boolean setLogPrinter(ILogPrinter printer) {
		if (printer == null) {
			return false;
		}
		sLogPrinter = printer;
		return true;
	}
	
	/**
	 * Info log
	 * 
	 * @param clazz
	 * @param format
	 * @param args
	 */
//	public static void i(Class<?> clazz, String format, Object... args) {
//		Log.i(formatTag(clazz), format, args);
//	}
//
//	public static void i(Object o, String format, Object... args) {
//		Log.i(formatTag(o), format, args);
//	}

	public static void i(String tag, String format, Object... args) {
		sLogPrinter.printLog(INFO, tag, format, args);
	}

	/**
	 * error log
	 * 
	 * @param clazz
	 * @param format
	 * @param args
	 */
//	public static void e(Class<?> clazz, String format, Object... args) {
//		Log.e(formatTag(clazz), format, args);
//	}
//
//	public static void e(Object o, String format, Object... args) {
//		Log.e(formatTag(o), format, args);
//	}

	public static void e(String tag, String format, Object... args) {
		sLogPrinter.printLog(ERROR, tag, format, args);
	}

	/**
	 * Warming
	 * 
	 * @param clazz
	 * @param format
	 * @param args
	 */
//	public static void w(Class<?> clazz, String format, Object... args) {
//		Log.w(formatTag(clazz), format, args);
//	}
//
//	public static void w(Object o, String format, Object... args) {
//		Log.w(formatTag(o), format, args);
//	}

	public static void w(String tag, String format, Object... args) {
		sLogPrinter.printLog(WARN, tag, format, args);
	}

	/**
	 * Description
	 * 
	 * @param clazz
	 * @param format
	 * @param args
	 */
//	public static void d(Class<?> clazz, String format, Object... args) {
//		Log.d(formatTag(clazz), format, args);
//	}
//
//	public static void d(Object o, String format, Object... args) {
//		Log.d(formatTag(o), format, args);
//	}

	public static void d(String tag, String format, Object... args) {
		sLogPrinter.printLog(DEBUG, tag, format, args);
	}

	/**
	 * 
	 * @param clazz
	 * @param format
	 * @param args
	 */
//	public static void v(Class<?> clazz, String format, Object... args) {
//		Log.v(formatTag(clazz), format, args);
//	}
//
//	public static void v(Object o, String format, Object... args) {
//		Log.v(formatTag(o), format, args);
//	}

	public static void v(String tag, String format, Object... args) {
		sLogPrinter.printLog(VERBOSE, tag, format, args);
	}

	/**
	 * 
	 * @param priority
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static void printLog(int priority, String tag, String format, Object... args) {
		sLogPrinter.printLog(priority, tag, format, args);
	}


	public static String formatTag(Object o) {
		if (o == null) {
			return null;
		}
		return formatTag(o.getClass());
	}

	public static String formatTag(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		return clazz.getSimpleName();
	}
}
