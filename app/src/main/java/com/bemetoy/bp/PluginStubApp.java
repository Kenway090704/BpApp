package com.bemetoy.bp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bemetoy.bp.model.HomeLogic;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.base.PluginStubAdapter;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.ui.HomePageUI;
import com.bemetoy.bp.ui.LauncherUI;
import com.bemetoy.bp.ui.auth.RegisterLocationUI;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.app.WebViewUI;
import com.bemetoy.stub.download.UpdateManager;
import com.bemetoy.stub.model.LocationLogic;
import com.bemetoy.stub.util.AppUtil;

/**
 * Created by Tom on 2016/4/27.
 */
public class PluginStubApp extends PluginStubAdapter {

    @Override
    public boolean doAction(final Context context, final int cmd, Bundle data, int flag, final OnActionResult onActionResult) {
        switch (cmd) {
            case PluginConstants.App.Action.CMD_SHOW_LOCATION_DIALOG:
                String province = null;
                String city = null;
                String district = null;
                if (data != null) {
                    province = data.getString(ProtocolConstants.IntentParams.LOCATION_PROVINCE);
                    city = data.getString(ProtocolConstants.IntentParams.LOCATION_CITY);
                    district = data.getString(ProtocolConstants.IntentParams.LOCATION_DISTRICT);
                }
                LocationLogic.showLocationDialog(context, new LocationLogic.OnGetLocation() {
                    @Override
                    public void onGetLocation(String province, String city, String district) {
                        if (onActionResult != null) {
                            if (Util.isNullOrNil(district)) {
                                district = "";
                            }
                            Bundle result = new Bundle();
                            result.putString(ProtocolConstants.IntentParams.LOCATION_PROVINCE, province);
                            result.putString(ProtocolConstants.IntentParams.LOCATION_CITY, city);
                            result.putString(ProtocolConstants.IntentParams.LOCATION_DISTRICT, district);
                            onActionResult.onActionResult(context, cmd, OnActionResult.ACTION_RESULT_OK, result);
                        }
                    }
                }, province, city, district);
                break;

            case PluginConstants.App.Action.CMD_START_LOCATION_CHOOSE_UI:
                startActivity(context, RegisterLocationUI.class, flag, data);
                break;

            case PluginConstants.App.Action.CMD_START_HOME_PAGE:
                startActivity(context, HomePageUI.class, flag, data);
                break;

            case PluginConstants.App.Action.CMD_START_LAUNCHER_UI:
                startActivity(context, LauncherUI.class, flag, data);
                break;
            case PluginConstants.App.Action.CMD_START_ACTIVATION:
                HomeLogic.showExchangeDialog(context);
                break;
            case PluginConstants.App.Action.CMD_START_TMALL:
                if(!AppUtil.checkNetworkFirst((Activity) context)) {
                    return true;
                }
                Intent intent = new Intent(context, WebViewUI.class);
                intent.putExtra(ProtocolConstants.IntentParams.URL_INFO, HomePageUI.getTmallStoreUrl());
                context.startActivity(intent);
        }
        return super.doAction(context, cmd, data, flag, onActionResult);
    }


    @Override
    public boolean setString(String key, String value) {
        if (PluginConstants.App.DataKey.SET_UPDATE_LOG.equals(key)) {
            UpdateManager.getManager().setUpdateLog(value);
            return true;
        } else if (PluginConstants.App.DataKey.SET_DOWN_FILE_PATH.equals(key)) {
            UpdateManager.getManager().setFilePath(value);
            return true;
        } else if (PluginConstants.App.DataKey.SET_DOWN_FILE_MD5.equals(key)) {
            UpdateManager.getManager().setFileMd5(value);
            return true;
        }
        return super.setString(key, value);
    }

    @Override
    public String getString(String key, String def) {
        if (PluginConstants.App.DataKey.GET_UPDATE_LOG.equals(key)) {
            return UpdateManager.getManager().getUpdateLog();
        } else if (PluginConstants.App.DataKey.GET_DOWN_FILE_PATH.equals(key)) {
            return UpdateManager.getManager().getFilePath();
        } else if (PluginConstants.App.DataKey.GET_DOWN_FILE_MD5.equals(key)) {
            return UpdateManager.getManager().getFileMd5();
        }

        return super.getString(key, def);
    }

    @Override
    public boolean setLong(String key, long value) {
        if (PluginConstants.App.DataKey.SET_APK_DOWNLOAD_ID.equals(key)) {
            UpdateManager.getManager().setAppDownloadId(value);
            return true;
        }
        return super.setLong(key, value);
    }

    @Override
    public long getLong(String key, long def) {
        if (PluginConstants.App.DataKey.GET_APK_DOWNLOAD_ID.equals(key)) {
            return UpdateManager.getManager().getAppDownloadId();
        }
        return super.getLong(key, def);
    }

    @Override
    public int getInt(String key, int def) {
        if (PluginConstants.App.DataKey.GET_USER_ID.equals(key)) {
            return AppCore.getCore().getAccountManager().getUserId();
        }
        return super.getInt(key, def);
    }

    @Override
    public boolean setInt(String key, int value) {
        if (PluginConstants.App.DataKey.SET_USER_ID.equals(key)) {
            AppCore.getCore().getAccountManager().setUserId(value);
            return true;
        }
        return super.setInt(key, value);
    }
}
