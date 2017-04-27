package com.bemetoy.bp.plugin.personalcenter.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.events.AddressesChangeEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiAddNewAddressBinding;
import com.bemetoy.bp.plugin.personalcenter.model.PersonalCenterLogic;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.base.IPluginStub;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.bp.uikit.ToastUtil;
import com.bemetoy.bp.uikit.widget.MixedWordCountTextWatcher;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.util.RegionManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by albieliang on 16/5/25.
 */
public class AddNewAddressUI extends BaseDataBindingActivity<UiAddNewAddressBinding> {

    private static final String TAG = "PersonalCenter.PersonalDetailInfoUI";

    private String mProvince = "";
    private String mCity = "";
    private String mDistrict = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onBackPressed() {
        setResult(ProtocolConstants.ResultCode.ACTION_CANCEL);
        super.onBackPressed();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_add_new_address;
    }

    protected void init() {
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(ProtocolConstants.ResultCode.ACTION_CANCEL);
                finish();
            }
        });

        mBinding.commAddressEditContainer.contactEt.addTextChangedListener(new
                MixedWordCountTextWatcher(mBinding.commAddressEditContainer.contactEt, null,
                ProtocolConstants.Address.CONTACT_MAX_LENGTH));

        mBinding.commAddressEditContainer.detailAddressEt.addTextChangedListener(new
                MixedWordCountTextWatcher(mBinding.commAddressEditContainer.detailAddressEt, null,
                ProtocolConstants.Address.DETAIL_ADDRESS_LENGTH));

        mBinding.commAddressEditContainer.regionEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ProtocolConstants.IntentParams.LOCATION_PROVINCE, mProvince);
                bundle.putString(ProtocolConstants.IntentParams.LOCATION_DISTRICT, mDistrict);
                bundle.putString(ProtocolConstants.IntentParams.LOCATION_CITY, mCity);

                PluginStubBus.doActionForResult(AddNewAddressUI.this, PluginConstants.Plugin.PLUGIN_NAME_APP,
                        PluginConstants.App.Action.CMD_SHOW_LOCATION_DIALOG, bundle, 0, new IPluginStub.OnActionResult(){
                    @Override
                    public void onActionResult(Context context, int cmd, int resultCode, Bundle data) {
                        mProvince = data.getString(ProtocolConstants.IntentParams.LOCATION_PROVINCE);
                        mCity = data.getString(ProtocolConstants.IntentParams.LOCATION_CITY);
                        mDistrict = data.getString(ProtocolConstants.IntentParams.LOCATION_DISTRICT);
                        mBinding.commAddressEditContainer.regionEt.setText(Util.contactString(new String[] {mProvince, mCity, mDistrict},null));
                    }
                });
            }
        });

        mBinding.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalCenterLogic.saveAddress(AddNewAddressUI.this, getAddress(), new PersonalCenterLogic.OnAddressSaved() {
                    @Override
                    public void OnAddressSaveSuccess(final Racecar.Address address) {
                        ThreadPool.post(new Runnable() {
                            @Override
                            public void run() {
                                Racecar.AccountInfo accountInfo = AccountLogic.getAccountInfo();
                                List<Racecar.Address> addressList = new ArrayList(accountInfo.getAddressList());
                                // if current address is the default, then update the local data.
                                if(address.getFlag() == 1) {
                                    for(int i = 0; i < addressList.size(); i++) {
                                        Racecar.Address a = addressList.get(i);
                                        if(a.getFlag() == 1) {
                                            addressList.set(i, Racecar.Address.newBuilder().mergeFrom(a).setFlag(0).build());
                                            break;
                                        }
                                    }
                                }
                                addressList.add(address);
                                AccountLogic.updateAccountInfo(StorageConstants.Info_Key.ADDRESS, addressList);
                                Log.i(TAG, "update user local address list successfully");
                                RxEventBus.getBus().publish(new AddressesChangeEvent());
                            }
                        });
                        Intent intent = new Intent();
                        intent.putExtra(ProtocolConstants.IntentParams.ADDRESS_INFO, getAddress());
                        setResult(ProtocolConstants.ResultCode.ACTION_DONE, intent);
                        finish();
                    }

                    @Override
                    public void OnAddressSaveFailed(int errorCode) {
                        ToastUtil.show(R.string.personal_center_save_address_fail);
                    }
                });
            }
        });
        RegionManager.getInstance().initRegion(new RegionManager.IOnRegionInitCallback (){
            @Override
            public void onRegionInited(Map<String, ArrayList<LinkedHashMap<String, ArrayList<String>>>> maps) {
                mBinding.commAddressEditContainer.regionEt.setEnabled(maps.size() > 0);
            }
        });
    }

    private Racecar.Address getAddress() {
        Racecar.Address.Builder builder = Racecar.Address.newBuilder();
        builder.setContact(mBinding.commAddressEditContainer.contactEt.getText().toString().trim());
        builder.setDetail(mBinding.commAddressEditContainer.detailAddressEt.getText().toString().trim());
        builder.setMobile(mBinding.commAddressEditContainer.mobileEt.getText().toString().trim());
        if(!Util.isNullOrNil(mDistrict)) {
            builder.setDistrict(mDistrict);
        }
        builder.setCity(mCity);
        builder.setProvince(mProvince);
        // if the user do not have any address, set the address as default one.
        if(AccountLogic.getAccountInfo().getAddressCount() == 0) {
            builder.setFlag(1);
        } else {
            builder.setFlag(mBinding.consigneeEt.isChecked() ? 1 : 0);
        }
        return builder.build();
    }
}
