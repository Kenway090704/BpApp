
package com.bemetoy.bp.sdk.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

/**
 * 
 * @author AlbieLiang
 *
 */
public class ThreadPool {

	private static Handler sUiThreadHandler;
	private static Handler sWorkerThreadHandler;

	static {
		sUiThreadHandler = new Handler(Looper.getMainLooper());
		HandlerThread thread = newHandlerThread("WorkerThread-Core");
		thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		sWorkerThreadHandler = new Handler(thread.getLooper());
	}

	ThreadPool() {
	}

	public static HandlerThread newHandlerThread(String name) {
		// TODO
		return new HandlerThread(name);
	}

	public static HandlerThread newHandlerThread(String name, int priority) {
		// TODO
		return new HandlerThread(name, priority);
	}

	public static <T> WorkerThread<T> newWorkerThread(Class<T> clazz) {
		// TODO
		return new WorkerThread<T>();
	}
	
	public static <T> WorkerThread<T> newWorkerThread(Class<T> clazz, Looper looper) {
		// TODO
		return new WorkerThread<T>(looper);
	}

	public static Thread newThread(Runnable run) {
		// TODO
		return new Thread(run);
	}
	
	public static Thread newThread(Runnable run, String threadName) {
		// TODO
		return new Thread(run, threadName);
	}

	/**
	 * Post task into UI thread tasks list.
	 *
	 * @param run
	 *
	 * @see #post(Runnable)
     */
	public static void postToMainThread(Runnable run) {
		if (run == null) {
			return;
		}
		sUiThreadHandler.post(run);
	}

	/**
	 * Post task into UI thread tasks list.
	 *
	 * @param run
	 *
	 * @see #post(Runnable)
     */
	public static void postToMainThread(Runnable run, long delay) {
		if (run == null) {
			return;
		}
		sUiThreadHandler.postDelayed(run, delay);
	}

	/**
	 * Post task into Worker thread tasks list.
	 *
	 * @param run
	 *
	 * @see #postToMainThread(Runnable)
     */
	public static void post(Runnable run) {
		if (run == null) {
			return;
		}
		sWorkerThreadHandler.post(run);
	}

	/**
	 * Post task into Worker thread tasks list.
	 *
	 * @param run
	 * @param delay
     */
	public static void postDelay(Runnable run, long delay) {
		if (run == null) {
			return;
		}
		sWorkerThreadHandler.postDelayed(run, delay);
	}

    /**
     * Remove the callback;
     * @param runnable
     */
    public static void removeOnMainThread(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        sUiThreadHandler.removeCallbacks(runnable);
    }

}
