package com.bemetoy.stub.account;

import com.bemetoy.bp.autogen.events.AccountUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.persistence.fs.FSConstants;
import com.bemetoy.bp.persistence.fs.base.KeyValueAccessor;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.network.auth.NetSceneGetAccount;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by albieliang on 16/4/30.
 */
public class AccountLogic {

    private static final String TAG = "Acc.AccountLogic";
    private static String avatarUrl = null;

    private static CfgFsCache accountCache;


    static {
        FileUtils.createFileIfNeed(new File(StorageConstants.COMM_SETTING_PATH));
        CfgFs cfgFs = new CfgFs(StorageConstants.COMM_SETTING_PATH);
        avatarUrl = cfgFs.getString(StorageConstants.SETTING_KEY.AVATAR_BASE_URL, ProtocolConstants.ALPHA_AVATAR_BASE_URL);
    }


    /**
     * Save the account info to the local
     *
     * @param info
     */
    public static void saveAccountInfo(Racecar.AccountInfo info) {
        if (info == null) {
            return;
        }

        accountCache = new CfgFsCache(info);

        String userDataPath = FSConstants.getCfgPath(AppCore.getCore().getAccountManager().getSessionKey());
        String accInfoFile = userDataPath + File.separator + StorageConstants.FileName.USER_FILE;
        FileUtils.createFileIfNeed(new File(accInfoFile));

        CfgFs cfgFs = new CfgFs(accInfoFile);
        saveAccountInfo(cfgFs, info);
        RxEventBus.getBus().publish(new AccountUpdateEvent());
        Log.d(TAG, info.getName() + " and user id is " + info.getUserId());
    }

    public static Racecar.Address getDefaultAddress() {
        Racecar.AccountInfo accountInfo = getAccountInfo(true);
        Racecar.Address address = null;
        for (Racecar.Address addr : accountInfo.getAddressList()) {
            if (addr.getFlag() == 1) {
                address = addr;
            }
        }
        return address;
    }

    /**
     * Get account info form the local :
     * <p/>
     * Get the value from the cache, if the cache is null
     * then get it from the file
     *
     * @return
     */
    public static synchronized Racecar.AccountInfo getAccountInfo() {
        return getAccountInfo(true);
    }


    /**
     * Get account info form the local :
     * <p/>
     * Get the value from the cache, if the cache is null
     * then get it from the file
     *
     * @return
     */
    public static synchronized Racecar.AccountInfo getAccountInfo(boolean useCache) {

        // if current user data path is null ,that means the user does not login, so return the empty.
        Racecar.AccountInfo.Builder builder = Racecar.AccountInfo.newBuilder();
        String userDataPath = FSConstants.getCfgPath(AppCore.getCore().getAccountManager().getSessionKey());
        if (userDataPath == null) {
            return Racecar.AccountInfo.getDefaultInstance();
        }

        if (accountCache != null && useCache) {
            return accountCache.build();
        }

        String accInfoFile = userDataPath + File.separator + StorageConstants.FileName.USER_FILE;
        FileUtils.createFileIfNeed(new File(accInfoFile));

        CfgFs cfgFs = new CfgFs(accInfoFile);

        Racecar.AccountInfo accountInfo = buildFromValue(cfgFs);
        accountCache = new CfgFsCache(accountInfo);

        return accountInfo;
    }

    /**
     * Update the account via the key-value.
     *
     * @param key
     * @param value
     */
    public static void updateAccountInfo(int key, Object value) {

        String userDataPath = FSConstants.getCfgPath(AppCore.getCore().getAccountManager().getSessionKey());
        if (userDataPath == null) {
            return;
        }

        if (accountCache != null) {
            accountCache.set(key, value);
        }

        String accInfoFile = userDataPath + File.separator + StorageConstants.FileName.USER_FILE;
        FileUtils.createFileIfNeed(new File(accInfoFile));
        CfgFs cfgFs = new CfgFs(accInfoFile);
        cfgFs.set(key, value);
        RxEventBus.getBus().publish(new AccountUpdateEvent());
        Log.i(TAG, "update account info key is %s and value is %s", key, value);
    }

    /**
     * Clear the local account info.
     */
    public static void resetAccountInfo() {
        CfgFs cfgFs = new CfgFs(StorageConstants.USER_DATA_PATH);
        cfgFs.reset();

        if (accountCache != null) {
            accountCache.reset();
        }

        String dataFilePath = StorageConstants.ACCOUNT_DATA_PATH;
        if (FileUtils.isFileExist(dataFilePath)) {
            CfgFs accountData = new CfgFs(StorageConstants.ACCOUNT_DATA_PATH);
            accountData.reset();
        }
    }

    public static void updateAccountFromSVR() {
        NetSceneGetAccount netSceneGetAccount = new NetSceneGetAccount(new NetSceneResponseCallback<Racecar.GetAccountInfoResponse>() {
            @Override
            public void onResponse(IRequest request, Racecar.GetAccountInfoResponse result) {
                if (result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                    saveAccountInfo(result.getAccountInfo());
                }
            }
        });
        netSceneGetAccount.doScene();
    }

    public static void setAvatarUrl(String url) {
        avatarUrl = url;
    }

    public static String getBaseAvatarUrl() {
        return avatarUrl;
    }

    public static String getAvatarImageUrl(String fileName) {
        return avatarUrl + fileName;
    }

    private static void saveAccountInfo(KeyValueAccessor<Integer> keyValueAccessor, Racecar.AccountInfo originalAccountInfo) {
        if (keyValueAccessor == null || originalAccountInfo == null) {
            Log.e(TAG, "keyValueAccessor or accountInfo is empty.");
            return;
        }

        keyValueAccessor.setString(StorageConstants.Info_Key.PROVINCE, originalAccountInfo.getProvince());
        keyValueAccessor.setString(StorageConstants.Info_Key.CITY, originalAccountInfo.getCity());
        keyValueAccessor.setString(StorageConstants.Info_Key.DISTRICT, originalAccountInfo.getDistrict());
        keyValueAccessor.setInt(StorageConstants.Info_Key.EXP, originalAccountInfo.getExp());
        keyValueAccessor.setInt(StorageConstants.Info_Key.LEVEL, originalAccountInfo.getLevel());
        keyValueAccessor.setString(StorageConstants.Info_Key.HDHEAD_IMG_URL, originalAccountInfo.getIcon());
        keyValueAccessor.setInt(StorageConstants.Info_Key.SCORE, originalAccountInfo.getScore());
        keyValueAccessor.setInt(StorageConstants.Info_Key.TOTAL_EXP, originalAccountInfo.getTotalExp());
        keyValueAccessor.setInt(StorageConstants.Info_Key.NEXT_LEVEL_UP, originalAccountInfo.getLevelExp());
        keyValueAccessor.setInt(StorageConstants.Info_Key.CART_COUNT, originalAccountInfo.getCar41Count());
        keyValueAccessor.setInt(StorageConstants.Info_Key.PARTS_COUNT, originalAccountInfo.getPartsCount());
        keyValueAccessor.setInt(StorageConstants.Info_Key.NATIONAL_RANK, originalAccountInfo.getNationalRank());
        keyValueAccessor.setInt(StorageConstants.Info_Key.PROVINCE_RANK, originalAccountInfo.getProvinceRank());
        keyValueAccessor.setInt(StorageConstants.Info_Key.CITY_RANK, originalAccountInfo.getCityRank());
        keyValueAccessor.setInt(StorageConstants.Info_Key.WEEKLY_RANK, originalAccountInfo.getCityWeekRank());
        keyValueAccessor.setString(StorageConstants.Info_Key.NAME, originalAccountInfo.getName());
        keyValueAccessor.setInt(StorageConstants.Info_Key.USER_NAME, originalAccountInfo.getUserId());
        keyValueAccessor.setInt(StorageConstants.Info_Key.RANK_PERCENT, originalAccountInfo.getRankPercent());
        keyValueAccessor.setInt(StorageConstants.Info_Key.SPEED, originalAccountInfo.getSpeed());
        keyValueAccessor.set(StorageConstants.Info_Key.ADDRESS, originalAccountInfo.getAddressList());
        keyValueAccessor.setString(StorageConstants.Info_Key.OLD_NAME, originalAccountInfo.getOldName());
        keyValueAccessor.set(StorageConstants.Info_Key.CARS, originalAccountInfo.getCar41List());
        keyValueAccessor.setString(StorageConstants.Info_Key.LEVEL_NAME, originalAccountInfo.getLevelName());
    }

    private static Racecar.AccountInfo buildFromValue(KeyValueAccessor<Integer> keyValueAccessor) {
        if (keyValueAccessor == null) {
            Log.w(TAG, "keyValueAccessor is null, return the default account info");
            return Racecar.AccountInfo.getDefaultInstance();
        }

        Racecar.AccountInfo.Builder builder = Racecar.AccountInfo.newBuilder();
        builder.setProvince(keyValueAccessor.getString(StorageConstants.Info_Key.PROVINCE, ""));
        builder.setCity(keyValueAccessor.getString(StorageConstants.Info_Key.CITY, ""));
        builder.setDistrict(keyValueAccessor.getString(StorageConstants.Info_Key.DISTRICT, ""));
        builder.setExp(keyValueAccessor.getInt(StorageConstants.Info_Key.EXP, 0));
        builder.setLevel(keyValueAccessor.getInt(StorageConstants.Info_Key.LEVEL, 0));
        builder.setIcon(keyValueAccessor.getString(StorageConstants.Info_Key.HDHEAD_IMG_URL, ""));
        builder.setScore(keyValueAccessor.getInt(StorageConstants.Info_Key.SCORE, 0));
        builder.setLevelExp(keyValueAccessor.getInt(StorageConstants.Info_Key.NEXT_LEVEL_UP, 0));
        builder.setTotalExp(keyValueAccessor.getInt(StorageConstants.Info_Key.TOTAL_EXP, 0));
        builder.setCarCount100(keyValueAccessor.getInt(StorageConstants.Info_Key.CART_COUNT, 0));
        builder.setPartsCount(keyValueAccessor.getInt(StorageConstants.Info_Key.PARTS_COUNT, 0));
        builder.setNationalRank(keyValueAccessor.getInt(StorageConstants.Info_Key.NATIONAL_RANK, 0));
        builder.setProvinceRank(keyValueAccessor.getInt(StorageConstants.Info_Key.PROVINCE_RANK, 0));
        builder.setCityRank(keyValueAccessor.getInt(StorageConstants.Info_Key.CITY_RANK, 0));
        builder.setCityWeekRank(keyValueAccessor.getInt(StorageConstants.Info_Key.WEEKLY_RANK, 0));
        builder.setName(keyValueAccessor.getString(StorageConstants.Info_Key.NAME, ""));
        builder.setUserId(keyValueAccessor.getInt(StorageConstants.Info_Key.USER_NAME, 0));
        builder.setOldName(keyValueAccessor.getString(StorageConstants.Info_Key.OLD_NAME, ""));
        builder.setLevelName(keyValueAccessor.getString(StorageConstants.Info_Key.LEVEL_NAME, ""));

        Object address = keyValueAccessor.get(StorageConstants.Info_Key.ADDRESS, null);
        if (address != null && address instanceof List) {
            builder.addAllAddress((List<Racecar.Address>) address);
        }

        Object cars = keyValueAccessor.get(StorageConstants.Info_Key.CARS, null);
        if (cars != null && cars instanceof List) {
            builder.addAllCar41((List<Racecar.Car>) cars);
        }

        builder.setRankPercent(keyValueAccessor.getInt(StorageConstants.Info_Key.RANK_PERCENT, 0));
        builder.setSpeed(keyValueAccessor.getInt(StorageConstants.Info_Key.SPEED, 0));
        return builder.build();

    }


    private static class CfgFsCache implements KeyValueAccessor<Integer> {
        private Map<Integer, Object> keyValues = new HashMap();

        public CfgFsCache(Racecar.AccountInfo originalAccountInfo) {
            saveAccountInfo(this, originalAccountInfo);
        }

        public Racecar.AccountInfo build() {
            return buildFromValue(this);
        }

        @Override
        public void set(Integer key, Object value) {
            keyValues.put(key, value);
        }

        @Override
        public Object get(Integer key) {
            return keyValues.get(key);
        }

        @Override
        public Object get(Integer key, Object def) {
            Object value = keyValues.get(key);
            if (value == null) {
                return def;
            }
            return value;
        }

        @Override
        public void setInt(Integer key, int val) {
            keyValues.put(key, val);
        }

        @Override
        public int getInt(Integer key, int def) {
            Object value = keyValues.get(key);
            if (value == null || !(value instanceof Integer)) {
                return def;
            }
            return (Integer) value;
        }

        @Override
        public void setLong(Integer key, long val) {
            keyValues.put(key, val);
        }

        @Override
        public long getLong(Integer key, long def) {
            Object value = keyValues.get(key);
            if (value == null || !(value instanceof Long)) {
                return def;
            }
            return (Long) value;
        }

        @Override
        public void setFloat(Integer key, float val) {
            keyValues.put(key, val);
        }

        @Override
        public float getFloat(Integer key, float def) {
            Object value = keyValues.get(key);
            if (value == null || !(value instanceof Float)) {
                return def;
            }
            return (Float) value;
        }

        @Override
        public void setDouble(Integer key, double val) {
            keyValues.put(key, val);
        }

        @Override
        public double getDouble(Integer key, double def) {
            Object value = keyValues.get(key);
            if (value == null || !(value instanceof Double)) {
                return def;
            }
            return (Double) value;
        }

        @Override
        public void setString(Integer key, String val) {
            keyValues.put(key, val);
        }

        @Override
        public String getString(Integer key, String def) {
            Object value = keyValues.get(key);
            if (value == null || !(value instanceof String)) {
                return def;
            }
            return (String) value;
        }

        void reset() {
            keyValues.clear();
        }
    }

}
