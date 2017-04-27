package com.bemetoy.stub.account;

import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.persistence.fs.FSConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.app.AppCore;

import java.io.File;

/**
 * Created by Tom on 2016/5/17.
 */
public class AccountSettingLogic {

    private static final String TAG = "stub.AccountSettingLogic";

    public static void saveSetting(int key, Object value) {

        if(Util.isNullOrNil(AppCore.getCore().getAccountManager().getSessionKey())) {
            Log.e(TAG, "the file can not be edit when the session key empty");
            return;
        }

        String userDataPath = FSConstants.getCfgPath(AppCore.getCore().getAccountManager().getSessionKey());
        String accInfoFile = userDataPath + File.separator + StorageConstants.FileName.SETTING_FILE;
        FileUtils.createFileIfNeed(new File(accInfoFile));
        CfgFs cfgFs = new CfgFs(accInfoFile);
        cfgFs.set(key,value);
        Log.i(TAG, "update account info key is %s and value is %s", key, value);
    }

    public static Object getSetting(int key) {

        if(Util.isNullOrNil(AppCore.getCore().getAccountManager().getSessionKey())) {
            Log.e(TAG, "the file can not be edit when the session key empty");
            return null;
        }

        String userDataPath = FSConstants.getCfgPath(AppCore.getCore().getAccountManager().getSessionKey());
        String accInfoFile = userDataPath + File.separator + StorageConstants.FileName.SETTING_FILE;
        FileUtils.createFileIfNeed(new File(accInfoFile));
        CfgFs cfgFs = new CfgFs(accInfoFile);
        return cfgFs.get(key);
    }
}
