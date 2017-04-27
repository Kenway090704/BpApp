package com.bemetoy.bp.plugin.personalcenter.ui;

import android.os.Bundle;
import android.view.View;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.plugin.personalcenter.databinding.UiAchievementDetailInfoBinding;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;

/**
 * Created by albieliang on 16/4/27.
 */
public class AchievementDetailInfoUI extends BaseDataBindingActivity<UiAchievementDetailInfoBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void init() {
        Racecar.Achieve achieve = (Racecar.Achieve) getIntent().getExtras().getSerializable(ProtocolConstants.IntentParams.ACHIEVE_INFO);
        if(achieve != null && !achieve.getFinished()) {
            achieve = Racecar.Achieve.newBuilder().mergeFrom(achieve).setDesc(achieve.getDesc().replaceAll("恭喜你","可")).build();
        }
        mBinding.setAchieve(achieve);
        mBinding.comfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_achievement_detail_info;
    }
}
