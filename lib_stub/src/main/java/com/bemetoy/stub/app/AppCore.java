package com.bemetoy.stub.app;

import com.bemetoy.bp.network.RequestQueue;
import com.bemetoy.bp.network.RequestQueueImpl;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.bp.sdk.plugin.imageloader.ImageLoader;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.account.AccountManager;
import com.bemetoy.stub.account.AccountManager.AccountStateListener;
import com.bemetoy.stub.network.INetWorkDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albieliang on 16/3/7.
 */
public class AppCore {

    private static AppCore sCore;

    private static String sResetUIDStack;

    private RequestQueue mRequestQueue;

    private INetWorkDispatcher mDispatcher;
    private AccountManager mAccountManager;
    private List<AccountStateListener> mAccountStateListeners = new ArrayList();
    private OnAppCoreInitListener mOnAppCoreInitListener;

    private AppCore() {
        AppCore.setResetUIDStack(Util.getStack(new Throwable()));
        mAccountManager = new AccountManager();
        mAccountManager.setAccountStateListener(new AccountStateListener() {
            @Override
            public void onReady(AccountManager accMgr) {
                for (AccountStateListener listener : mAccountStateListeners) {
                    listener.onReady(accMgr);
                }
            }

            @Override
            public void onInitDatabase(AccountManager accMgr) {
                for (AccountStateListener listener : mAccountStateListeners) {
                    listener.onInitDatabase(accMgr);
                }
            }

            @Override
            public void onReset(AccountManager accMgr) {
                for (AccountStateListener listener : mAccountStateListeners) {
                    listener.onReset(accMgr);
                }
            }
        });
    }

    public synchronized static AppCore getCore() {
        if (sCore == null) {
            sCore = new AppCore();
        }
        return sCore;
    }

    /**
     *
     */
    public void initialize() {
        if (mOnAppCoreInitListener != null) {
            mOnAppCoreInitListener.beforeInit();
        }
        mRequestQueue = RequestQueueImpl.getNetSceneQueue(ApplicationContext.getApplication());
        ImageLoader.initImageLoader(ApplicationContext.getApplication());
        //CrashHandler.getInstance().init();
        if (mOnAppCoreInitListener != null) {
            mOnAppCoreInitListener.afterInit();
        }
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public AccountManager getAccountManager() {
        return mAccountManager;
    }


    public static String getResetUIDStack() {
        return sResetUIDStack;
    }

    public static void setResetUIDStack(String stack) {
        sResetUIDStack = stack;
    }

    public void addAccountStateListener(AccountStateListener l) {
        mAccountStateListeners.add(l);
    }

    public void removeAccountStateListener(AccountStateListener l) {
        mAccountStateListeners.remove(l);
    }


    public void setOnAppCoreInitListener(OnAppCoreInitListener l) {
        mOnAppCoreInitListener = l;
    }

    public INetWorkDispatcher getDispatcher() {
        return mDispatcher;
    }

    public void setDispatcher(INetWorkDispatcher mDispatcher) {
        this.mDispatcher = mDispatcher;
    }

    /**
     *
     */
    public interface OnAppCoreInitListener {
        void beforeInit();
        void afterInit();
    }
}
