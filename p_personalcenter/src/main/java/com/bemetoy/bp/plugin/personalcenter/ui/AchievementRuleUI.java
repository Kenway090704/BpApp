package com.bemetoy.bp.plugin.personalcenter.ui;

import android.view.View;
import android.widget.ImageButton;

import com.bemetoy.bp.plugin.personalcenter.R;
import com.bemetoy.bp.uikit.BpActivity;

/**
 * Created by Tom on 2016/6/15.
 */
public class AchievementRuleUI extends BpActivity {

    private ImageButton button;

    @Override
    protected int getLayoutId() {
        return R.layout.ui_achievement_rule;
    }

    @Override
    protected void initView() {
        button = (ImageButton) this.findViewById(R.id.back_btn);
    }

    @Override
    protected void initListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
