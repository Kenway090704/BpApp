package com.bemetoy.bp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bemetoy.bp.R;
import com.bemetoy.bp.databinding.UiRegisterDialogBinding;
import com.bemetoy.bp.ui.AccountRegisterFragment;
import com.bemetoy.bp.ui.DefaultFragmentAdapter;
import com.bemetoy.bp.ui.QuickRegisterFragment;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/5/13.
 */
public class RegisterUINEW extends BaseDataBindingActivity<UiRegisterDialogBinding> {

    private static final String TAG  = "App.RegisterUINEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        mBinding.registerMethod.setViewPager(mBinding.registerVw);

        List<Fragment> fragmentList = new ArrayList();
        fragmentList.add(new AccountRegisterFragment());
        fragmentList.add(new QuickRegisterFragment());

        DefaultFragmentAdapter defaultFragmentAdapter = new DefaultFragmentAdapter(getSupportFragmentManager(), fragmentList);
        mBinding.registerVw.setAdapter(defaultFragmentAdapter);
        mBinding.registerVw.setOffscreenPageLimit(2);

        mBinding.loginNowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUINEW.this, AuthUINew.class));
                finish();
            }
        });

        mBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ui_register_dialog;
    }
}
