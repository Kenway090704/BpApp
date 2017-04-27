package com.bemetoy.stub.account;

import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.model.AccountNotReadyException;

/**
 * Created by albieliang on 16/3/12.
 */
public class AccountManager {

    private static final String TAG = "Acc.AccountManager";

    public static final int INVALID_UID = ProtocolConstants.MM_INVALID_USER_ID;

    private AccountStateListener mAccountStateListener;

    private volatile int mUserId;

    private volatile String mSessionKey;

    private AccInfo mAccountInfo;

    public AccountManager() {
        mAccountInfo = new AccInfo();
    }

    /**
     *
     */
    public synchronized void initialize() {
        if (!isReady()) {
            throw new AccountNotReadyException();
        }
        mSessionKey = genSessionKey();
        // TODO: 16/3/7 albieliang
        Log.i(TAG, "account manager initialize finished.");
        if (mAccountStateListener != null) {
            mAccountStateListener.onInitDatabase(this);
            mAccountStateListener.onReady(this);
        }
    }

    /**
     * Invoke when logout.
     */
    public synchronized void reset() {
        mUserId = INVALID_UID;
        if (mAccountStateListener != null) {
            mAccountStateListener.onReset(this);
        }
        mAccountInfo.reset();
        AccountLogic.resetAccountInfo();
        //AppCore.getCore().getRequestQueue().cancelAllRequest();
        Log.i(TAG, "account manager reset finished.");
    }

    public synchronized boolean isReady() {
        return isValidUin(mUserId);
    }

    public AccountManager setAccountStateListener(AccountStateListener l) {
        mAccountStateListener = l;
        return this;
    }

    /**
     * Set user uid.
     *
     * @param userId
     *          it can not be {@link #INVALID_UID}
     * @return
     */
    public AccountManager setUserId(int userId) {
        if (userId == INVALID_UID) {
            throw new IllegalArgumentException("can not reset uin by setter, use rest() method instead");
        }
        mUserId = userId;
        return this;
    }

    /**
     *
     * @return
     */
    public int getUserId() {
        return mUserId;
    }

    public String getSessionKey() {
        return mSessionKey;
    }

    private String genSessionKey() {
        String sessionKey = "id#" + mUserId;
        return sessionKey;
    }

    /**
     *
     * @return
     */
    public AccInfo getAccountInfo() {
        return mAccountInfo;
    }


    public boolean isLogin(){
        return !Util.isNullOrNil(mAccountInfo.getSessionKey());
    }

    /**
     *
     * @param userUin
     * @return
     */
    public static boolean isValidUin(int userUin) {
        return userUin != INVALID_UID;
    }

    /**
     *
     */
    public interface AccountStateListener {
        void onReady(AccountManager accMgr);
        void onInitDatabase(AccountManager accMgr);
        void onReset(AccountManager accMgr);
    }
}
