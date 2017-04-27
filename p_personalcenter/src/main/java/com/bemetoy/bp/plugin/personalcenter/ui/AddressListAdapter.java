package com.bemetoy.bp.plugin.personalcenter.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.bemetoy.bp.autogen.events.AddressesChangeEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiAddressListItemBinding;
import com.bemetoy.bp.plugin.personalcenter.model.AddressInfo;
import com.bemetoy.bp.plugin.personalcenter.model.PersonalCenterLogic;
import com.bemetoy.bp.protocols.PluginConstants;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.PluginStubBus;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;
import com.bemetoy.stub.account.AccountLogic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/6/21.
 */
public class AddressListAdapter extends ExtRecyclerViewAdapter<Racecar.Address> {

    private static final String TAG = "PersonalCenter.AddressListAdapter";

    private LayoutInflater mInflater;
    private RadioButton lastCheckBox;
    private boolean chooseMode;
    private boolean isFirstCheck = true;
    private int currentCheckId = 0;

    public AddressListAdapter(Context context, boolean chooseMode) {
        mInflater = LayoutInflater.from(context);
        this.chooseMode = chooseMode;
    }

    public void setCurrentCheckId(int currentCheckId) {
        this.currentCheckId = currentCheckId;
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return mInflater.inflate(R.layout.ui_address_list_item, parent, false);
    }

    @Override
    public IViewHolder<Racecar.Address> onCreateViewHolder(View view, int viewType) {
        return new AddressVH();
    }

    private class AddressVH extends DataBindingViewHolder<UiAddressListItemBinding, Racecar.Address> {
        @Override
        public void onCreateBinding(View itemView, int viewType) {
            super.onCreateBinding(itemView, viewType);
            mBinding.setChooseMode(chooseMode);
            if (chooseMode) {
                mBinding.checkboxLabelTv.setText(R.string.personal_center_choose_ship_address);
            }

        }

        @Override
        public void onBind(final Racecar.Address item, int viewType) {
            super.onBind(item, viewType);
            mBinding.setAddress(item);
            mBinding.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ProtocolConstants.IntentParams.ADDRESS_INFO, new AddressInfo(getItem()));
                    PluginStubBus.doAction(v.getContext(), PluginConstants.Plugin.PLUGIN_NAME_P_PERSONAL_CENTER,
                            PluginConstants.PersonalCenter.Action.CMD_START_EDIT_ADDRESS_UI, 0, bundle);
                }
            });

            mBinding.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonalCenterLogic.showDeleteAddressDialog(v.getContext(), getItem(), null);
                }
            });

            if (chooseMode) {
                mBinding.defaultAddressCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked && isFirstCheck) {
                            isFirstCheck = false;
                            return;
                        }

                        //如果用户选择跟当前的ID是一样的，表示选择没有改。
                        if(currentCheckId == item.getId()) {
                            return;
                        }

                        if (isChecked && buttonView.getContext() instanceof Activity) {
                            Activity activity = (Activity) buttonView.getContext();
                            Intent intent = new Intent();
                            intent.putExtra(ProtocolConstants.IntentParams.ADDRESS_INFO, item);
                            activity.setResult(ProtocolConstants.ResultCode.ACTION_DONE, intent);
                            activity.finish();
                        }
                    }
                });

                if (item.getId() == currentCheckId) {
                    mBinding.defaultAddressCb.setChecked(true);
                } else {
                    mBinding.defaultAddressCb.setChecked(false);
                }
            } else {
                if (item.getFlag() == 1) {
                    lastCheckBox = mBinding.defaultAddressCb;
                    mBinding.defaultAddressCb.setChecked(true);
                } else {
                    mBinding.defaultAddressCb.setChecked(false);
                }

                mBinding.defaultAddressCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            return;
                        }

                        if (lastCheckBox != null && lastCheckBox != buttonView) {
                            lastCheckBox.setChecked(false);

                            lastCheckBox = (RadioButton) buttonView;

                            /**
                             * if current item is the default address, return directly.
                             */
                            if (getItem().getFlag() == 1) {
                                return;
                            }

                            final Racecar.Address address = Racecar.Address.newBuilder().mergeFrom(getItem()).setFlag(1).build();
                            PersonalCenterLogic.saveAddress(buttonView.getContext(), address, new PersonalCenterLogic.OnAddressSaved() {
                                @Override
                                public void OnAddressSaveSuccess(final Racecar.Address newAddress) {

                                    ThreadPool.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            List<Racecar.Address> addressList = new ArrayList(AccountLogic.getAccountInfo().getAddressList());
                                            for (int i = 0; i < addressList.size(); i++) {
                                                Racecar.Address a = addressList.get(i);
                                                if (a.getId() == newAddress.getId()) {
                                                    addressList.set(i, newAddress);
                                                    continue;
                                                }

                                                /**
                                                 * uncheck the old default.
                                                 */
                                                if (a.getFlag() == 1) {
                                                    addressList.set(i, Racecar.Address.newBuilder().mergeFrom(a).setFlag(0).build());
                                                }
                                            }

                                            AccountLogic.updateAccountInfo(StorageConstants.Info_Key.ADDRESS, addressList);
                                            Log.i(TAG, "update default address ");
                                            RxEventBus.getBus().publish(new AddressesChangeEvent());
                                        }
                                    });
                                }

                                @Override
                                public void OnAddressSaveFailed(int errorCode) {

                                }
                            });
                        }
                    }
                });
            }
        }
    }
}
