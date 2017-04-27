package com.bemetoy.bp.plugin.personalcenter.model;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.bemetoy.bp.autogen.events.AddressesChangeEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.persistence.fs.CfgFs;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiDeleteAddressBinding;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiLogoutBinding;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.FileUtils;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.BpAlertDialog;
import com.bemetoy.bp.uikit.BpDialog;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.network.NetSceneCallbackLoadingWrapper;
import com.bemetoy.stub.network.NetSceneLoadingWrapper;
import com.bemetoy.stub.util.JsonManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by albieliang on 16/4/25.
 */
public class PersonalCenterLogic {

    private static final String TAG = "PersonCenter.PersonalCenterLogic";

    public static boolean validateAddress(Racecar.Address address) {
        if (Util.isNullOrNil(address.getContact())) {
            ToastUtil.show(R.string.personal_ship_person_name_invalid);
            return false;
        }

        if (address.getContact().length() > ProtocolConstants.Address.CONTACT_MAX_LENGTH) {
            ToastUtil.show(R.string.personal_center_name_length_error);
            return false;
        }

        if (Util.isNullOrBlank(address.getMobile())) {
            ToastUtil.show(R.string.phone_format_error);
            return false;
        }

        if (Util.isNullOrNil(address.getProvince())) {
            ToastUtil.show(R.string.personal_center_region_error);
            return false;
        }

        if (Util.isNullOrNil(address.getDetail())) {
            ToastUtil.show(R.string.personal_center_detail_error);
            return false;
        }

        return true;
    }


    public static void showDeleteAddressDialog(final Context context, final Racecar.Address address, final OnAddressDeleted onAddressDeleted) {

        if(address == null) {
            Log.e(TAG, "address data is null");
            return;
        }

        final BpDialog<UiDeleteAddressBinding> dialog = new BpDialog(context, R.layout.ui_delete_address);
        dialog.mBinding.setContent(getContent(address));
        dialog.setCancelable(true);
        dialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                final NetSceneDeleteAddress deleteAddress = new NetSceneDeleteAddress(address.getId(), new NetSceneCallbackLoadingWrapper<Racecar.DeleteAddressResponse>() {
                    @Override
                    public void onResponse(IRequest request, Racecar.DeleteAddressResponse result) {
                        if (result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                            Log.i(TAG, "address which id is %d has been deleted.", address.getId());

                            ThreadPool.post(new Runnable() {
                                @Override
                                public void run() {
                                    Racecar.AccountInfo accountInfo = AccountLogic.getAccountInfo();
                                    List<Racecar.Address> addressesList = new ArrayList(accountInfo.getAddressList());
                                    Iterator<Racecar.Address> iterator = addressesList.listIterator();
                                    while (iterator.hasNext()) {
                                        Racecar.Address add = iterator.next();
                                        if (add.getId() == address.getId()) {
                                            iterator.remove();
                                            break;
                                        }
                                    }
                                    /**
                                     * if the deleted address is the default one,then the first one in the list will be the default.
                                     */
                                    if (addressesList.size() != 0 && address.getFlag() == 1) {
                                        Racecar.Address a = addressesList.get(0);
                                        addressesList.set(0, Racecar.Address.newBuilder().mergeFrom(a).setFlag(1).build());
                                        Log.i(TAG, "the new default address id is %d", a.getId());
                                    }
                                    Log.i(TAG, "update local data successfully.");
                                    AccountLogic.updateAccountInfo(StorageConstants.Info_Key.ADDRESS, addressesList);
                                    RxEventBus.getBus().publish(new AddressesChangeEvent());
                                }
                            });
                            if (onAddressDeleted != null) {
                                onAddressDeleted.onAddressDeleted(address.getId());
                            }
                        } else {
                            ToastUtil.show(R.string.personal_center_delete_error);
                        }
                    }
                });
                new NetSceneLoadingWrapper(deleteAddress).doScene();
            }
        });

        dialog.mBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Save the address data.
     * @param context
     * @param address
     * @param onAddressSaved
     */
    public static void saveAddress(final Context context, final Racecar.Address address, final OnAddressSaved onAddressSaved) {
        if (!PersonalCenterLogic.validateAddress(address)) {
            Log.i(TAG, "address data is invalid.");
            return;
        }

        if (address.getId() == 0) {
            NetSceneAddAddress sceneBase = new NetSceneAddAddress(address, new NetSceneCallbackLoadingWrapper<Racecar.AddAddressResponse>() {
                @Override
                public void onResponse(IRequest request, Racecar.AddAddressResponse result) {
                    if (result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                        Log.i(TAG, "add address id %d successfully", result.getId());
                        Racecar.Address newAddress =  Racecar.Address.newBuilder().mergeFrom(address).setId(result.getId()).build();
                        onAddressSaved.OnAddressSaveSuccess(newAddress);
                    } else {
                        Log.e(TAG, "add address failed");
                        onAddressSaved.OnAddressSaveFailed(result.getPrimaryResp().getResult());
                    }
                }

            });
            new NetSceneLoadingWrapper(sceneBase).doScene();
        } else {
            NetSceneEditAddress sceneBase = new NetSceneEditAddress(address, new NetSceneCallbackLoadingWrapper<Racecar.ModifyAddressResponse>() {
                @Override
                public void onResponse(IRequest request, Racecar.ModifyAddressResponse result) {
                    if (result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                        Log.i(TAG, "update address id %d successfully", address.getId());
                        onAddressSaved.OnAddressSaveSuccess(address);
                    } else {
                        Log.i(TAG, "update address id %d failed");
                        onAddressSaved.OnAddressSaveFailed(result.getPrimaryResp().getResult());
                    }
                }
            });
            new NetSceneLoadingWrapper(sceneBase).doScene();
        }
    }

    public static void showLogoutDialog(final Context context) {
        final BpDialog<UiLogoutBinding> logoutDialog = new BpDialog(context, R.layout.ui_logout);
        logoutDialog.setCancelable(true);
        logoutDialog.mBinding.agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
                NetSceneLogout logout = new NetSceneLogout(new NetSceneCallbackLoadingWrapper<Racecar.LogoutResponse>() {
                    @Override
                    public void onResponse(IRequest request, Racecar.LogoutResponse result) {
                        /**
                         * clear the local account data and go to the launcher UI.
                         */
                        if (result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
                            AppCore.getCore().getAccountManager().reset();
                            int flag = Intent.FLAG_ACTIVITY_NEW_TASK;
                            flag |= Intent.FLAG_ACTIVITY_CLEAR_TASK;
                            PluginStubBus.doAction(context, PluginConstants.Plugin.PLUGIN_NAME_APP,
                                    PluginConstants.App.Action.CMD_START_LAUNCHER_UI, flag, null);
                        } else {
                            ToastUtil.show(R.string.personal_logout_error);
                        }
                    }
                });

                new NetSceneLoadingWrapper(logout).doScene();
            }
        });

        logoutDialog.mBinding.disagreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.dismiss();
            }
        });
        logoutDialog.show();
    }


    public static final List<String> transformJsonData(String jsonData) {
        JSONObject jsonObject = JsonManager.convertString2Json(jsonData);
        if(Util.isNull(jsonObject)) {
            Log.e(TAG, "user avatar json data error");
            return Collections.EMPTY_LIST;
        }
        List<String> avatars = new ArrayList();
        try {
            int resultCode = jsonObject.getInt(ProtocolConstants.JsonFiled.RESULT);
            if(resultCode == ProtocolConstants.JsonValue.ALPHA_AVATAR_OK) {
                JSONObject metaData = jsonObject.getJSONObject(ProtocolConstants.JsonFiled.ICON_LIST);
                String baseUrl =  metaData.getString(ProtocolConstants.JsonFiled.ICON_URL);

                // save the base url to the local
                FileUtils.createFileIfNeed(new File(StorageConstants.COMM_SETTING_PATH));
                CfgFs cfgFs = new CfgFs(StorageConstants.COMM_SETTING_PATH);
                baseUrl =  baseUrl + ProtocolConstants.ALPHA_MAX_IMAGE_SIZE + "/";
                cfgFs.setString(StorageConstants.SETTING_KEY.AVATAR_BASE_URL, baseUrl);
                AccountLogic.setAvatarUrl(baseUrl);

                JSONArray categoryList = metaData.getJSONArray(ProtocolConstants.JsonFiled.CATEGORY);
                for(int i = 0; i < categoryList.length(); i++) {
                    JSONObject category = categoryList.getJSONObject(i);
                    JSONArray filesArray = category.getJSONArray(ProtocolConstants.JsonFiled.ICONS);
                    for(int j = 0; j < filesArray.length() ; j++) {
                        String url = (baseUrl + filesArray.getString(j));
                        avatars.add(url);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "user avatar json data error");
        }

        return avatars;
    }

    private static String getContent(Racecar.Address address) {
        StringBuffer sb = new StringBuffer();
        sb.append("\"");
        sb.append(address.getContact());
        sb.append(",");
        sb.append(address.getProvince());
        sb.append(address.getCity());
        sb.append(address.getDistrict());
        sb.append(address.getDetail());
        sb.append("\"");
        return sb.toString();
    }


    public interface OnAddressSaved {
        void OnAddressSaveSuccess(Racecar.Address address);

        void OnAddressSaveFailed(int errorCode);
    }

    public interface OnAddressDeleted {
        void onAddressDeleted(int addressId);
    }

    public interface OnEditTextDialogClickListener {
        void onConfirmClicked(BpAlertDialog dialog, String content);

        void onCancelClicked(BpAlertDialog dialog);
    }
}
