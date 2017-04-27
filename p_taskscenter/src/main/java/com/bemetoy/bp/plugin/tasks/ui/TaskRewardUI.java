package com.bemetoy.bp.plugin.tasks.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.plugin.tasks.R;
import com.bemetoy.bp.plugin.tasks.databinding.UiGetRewardDialogBinding;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.stub.account.AccountLogic;

/**
 * Created by Tom on 2016/8/20.
 */
public class TaskRewardUI extends BaseDataBindingActivity<UiGetRewardDialogBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        int exp = intent.getIntExtra(ProtocolConstants.IntentParams.TASK_EXP, 0);
        int score = intent.getIntExtra(ProtocolConstants.IntentParams.TASK_SCORE, 0);

        String text = "";
        if (exp != 0 && score != 0) {
            text = this.getString(R.string.tasks_center_get_reward, score, exp);
        } else if(exp == 0) {
            text = this.getString(R.string.tasks_center_get_score, score);
        } else {
            text = this.getString(R.string.tasks_center_get_popularity, exp);
        }
        mBinding.resultTv.setText(text);
        mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountLogic.updateAccountFromSVR();
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_get_reward_dialog;
    }
}
