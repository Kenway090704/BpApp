package com.bemetoy.bp.ui;

import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.R;
import com.bemetoy.bp.databinding.UiActivationSuccessScoreBinding;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;

/**
 * Created by Tom on 2016/6/13.
 */
public class ActivationScoreUI extends BaseDataBindingActivity<UiActivationSuccessScoreBinding> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    protected void init() {
        int score = getIntent().getExtras().getInt(ProtocolConstants.IntentParams.SCORE_INFO);
        mBinding.resultTv.setText(getString(R.string.exchange_score, score));
        mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_activation_success_score;
    }
}
