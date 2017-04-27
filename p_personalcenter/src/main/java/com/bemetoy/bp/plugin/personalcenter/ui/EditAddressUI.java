package com.bemetoy.bp.plugin.personalcenter.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.events.AddressesChangeEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiEditAddressBinding;
import com.bemetoy.bp.plugin.personalcenter.model.AddressInfo;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by albieliang on 16/5/25.
 */
public class EditAddressUI extends BaseDataBindingActivity<UiEditAddressBinding> {

    private static final String TAG = "PersonalCenter.EditAddressUI";
    private AddressInfo mAddressInfo;

    private String mProvince = "";
    private String mCity = "";
    private String mDistrict = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_edit_address;
    }

    protected void init() {

        mAddressInfo = getIntent().getParcelableExtra(ProtocolConstants.IntentParams.ADDRESS_INFO);
        mBinding.setAddress(mAddressInfo);

        mProvince = mAddressInfo.getProvince();
        mCity = mAddressInfo.getCity();
        mDistrict = mAddressInfo.getDistrict();
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Racecar.Address address = getAddress();
                PersonalCenterLogic.showDeleteAddressDialog(EditAddressUI.this, address, new PersonalCenterLogic.OnAddressDeleted() {
                    @Override
                    public void onAddressDeleted(final int addressId) {
                        finish();
                    }
                });
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

                PluginStubBus.doActionForResult(EditAddressUI.this, PluginConstants.Plugin.PLUGIN_NAME_APP,
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
                PersonalCenterLogic.saveAddress(EditAddressUI.this, getAddress(), new PersonalCenterLogic.OnAddressSaved() {
                    @Override
                    public void OnAddressSaveSuccess(final Racecar.Address address) {
                        ThreadPool.post(new Runnable() {
                            @Override
                            public void run() {
                                Racecar.AccountInfo accountInfo = AccountLogic.getAccountInfo();
                                List<Racecar.Address> addressList = new ArrayList(accountInfo.getAddressList());
                                for(int i = 0; i < addressList.size(); i++) {
                                    Racecar.Address a = addressList.get(i);
                                    if(a.getId() == address.getId()) {
                                        addressList.set(i, address);
                                        break;
                                    }
                                }
                                AccountLogic.updateAccountInfo(StorageConstants.Info_Key.ADDRESS,addressList);
                                Log.i(TAG, "update local address list successfully");
                                RxEventBus.getBus().publish(new AddressesChangeEvent());
                            }
                        });
                        ToastUtil.show(R.string.personal_center_save_address_success);
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
        builder.setId(mAddressInfo.getId());
        builder.setContact(mBinding.commAddressEditContainer.contactEt.getText().toString().trim());
        builder.setDetail(mBinding.commAddressEditContainer.detailAddressEt.getText().toString().trim());
        builder.setMobile(mBinding.commAddressEditContainer.mobileEt.getText().toString().trim());
        builder.setDistrict(mDistrict);
        builder.setCity(mCity);
        builder.setProvince(mProvince);
        builder.setFlag(mAddressInfo.isDefault() ? 1 : 0);
        return builder.build();
    }
}
