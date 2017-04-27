package com.bemetoy.bp.plugin.personalcenter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bemetoy.bp.autogen.events.AddressesChangeEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiAddressListBinding;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.ui.BPDialogManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by albieliang on 16/5/25.
 */
public class AddressListUI extends BaseDataBindingActivity<UiAddressListBinding> {

    private static final String TAG = "PersonalCenter.AddressListUI";
    private AddressListAdapter defaultAdapter;
    private EventObserver eventObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_address_list;
    }


    @Override
    public void onBackPressed() {
        if(defaultAdapter.getItems().isEmpty()) {
            setResult(ProtocolConstants.ResultCode.ACTION_GO_BACK);
        } else {
            setResult(ProtocolConstants.ResultCode.ACTION_CANCEL);
        }
        super.onBackPressed();
    }

    protected void init() {
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(defaultAdapter.getItems().isEmpty()) {
                    setResult(ProtocolConstants.ResultCode.ACTION_GO_BACK);
                } else {
                    setResult(ProtocolConstants.ResultCode.ACTION_CANCEL);
                }
                finish();
            }
        });

        boolean chooseMode = getIntent().getBooleanExtra(ProtocolConstants.IntentParams.ADAPTER_CHOOSE_MODEL, false);
        int currentCheckId = getIntent().getIntExtra(ProtocolConstants.IntentParams.ADDRESS_ID, -1);
        defaultAdapter = new AddressListAdapter(this, chooseMode);
        defaultAdapter.setCurrentCheckId(currentCheckId);
        mBinding.dataRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.dataRv.setHasFixedSize(true);
        mBinding.dataRv.setAdapter(defaultAdapter);
        mBinding.dataRv.setEmptyView(mBinding.emptyView);
        loadAddressList();

        mBinding.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (defaultAdapter.getItemCount() >= 5) {
                    BPDialogManager.showNoticeDialog(AddressListUI.this, getString(R.string.personal_center_too_many_addresses),
                            getString(R.string.personal_center_addresses_at_most_5));
                    return;
                }
                startActivity(new Intent(v.getContext(), AddNewAddressUI.class));
            }
        });
        eventObserver = new AccurateEventObserver<AddressesChangeEvent>() {
            @Override
            public void onReceiveEvent(AddressesChangeEvent event) {
                Log.d(TAG, "address data list change.");
                loadAddressList();
            }
        };
        RxEventBus.getBus().register(AddressesChangeEvent.ID, eventObserver);
    }


    private void loadAddressList() {
        List<Racecar.Address> addressesList = new ArrayList(AccountLogic.getAccountInfo().getAddressList());

        //move the default address to the first one
        Racecar.Address defaultAddress = null;
        Iterator<Racecar.Address> iterator = addressesList.iterator();
        while (iterator.hasNext()) {
            Racecar.Address address = iterator.next();
            if (address.getFlag() == 1) {
                defaultAddress = address;
                iterator.remove();
                break;
            }
        }

        if (defaultAddress != null) {
            addressesList.add(0, defaultAddress);
        }
        defaultAdapter.setData(addressesList);
        defaultAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        RxEventBus.getBus().unregister(AddressesChangeEvent.ID, eventObserver);
        super.onDestroy();
    }

}
