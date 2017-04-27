package com.bemetoy.bp.sdk.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.GetValueUtils;
import com.bemetoy.bp.sdk.utils.Util;

/**
 *
 * @author AlbieLiang
 *
 */
public class ApplicationContext {

	private static final String TAG = "App.ApplicationContext";

	private static WeakReference<Application> sApplicationWeakRef;
	private static WeakReference<Context> sCurrContextWeakRef;

	public static Application getApplication() {
		return sApplicationWeakRef != null ? sApplicationWeakRef.get() : null;
	}

	public static void setApplication(Application application) {
		if (application == null) {
			ApplicationContext.sApplicationWeakRef = null;
			return;
		}
		ApplicationContext.sApplicationWeakRef = new WeakReference<Application>(application);
	}

	public static Context getCurrentContext() {
		return sCurrContextWeakRef != null ? sCurrContextWeakRef.get() : null;
	}

	public static void setCurrentContext(Context context) {
		if (context == null) {
			ApplicationContext.sCurrContextWeakRef = null;
			return;
		}
		ApplicationContext.sCurrContextWeakRef = new WeakReference<Context>(context);
	}

	public static Context getContext() {
		Context c = getCurrentContext();
		if (c == null) {
			Log.e(TAG, "ApplicationContext's currentContext is null.");
			c = getApplication();
		}
		return c;
	}

	public static String getPidKey(Context context, int pid) {
		// TODO: 16/3/8 albieliang 
		return "@MAIN";
	}
}
