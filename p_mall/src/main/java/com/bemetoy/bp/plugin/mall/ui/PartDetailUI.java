package com.bemetoy.bp.plugin.mall.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.events.AccountUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.ExchangePresentImpl;
import com.bemetoy.bp.plugin.mall.MallContract;
import com.bemetoy.bp.plugin.mall.R;
import com.bemetoy.bp.plugin.mall.databinding.UiPartDetailBinding;
import com.bemetoy.bp.plugin.mall.model.MallDataSourceRemote;
import com.bemetoy.bp.plugin.mall.model.MallLogic;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.app.base.BaseViewActivityImpl;
import com.bemetoy.stub.ui.BPDialogManager;

import java.util.List;

import static com.bemetoy.bp.autogen.protocol.Racecar.AccountInfo;
import static com.bemetoy.bp.autogen.protocol.Racecar.Address;
import static com.bemetoy.bp.autogen.protocol.Racecar.GoodsListResponse;

/**
 * Created by Tom on 2016/4/25.
 */
public class PartDetailUI extends BaseViewActivityImpl<UiPartDetailBinding> implements MallContract.ExchangeView {

    private static final String TAG = "Mall.PartDetailUI";
    private EventObserver observer;

    private GoodsListResponse.Item mPart;
    private MallContract.ExchangePresent present;
    private Address mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {

        observer = new AccurateEventObserver<AccountUpdateEvent>() {
            @Override
            public void onReceiveEvent(AccountUpdateEvent event) {
                mBinding.setScore(AccountLogic.getAccountInfo().getScore());
            }
        };
        RxEventBus.getBus().register(AccountUpdateEvent.ID, observer);
        present = new ExchangePresentImpl(MallDataSourceRemote.getInstance(), this);
        mPart = (GoodsListResponse.Item) getIntent().getExtras().getSerializable(ProtocolConstants.IntentParams.PART_ITEM);
        mBinding.setPart(mPart);
        float scale  = 3.3f;
        final float[] data = new float[]{
                mPart.getParameter().getFlexibility() * scale,
                mPart.getParameter().getStability() * scale,
                mPart.getParameter().getWeight() * scale,
                mPart.getParameter().getRotateSpeed() * scale,
                mPart.getParameter().getOutputPower() * scale,
                mPart.getParameter().getTorsion() * scale,
                mPart.getParameter().getStamina() * scale,
                mPart.getParameter().getRoadHolding() * scale
        };

        mBinding.partInfoRc.setData(data);
        mBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(Util.isNullOrBlank(mPart.getDetailText())) {
            mBinding.partInfoRc.setVisibility(View.VISIBLE);
            mBinding.exchangeDescTv.setVisibility(View.INVISIBLE);
        }

        mBinding.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo accountInfo = AccountLogic.getAccountInfo();
                if (accountInfo.getScore() < mPart.getScore()) {
                    MallLogic.showDoTaskDialog(PartDetailUI.this);
                } else {
                    mAddress =  AccountLogic.getDefaultAddress();
                    MallLogic.showExchangeConfirmDialog(PartDetailUI.this, AccountLogic.getDefaultAddress(), mPart, present);
                }
            }
        });

        AccountInfo accountInfo = AccountLogic.getAccountInfo();
        mBinding.setScore(accountInfo.getScore());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_part_detail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ProtocolConstants.RequestCode.ACTION_ADD_NEW_ADDRESS) {
            if (resultCode == ProtocolConstants.ResultCode.ACTION_DONE) {
                Racecar.Address address = (Address) data.getSerializableExtra(ProtocolConstants.IntentParams.ADDRESS_INFO);
                showAddressChooseDialog(address);
                return;
            }

            if(resultCode == ProtocolConstants.ResultCode.ACTION_CANCEL) {
                MallLogic.showExchangeConfirmDialog(PartDetailUI.this, null, mPart, present);
                return;
            }
        }

        if(requestCode == ProtocolConstants.RequestCode.ACTION_CHOOSE_ADDRESS) {
            /**
             * when user go to choose address ui and delete all addresses.
             */
            if (resultCode == ProtocolConstants.ResultCode.ACTION_GO_BACK) {
                mAddress = null;
                MallLogic.showExchangeConfirmDialog(PartDetailUI.this, mAddress, mPart, present);
                return;
            }

            if (resultCode == ProtocolConstants.ResultCode.ACTION_CANCEL) {
                if(mAddress != null) {
                    /**
                     * 因为用户有可能在编辑或者删除了收货地址之后按返回键，这个时候需要更新当前address
                     */
                    showAddressChooseDialog(getAddress(mAddress.getId()));
                }
                return;
            }

            if(resultCode == ProtocolConstants.ResultCode.ACTION_DONE) {
                Racecar.Address address = (Address) data.getSerializableExtra(ProtocolConstants.IntentParams.ADDRESS_INFO);
                showAddressChooseDialog(address);
            }
        }
    }

    /**
     * Get the address by id. if the address not exist return the default address.
     * @param id
     * @return
     */
    private Address getAddress(int id) {
        Address address = null;
        Address defaultAddress = null;
        List<Address> addressList = AccountLogic.getAccountInfo().getAddressList();
        for(Address add : addressList) {
            if(add.getId() == id) {
                address = add;
            }

            if(add.getFlag() == 1) {
                defaultAddress = add;
            }
        }
        return address == null ? defaultAddress : address;
    }

    @Override
    public void showExchangeResult(int result, String message, int score) {
        if(result == MallContract.ExchangeView.RESULT_OK) {
            Intent intent = new Intent(this, ExchangeResultUI.class);
            intent.putExtra(ProtocolConstants.IntentParams.PART_ITEM, mPart);
            startActivity(intent);
            finish();
            AccountLogic.updateAccountInfo(StorageConstants.Info_Key.SCORE, score);
        } else {
            BPDialogManager.showSorryDialog(this, message);
        }
    }

    @Override
    public void showAddressChooseDialog(Address address) {
        if(address != null) {
            mAddress = address;
            MallLogic.showAddressChooseDialog(this, address, mPart, present);
        }
    }

    @Override
    protected void onDestroy() {
        RxEventBus.getBus().unregister(AccountUpdateEvent.ID, observer);
        super.onDestroy();
    }

    @Override
    protected Runnable getRetryAction() {
        return new Runnable() {
            @Override
            public void run() {
                present.doExchange(AccountLogic.getDefaultAddress().getId(), mPart.getGoodsId());
            }
        };
    }
}
