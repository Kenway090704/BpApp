package com.bemetoy.bp.sdk.core;

import java.util.LinkedList;
import java.util.Queue;

import android.os.Handler;
import android.os.Looper;

import com.bemetoy.bp.sdk.reflect.ReflectStaticFieldSmith;
import com.bemetoy.bp.sdk.sync.LockEntity;
import com.bemetoy.bp.sdk.tools.Log;

/**
 * The WorkerThread has high extensibility. You can invoke
 * {@link #addTask(DoTaskProxy)} to add a task into the worker queue and invoke
 * {@link #removeTask(Object)} or {@link #removeTask(DoTaskProxy)} to remove
 * task from the queue.
 * <p>
 * The {@link TaskType} can be a data or a real task, the {@link DoTaskProxy}
 * will be the proxy to help do task.
 * 
 * @author AlbieLiang
 *
 * @param <TaskType>
 * 
 * @see DoTaskProxy
 * 
 * @see #addTask(DoTaskProxy)
 * @see #removeTask(DoTaskProxy)
 * @see #removeTask(Object)
 */
public class WorkerThread<TaskType> extends Thread {

	private static final String TAG = "SDK.WorkerThread";
	
	private volatile boolean mIsStop;
	private Queue<DoTaskProxy<TaskType>> mTasks;
	private LockEntity mLock;
	private Looper mLooper;
	private Handler mHandler;
	
	public WorkerThread() {
		this(null);
	}
	
	public WorkerThread(Looper looper) {
		mLooper = looper;
		mTasks = new LinkedList<DoTaskProxy<TaskType>>();
		mLock = new LockEntity();
	}
	
	@Override
	public final void run() {
		try {
			if (mLooper != null) {
				// Check
				ThreadLocal<Looper> tl = new ReflectStaticFieldSmith<ThreadLocal<Looper>>(Looper.class, "sThreadLocal").getWithoutThrow();
				if (tl != null && tl.get() == null) {
					Log.d(TAG, "create a new Looper ThreadLocal variable.");
					tl.set(mLooper);
				} else {
					Log.d(TAG, "ThreadLocal Looper variable is null or has set.(%s)", tl);
				}
				mHandler = new Handler(mLooper);
			}
			DoTaskProxy<TaskType> task = null;
			while (!mIsStop) {
				if (mTasks.size() == 0) {
					mLock.lock();
				}
				synchronized (mTasks) {
					if (mTasks.size() == 0) {
						continue;
					}
					task = mTasks.poll();
				}
				if (task == null) {
					continue;
				}
				if (task.isDoInLooper()) {
					if (mHandler == null) {
						Log.i(TAG, "Looper is null, can not do task in a null Looper.");
						continue;
					}
					mHandler.post(new TransferArgsRunnable(task) {
						
						@Override
						public void run() {
							@SuppressWarnings("unchecked")
							DoTaskProxy<TaskType> task = (DoTaskProxy<TaskType>) getArg(0);
							task.doTask(task.getTask());
						}
					});
				} else {
					task.doTask(task.getTask());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The method will shutdown the {@link WorkerThread} after the last task
	 * finished.
	 */
	public final synchronized void shutdown() {
		mIsStop = true;
		mLock.unlock();
	}

	/**
	 * Add a {@link DoTaskProxy} into the {@link WorkerThread} task list.
	 * 
	 * @param doTaskProxy
	 * @return
	 * @see #removeTask(Object)
	 * @see #removeTask(DoTaskProxy)
	 */
	public final boolean addTask(DoTaskProxy<TaskType> doTaskProxy) {
		boolean r = false;
		if (doTaskProxy != null) {
			synchronized (mTasks) {
				r = mTasks.add(doTaskProxy);
			}
			mLock.unlock();
		}
		return r;
	}
	
	/**
	 * Remove the given task.
	 * 
	 * @param doTaskProxy
	 * @return
	 * @see #removeTask(Object)
	 */
	public final boolean removeTask(DoTaskProxy<TaskType> doTaskProxy) {
		boolean r = false;
		if (doTaskProxy != null) {
			synchronized (mTasks) {
				for (DoTaskProxy<TaskType> task : mTasks) {
					if (doTaskProxy.equals(task)) {
						r = mTasks.remove(doTaskProxy);
						break;
					}
				}
			}
		}
		return r;
	}

	/**
	 * Remove task by the given token. The {@link DoTaskProxy} object may be
	 * remove if it token equals the given token.
	 * 
	 * @param token
	 * @return
	 * @see DoTaskProxy#DoTaskProxy(Object, Object, Object...)
	 */
	public final boolean removeTask(Object token) {
		boolean r = false;
		if (token != null) {
			synchronized (mTasks) {
				for (DoTaskProxy<TaskType> task : mTasks) {
					if (token.equals(task.getToken())) {
						r = mTasks.remove(task);
						break;
					}
				}
			}
		}
		return r;
	}

	/**
	 * Inherit it and implement the abstract method {@link #doTask(Object)}.
	 * 
	 * @author AlbieLiang
	 *
	 * @param <TaskType>
	 * @see WorkerThread
	 * @see ArgsTransferHelper
	 */
	public static abstract class DoTaskProxy<TaskType> extends ArgsTransferHelper {
		
		private TaskType task;
		private Object token;
		private boolean doInLooper;
		
		/**
		 * 
		 * @param token
		 *            the token can be used to remove task from the task queue,
		 *            know more see {@link WorkerThread#removeTask(Object)}
		 * @param task
		 *            can be data or a real task to do in
		 *            {@link #doTask(Object)}
		 * @param args
		 *            arguments to transfer into the {@link DoTaskProxy} and
		 *            invoke {@link #getArg(int)} to get them
		 */
		public DoTaskProxy(Object token, TaskType task, Object... args) {
			super(args);
			this.task = task;
			this.token = token;
		}
		
		public final TaskType getTask() {
			return task;
		}
		
		protected Object getToken() {
			return token;
		}
		
		public final void setDoInLooper(boolean doInLooper) {
			this.doInLooper = doInLooper;
		}
		
		public final boolean isDoInLooper() {
			return doInLooper;
		}
		
		@Override
		public boolean equals(Object o) {
			return this == o || token == o || (token != null && token.equals(o));
		}

		/**
		 * Write do-task logic in the method.
		 * 
		 * @param task
		 */
		public abstract void doTask(TaskType task);
	}
}