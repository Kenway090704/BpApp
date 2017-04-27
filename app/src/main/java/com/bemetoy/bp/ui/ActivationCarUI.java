package com.bemetoy.bp.ui;

import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.R;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.databinding.UiActivationSuccessCarBinding;
import com.bemetoy.bp.model.HomeLogic;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;

/**
 * Created by Tom on 2016/6/13.
 */
public class ActivationCarUI extends BaseDataBindingActivity<UiActivationSuccessCarBinding> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Racecar.Car car = (Racecar.Car) getIntent().getExtras().getSerializable(ProtocolConstants.IntentParams.CAR_INFO);
        final int carCount = getIntent().getExtras().getInt(ProtocolConstants.IntentParams.CAR_COUNT);
        String carName = HomeLogic.getCaName(car.getCarBaseId());
        mBinding.carNameTv.setText(getString(R.string.exchange_car, carName));
        mBinding.carCountTv.setText(getString(R.string.own_car_count, carCount, carName));
        mBinding.carIm.setImageResource(HomeLogic.getCarImage(car.getCarBaseId()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_activation_success_car;
    }
}
